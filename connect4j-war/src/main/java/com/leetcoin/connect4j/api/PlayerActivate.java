package com.leetcoin.connect4j.api;

import com.alibaba.fastjson.JSONObject;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.urlfetch.*;
import com.leetcoin.connect4j.GameUpdater;
import com.leetcoin.connect4j.config.Configs;
import com.leetcoin.connect4j.utils.HMacSHA512Utils;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withPayload;

/**
 * Created with IntelliJ IDEA.
 * User: liguangxia
 * Date: 21/4/14
 * Time: 5:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerActivate {

    private static final Logger log = Logger.getLogger(PlayerActivate.class.getName());

    private static final String URI = "/api/activate_player";

    /**
     * check to see if a user has authorized play yet.
     * @param userStr
     * @param gameKey
     * @param playerId
     * @param count
     * @return
     * @throws IOException
     */
    public static boolean checkAuthorization(final String userStr, final String gameKey, final String playerId, final int count) throws IOException {
        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        log.info("check_authorization");

        final Key key = KeyFactory.createKey("Game", "game_key");
        final Query query = new Query("Game", key);
        query.setFilter(new Query.FilterPredicate(
                "game_key", Query.FilterOperator.EQUAL, gameKey
        ));

        // get the game entity
        final Entity game = datastore.prepare(query).asSingleEntity();
        if(game == null) {
            return false;
        }

        final JSONObject activeRet = activatePlayer(userStr,
                (String)game.getProperty("server_secret"), (String)game.getProperty("server_api_key"));

        final boolean playerAuthorized = activeRet.getBoolean("player_authorized");
        final String leetcoinKey = activeRet.getString("player_platformid");

        log.info("deferred");
        log.info("playerAuthorized: "+playerAuthorized);

        if(playerAuthorized) {
            log.info("player authorized");

            // which player was it?
            if("X".equals(playerId)) {
                game.setProperty("userXleetcoinKey", leetcoinKey);
            } else {
                game.setProperty("userOleetcoinKey", leetcoinKey);
            }

            datastore.put(game);

            new GameUpdater(game).sendUpdate();
        } else {
            log.info("player not authorized");
            log.info("count: "+count);

            // requeue a deferred
            if(count < 9) {
                QueueFactory.getDefaultQueue().add(withPayload(new DeferredTask() {
                    @Override
                    public void run() {
                        try {
                            checkAuthorization(userStr, gameKey, playerId, count + 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }));
            }
        }

        return true;
    }

    /**
     * send the player to the api server
     * @param platformId
     * @param serverSecret
     * @param serverApiKey
     * @return
     * @throws IOException
     */
    public static JSONObject activatePlayer(final String platformId, final String serverSecret, final String serverApiKey) throws IOException {
        log.info("platformId: "+platformId);
        log.info("serverSecret: "+serverSecret);
        log.info("serverApiKey: "+serverApiKey);

        final String time = PythonUtils.getNanoTime();
        final String params = "nonce="+time+"&platformid="+platformId;
        log.info("params: "+params);

        // Hash the params string to produce the Sign header value
        final String sign = HMacSHA512Utils.encrypt(params);
        log.info("sign: "+sign);

        final URLFetchService urlFetchService = URLFetchServiceFactory.getURLFetchService();
        final URL url = new URL((Configs.isLocalTest() ? "http://" : "https://"
        )+ Configs.getUrl()+URI);
        final HTTPRequest httpRequest = new HTTPRequest(url, HTTPMethod.POST);
        httpRequest.setHeader(new HTTPHeader("Content-type", "application/x-www-form-urlencoded"));
        httpRequest.setHeader(new HTTPHeader("Key", Configs.getApiKey()));
        httpRequest.setHeader(new HTTPHeader("Sign", sign));

        httpRequest.setPayload(params.getBytes());
        final HTTPResponse httpResponse = urlFetchService.fetch(httpRequest);

        return JSONObject.parseObject(new String(httpResponse.getContent()));
    }

}
