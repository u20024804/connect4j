package com.leetcoin.connect4j.api;

import com.alibaba.fastjson.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: liguangxia
 * Date: 21/4/14
 * Time: 3:55 PM
 * To change this template use File | Settings | File Templates.
 *
 * a leetcoin server
 *
 */
public class Server {

    private String title;
    private String hostAddress;
    private String hostPort;
    private String hostConnectionLink;
    private String gameKey;
    private int maxActivePlayers;
    private int maxAuthorizedPlayers;
    private int minimumBTCHold;
    private int incrementBTC;
    private float serverRakeBTCPercentage;
    private String serverAdminUserKey;
    private float leetcoinRakePercentage;
    private boolean allowNonAuthorizedPlayers;
    private String stakesClass;
    private boolean motdShowBanner;
    private String motdBannerColor;
    private String motdBannerText;

    public Server() {
    }

    public String toJSONString() {
        return JSONObject.toJSONString(this);
    }

    public String toMinJSON() {
        final JSONObject json = new JSONObject();
        json.put("gameKey", gameKey);
        json.put("hostConnectionLink", hostConnectionLink);
        json.put("title", title);

        return json.toJSONString();
    }

    public String toTinyJSON() {
        final JSONObject json = new JSONObject();
        json.put("gameKey", gameKey);

        return json.toJSONString();
    }

    public Server(String title, String hostAddress, String hostPort, String hostConnectionLink, String gameKey, int maxActivePlayers, int maxAuthorizedPlayers, int minimumBTCHold, int incrementBTC, float
            serverRakeBTCPercentage, String serverAdminUserKey, float leetcoinRakePercentage, boolean allowNonAuthorizedPlayers, String stakesClass, boolean motdShowBanner, String motdBannerColor, String motdBannerText) {
        this.title = title;
        this.hostAddress = hostAddress;
        this.hostPort = hostPort;
        this.hostConnectionLink = hostConnectionLink;
        this.gameKey = gameKey;
        this.maxActivePlayers = maxActivePlayers;
        this.maxAuthorizedPlayers = maxAuthorizedPlayers;
        this.minimumBTCHold = minimumBTCHold;
        this.incrementBTC = incrementBTC;
        this.serverRakeBTCPercentage = serverRakeBTCPercentage;
        this.serverAdminUserKey = serverAdminUserKey;
        this.leetcoinRakePercentage = leetcoinRakePercentage;
        this.allowNonAuthorizedPlayers = allowNonAuthorizedPlayers;
        this.stakesClass = stakesClass;
        this.motdShowBanner = motdShowBanner;
        this.motdBannerColor = motdBannerColor;
        this.motdBannerText = motdBannerText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public String getHostPort() {
        return hostPort;
    }

    public void setHostPort(String hostPort) {
        this.hostPort = hostPort;
    }

    public String getHostConnectionLink() {
        return hostConnectionLink;
    }

    public void setHostConnectionLink(String hostConnectionLink) {
        this.hostConnectionLink = hostConnectionLink;
    }

    public String getGameKey() {
        return gameKey;
    }

    public void setGameKey(String gameKey) {
        this.gameKey = gameKey;
    }

    public int getMaxActivePlayers() {
        return maxActivePlayers;
    }

    public void setMaxActivePlayers(int maxActivePlayers) {
        this.maxActivePlayers = maxActivePlayers;
    }

    public int getMaxAuthorizedPlayers() {
        return maxAuthorizedPlayers;
    }

    public void setMaxAuthorizedPlayers(int maxAuthorizedPlayers) {
        this.maxAuthorizedPlayers = maxAuthorizedPlayers;
    }

    public int getMinimumBTCHold() {
        return minimumBTCHold;
    }

    public void setMinimumBTCHold(int minimumBTCHold) {
        this.minimumBTCHold = minimumBTCHold;
    }

    public int getIncrementBTC() {
        return incrementBTC;
    }

    public void setIncrementBTC(int incrementBTC) {
        this.incrementBTC = incrementBTC;
    }

    public float getServerRakeBTCPercentage() {
        return serverRakeBTCPercentage;
    }

    public void setServerRakeBTCPercentage(float serverRakeBTCPercentage) {
        this.serverRakeBTCPercentage = serverRakeBTCPercentage;
    }

    public String getServerAdminUserKey() {
        return serverAdminUserKey;
    }

    public void setServerAdminUserKey(String serverAdminUserKey) {
        this.serverAdminUserKey = serverAdminUserKey;
    }

    public float getLeetcoinRakePercentage() {
        return leetcoinRakePercentage;
    }

    public void setLeetcoinRakePercentage(float leetcoinRakePercentage) {
        this.leetcoinRakePercentage = leetcoinRakePercentage;
    }

    public boolean isAllowNonAuthorizedPlayers() {
        return allowNonAuthorizedPlayers;
    }

    public void setAllowNonAuthorizedPlayers(boolean allowNonAuthorizedPlayers) {
        this.allowNonAuthorizedPlayers = allowNonAuthorizedPlayers;
    }

    public String getStakesClass() {
        return stakesClass;
    }

    public void setStakesClass(String stakesClass) {
        this.stakesClass = stakesClass;
    }

    public boolean getMotdShowBanner() {
        return motdShowBanner;
    }

    public void setMotdShowBanner(boolean motdShowBanner) {
        this.motdShowBanner = motdShowBanner;
    }

    public String getMotdBannerColor() {
        return motdBannerColor;
    }

    public void setMotdBannerColor(String motdBannerColor) {
        this.motdBannerColor = motdBannerColor;
    }

    public String getMotdBannerText() {
        return motdBannerText;
    }

    public void setMotdBannerText(String motdBannerText) {
        this.motdBannerText = motdBannerText;
    }

}
