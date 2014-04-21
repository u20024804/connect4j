package com.leetcoin.connect4j.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.appengine.api.log.LogServiceFactory;
import com.google.appengine.api.urlfetch.*;
import com.leetcoin.connect4j.config.Configs;
import com.leetcoin.connect4j.utils.HMacSHA512Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: liguangxia
 * Date: 21/4/14
 * Time: 3:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServerCreate {

    private static final Logger log = Logger.getLogger(ServerCreate.class.getName());

    private static final String URI = "/api/server_create";

    public static JSONObject createServer(final String hostAddress, final String hostConnectionLink, final String title) throws IOException {
        final String developerApiKey = Configs.getDeveloperApiKey();

        final Server server = new Server(
                title,                                          // title,
                hostAddress,                                  // hostAddress,
                "80",                                        // hostPort,
                hostConnectionLink,                            // hostConnectionLink,
                Configs.getGameKey(),                                       // gameKey,
                2,                                            // maxActivePlayers,
                2,                                             // maxAuthorizedPlayers,
                10000,                                          // minimumBTCHold,
                1000,                                           // incrementBTC,
                0.01f,                                           // serverRakeBTCPercentage,
                "",                                           // serverAdminUserKey,
                0.01f,                                           // leetcoinRakePercentage,
                false,                                          // allowNonAuthorizedPlayers,
                "LOW",                                         // stakesClass,
                false,                                          // motdShowBanner,
                "F00",                                          // motdBannerColor,
                "leetcoin-tac-toe"                                  // motdBannerText
        );

        final String serverJson = server.toMinJSON();
        final String encodedServerJson = URLEncoder.encode(serverJson, "UTF8");
        final String nonce = PythonUtils.getNanoTime();
        final String params = "nonce="+nonce+"&server="+encodedServerJson;

        // Hash the params string to produce the Sign header value
        final String sign = HMacSHA512Utils.encrypt(params);

        log.info("Sign: "+ sign);
        log.info("nonce: "+ nonce);
        log.info("developer_api_key: " + developerApiKey);

        final URLFetchService urlFetchService = URLFetchServiceFactory.getURLFetchService();
        final URL url = new URL((Configs.isLocalTest() ? "http://" : "https://"
                                )+ Configs.getUrl()+URI);
        final HTTPRequest httpRequest = new HTTPRequest(url, HTTPMethod.POST);
        httpRequest.setHeader(new HTTPHeader("Content-type", "application/x-www-form-urlencoded"));
        httpRequest.setHeader(new HTTPHeader("Key", Configs.getApiKey()));
        httpRequest.setHeader(new HTTPHeader("Sign", sign));

        httpRequest.setPayload(params.getBytes());
        final HTTPResponse httpResponse = urlFetchService.fetch(httpRequest);

        final String responseBody = new String(httpResponse.getContent());
        final JSONObject jsonResponse = JSONObject.parseObject(responseBody);
        return jsonResponse;
    }

}
