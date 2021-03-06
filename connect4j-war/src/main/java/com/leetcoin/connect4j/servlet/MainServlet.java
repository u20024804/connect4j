package com.leetcoin.connect4j.servlet;

import com.alibaba.fastjson.JSONObject;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.channel.ChannelServicePb;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.leetcoin.connect4j.GameUpdater;
import com.leetcoin.connect4j.api.PlayerActivate;
import com.leetcoin.connect4j.api.ServerCreate;
import com.leetcoin.connect4j.utils.RandUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withPayload;

/**
 * Created with IntelliJ IDEA.
 * User: liguangxia
 * Date: 21/4/14
 * Time: 2:21 AM
 * To change this template use File | Settings | File Templates.
 *
 * The main UI page, renders the 'index.html' template.
 *
 */
public class MainServlet extends HttpServlet {

    private static final String DEFAULT_BOARD = "                                                                " ;

    /**
     * Renders the main page. When this page is shown, we create a new
     * channel to push asynchronous updates to the client.
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final UserService userService = UserServiceFactory.getUserService();
        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final ChannelService channelService = ChannelServiceFactory.getChannelService();
        final User user = userService.getCurrentUser();

        final String gameLink;
        String gameKey = req.getParameter("g");
        Entity game = null;

        if(user != null) {
            if(gameKey == null || gameKey.trim().isEmpty()) {
                gameKey = user.getUserId() + "_" + System.currentTimeMillis() + "_" + RandUtils.nextInt();
                gameLink = req.getRequestURL()+"?g=" + gameKey;
                final String title = user+"s game";
                final JSONObject jsonObject = ServerCreate.createServer(req.getRemoteHost(), gameLink, title);
                final String serverApiKey = jsonObject.getString("server_api_key");
                final String serverSecret = jsonObject.getString("server_secret");
                final String serverKey = jsonObject.getString("server_key");

                final Key key = KeyFactory.createKey("Game", "game_key");

                game = new Entity("Game", key);
                game.setProperty("game_key", gameKey);
                game.setProperty("userX", user.getUserId());
                game.setProperty("moveX", true);
                game.setProperty("board", DEFAULT_BOARD);
                game.setProperty("server_api_key", serverApiKey);
                game.setProperty("server_secret", serverSecret);
                game.setProperty("server_key", serverKey);

                datastore.put(game);

                final String game_key = gameKey;
                QueueFactory.getDefaultQueue().add(withPayload(new DeferredTask() {
                    @Override
                    public void run() {
                        try {
                            PlayerActivate.checkAuthorization(user.getUserId(), game_key, "X", 10);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }));
            } else {
                gameLink = req.getRequestURL()+"?g=" + gameKey;
                final Key key = KeyFactory.createKey("Game", "game_key");
                final Query query = new Query("Game");
                query.setFilter(new Query.FilterPredicate(
                        "game_key", Query.FilterOperator.EQUAL, gameKey));
                game = datastore.prepare(query).asSingleEntity();

                final String userO = (String) game.getProperty("userO");
                if(userO == null) {
                    game.setProperty("userO", user.getUserId());
                    datastore.put(game);
                }

                // Fire off a task to check to see if this user has authorized play on leetcoin.com
                final String game_key = gameKey;
                QueueFactory.getDefaultQueue().add(withPayload(new DeferredTask() {
                    @Override
                    public void run() {
                        try {
                            PlayerActivate.checkAuthorization(user.getUserId(), game_key, "O", 0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }));
            }

            if(game != null) {

                final String token = channelService.createChannel(user.getUserId()+"_"+gameKey);
                channelService.sendMessage(new ChannelMessage(user.getUserId()+"_"+gameKey, "f*ck"));
                req.setAttribute("token", token);
                req.setAttribute("me", user.getUserId());
                req.setAttribute("game_key", gameKey);
                req.setAttribute("game_link", gameLink);
                req.setAttribute("initial_message", new GameUpdater(game).getGameMessage());
                req.setAttribute("my_google_user", user.toString());

                final RequestDispatcher dispatcher = req.getRequestDispatcher("index.jsp");
                dispatcher.forward(req, resp);
            } else {
                resp.getWriter().println("No such game");
            }
        } else {
            resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
        }
    }

}
