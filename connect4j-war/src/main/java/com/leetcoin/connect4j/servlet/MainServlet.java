package com.leetcoin.connect4j.servlet;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.channel.ChannelServicePb;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.leetcoin.connect4j.GameUpdater;
import com.leetcoin.connect4j.utils.RandUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: liguangxia
 * Date: 21/4/14
 * Time: 2:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class MainServlet extends HttpServlet {

    private static final String DEFAULT_BOARD = "                                                                " ;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final UserService userService = UserServiceFactory.getUserService();
        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final ChannelService channelService = ChannelServiceFactory.getChannelService();
        final User user = userService.getCurrentUser();

        String gameKey = req.getParameter("g");
        Entity game = null;

        if(user != null) {
            if(gameKey == null || gameKey.trim().isEmpty()) {
                gameKey = user.getUserId() + "_" + System.currentTimeMillis() + "_" + RandUtils.nextInt();

                final Key key = KeyFactory.createKey("Game", "game_key");

                game = new Entity("Game", key);
                game.setProperty("game_key", gameKey);
                game.setProperty("userX", user.getUserId());
                game.setProperty("moveX", true);
                game.setProperty("board", DEFAULT_BOARD);

                datastore.put(game);
            } else {
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
            }

            final String gameLink = "http://localhost:8080/?g=" + gameKey;

            if(game != null) {

                final String token = channelService.createChannel(user.getUserId()+"_"+gameKey);
                channelService.sendMessage(new ChannelMessage(user.getUserId()+"_"+gameKey, "f*ck"));
                req.setAttribute("token", token);
                req.setAttribute("me", user.getUserId());
                req.setAttribute("game_key", gameKey);
                req.setAttribute("game_link", gameLink);
                req.setAttribute("initial_message", new GameUpdater(game).getGameMessage());

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
