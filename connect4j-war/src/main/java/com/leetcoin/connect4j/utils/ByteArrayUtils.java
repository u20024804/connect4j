package com.leetcoin.connect4j.utils;

/**
 * Created with IntelliJ IDEA.
 * User: liguangxia
 * Date: 21/4/14
 * Time: 4:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class ByteArrayUtils {

    private static final int DEFAULT_LENGTH = 32;

    public static final String toHexString(final byte[] bytes) {
        final StringBuilder sb = new StringBuilder(DEFAULT_LENGTH);

        for(int i = 0; i < bytes.length; i++) {
            final short b = byteToShort(bytes[i]);

            if(b < 0x10) {
                sb.append('0');
            }

            sb.append(Integer.toString(b, 0x10));
        }

        return sb.toString();
    }

    private static final short byteToShort(byte b) {
        return (b < 0)? (short)(256 + b) : (short)b;
    }

}
