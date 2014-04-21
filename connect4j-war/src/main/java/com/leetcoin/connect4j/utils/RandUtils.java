package com.leetcoin.connect4j.utils;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: liguangxia
 * Date: 21/4/14
 * Time: 3:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class RandUtils {

    private static final Random random = new Random();

    public static int nextInt() {
        return random.nextInt();
    }

}
