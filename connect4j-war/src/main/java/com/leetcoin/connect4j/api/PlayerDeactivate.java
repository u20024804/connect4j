package com.leetcoin.connect4j.api;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.urlfetch.*;
import com.leetcoin.connect4j.config.Configs;
import com.leetcoin.connect4j.utils.HMacSHA512Utils;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: liguangxia
 * Date: 21/4/14
 * Time: 5:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerDeactivate {

    private static final String URI = "/api/deactivate_player";

    private static final Logger log = Logger.getLogger(PlayerDeactivate.class.getName());

    public static String deactivatePlayer(final Entity game, final String platformId, final int rank, final int satoshiBalance) throws IOException {
        log.info("platformId: "+platformId);
        log.info("rank: "+rank);
        log.info("satoshi_balance: "+satoshiBalance);

        final String nonce = PythonUtils.getNanoTime();

        final String params = "nonce="+nonce+"&platformid="+platformId;
        final String sign = HMacSHA512Utils.encrypt(params);
        log.info("params: "+params);
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
