package com.leetcoin.connect4j;

import com.alibaba.fastjson.JSONArray;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.urlfetch.*;
import com.leetcoin.connect4j.api.Player;
import com.leetcoin.connect4j.api.PythonUtils;
import com.leetcoin.connect4j.config.Configs;
import com.leetcoin.connect4j.utils.HMacSHA512Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: liguangxia
 * Date: 21/4/14
 * Time: 5:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class MatchResults {

    private static final String URI = "/api/put_match_results";

    private static final Logger log = Logger.getLogger(MatchResults.class.getName());

    public static String setMatchResults(final Entity game, final String mapTitle,
                                  final String playerKeys[], final String playerNames[],
                                  final String weapons[], final int kills[],
                                  final int deaths[], final int ranks[]) throws IOException {

        log.info("playerKeys: "+playerKeys);
        log.info("playerNames: "+playerNames);
        log.info("weapons: "+weapons);
        log.info("kills: "+kills);
        log.info("deaths: "+deaths);
        log.info("ranks: "+ranks);
        log.info("mapTitle: "+mapTitle);

        final List<Player> playerList = new ArrayList<Player>();

        for(int i = 0; i < playerKeys.length; i++) {
            final String playerKey = playerKeys[i];
            final Player player = new Player(playerKey, kills[i], deaths[i], playerNames[i],
                                    ranks[i], weapons[i]);
        }

        final String nonce = PythonUtils.getNanoTime();
        final String playerListJson = JSONArray.toJSONString(playerList);
        final String encodedPlayerListJson = URLEncoder.encode(playerListJson, "UTF8");
        final String encodedTitle = URLEncoder.encode(mapTitle, "UTF8");
        final String params = "map_title="+encodedTitle+"&nonce="+nonce+"&player_dict_list="+encodedPlayerListJson;

        final String sign = HMacSHA512Utils.encrypt(params);
        log.info("params: "+params);
        log.info("game.server_secret: "+game.getProperty("server_secret"));
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

        return new String(httpResponse.getContent());
    }

}
