package com.leetcoin.connect4j.api;

/**
 * Created with IntelliJ IDEA.
 * User: liguangxia
 * Date: 21/4/14
 * Time: 5:09 PM
 * To change this template use File | Settings | File Templates.
 *
 * a leetcoin player
 *
 */
public class Player {

    private String key;
    private int kills;
    private int deaths;
    private String name;
    private int rank;
    private String weapon;

    public Player() {
    }

    public Player(String key, int kills, int deaths, String name, int rank, String weapon) {
        this.key = key;
        this.kills = kills;
        this.deaths = deaths;
        this.name = name;
        this.rank = rank;
        this.weapon = weapon;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getWeapon() {
        return weapon;
    }

    public void setWeapon(String weapon) {
        this.weapon = weapon;
    }

}
