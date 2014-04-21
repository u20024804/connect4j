package com.leetcoin.connect4j;

import com.alibaba.fastjson.JSONObject;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.leetcoin.connect4j.api.PlayerDeactivate;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: liguangxia
 * Date: 21/4/14
 * Time: 6:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class GameUpdater {

    private final Entity game;

    public GameUpdater(final Entity game) {
        this.game = game;
    }

    public void sendUpdate() {
        final ChannelService channelService = ChannelServiceFactory.getChannelService();
        final String message = getGameMessage();

        final String userO = (String) game.getProperty("userO");
        final String userX = (String) game.getProperty("userX");
        final String gameKey = (String) game.getProperty("game_key");

        channelService.sendMessage(new ChannelMessage(userX+"_"+gameKey, message));

        if(userO != null) {
            channelService.sendMessage(new ChannelMessage(userO+"_"+gameKey, message));
        }
    }

    private StringBuilder newBlankBoard() {
        final StringBuilder sb = new StringBuilder(64);
        for(int i = 0; i < 64; i++) {
            sb.append('.');
        }
        return sb;
    }

    int index(final int x, final int y) {
        return x + 8 * y;
    }

    protected String checkWin(final int index) {
        final String board = (String) game.getProperty("board");
        final boolean moveX = (boolean)(Boolean) game.getProperty("moveX");
        final char checkChar = !moveX ? 'X' : 'O';
        final char[][] grid = new char[8][8];
        for(int i = 0; i < 8; i++) {
            grid[i] = new char[8];
            for(int j = 0; j < 8; j++) {
                grid[i][j] = board.charAt(index(i, j));
            }
        }

        final int centerX = index % 8;
        final int centerY = index / 8;
        final int startX = Math.max(0, centerX - 3);
        final int endX = Math.min(7, centerX + 3);
        final int startY = Math.max(0, centerY - 3);
        final int endY = Math.min(7, centerY + 3);

        int connected = 0;

        for(int p = startX; p <= endX; p++) {
            for(int x = p; x < p + 4 && x <= endX; x++) {
                if(grid[x][centerY] != checkChar) {
                    connected = 0;
                    break;
                } else {
                    connected++;
                    if(connected >= 4) {
                        final StringBuilder winBoard = newBlankBoard();
                        for(int u = x - 3; u <= x; u++) {
                            winBoard.setCharAt(index(u, centerY), checkChar);
                        }
                        return winBoard.toString();
                    }
                }
            }
        }

        for(int q = startY; q <= endY; q++) {
            for(int y = q; y < q + 4 && y <= endY; y++) {
                if(grid[centerX][y] != checkChar) {
                    connected = 0;
                    break;
                } else {
                    connected++;
                    if(connected >= 4) {
                        final StringBuilder winBoard = newBlankBoard();
                        for(int v = y - 3; v <= y; v++) {
                            winBoard.setCharAt(index(centerX, v), checkChar);
                        }
                        return winBoard.toString();
                    }
                }
            }
        }

        for(int d = -3; d <= 3; d++) {
            final int p = centerX + d;
            final int q = centerY + d;
            if(!(p >= startX && q >= startY && p <= endX && q <= endY)) {
                continue;
            }

            for(int x = p, y = q; x < p + 4 && y < q + 4 && x <= endX && y <= endY; x++, y++) {
                if(grid[x][y] != checkChar) {
                    connected = 0;
                    break;
                } else {
                    connected++;
                    if(connected >= 4) {
                        final StringBuilder winBoard = newBlankBoard();
                        for(int u = x - 3, v = y - 3; u <= x && v <= y; u++, v++) {
                            winBoard.setCharAt(index(u, v), checkChar);
                        }
                        return winBoard.toString();
                    }
                }
            }
        }

        for(int d = -3; d <= 3; d++) {
            final int p = centerX + d;
            final int q = centerY - d;
            if(!(p >= startX && q >= startY && p <= endX && q <= endY)) {
                continue;
            }

            for(int x = p, y = q; x < p + 4 && y > q - 4 && x <= endX && y >= 0; x++, y--) {
                if(grid[x][y] != checkChar) {
                    connected = 0;
                    break;
                } else {
                    connected++;
                    if(connected >= 4) {
                        final StringBuilder winBoard = newBlankBoard();
                        for(int u = x - 3, v = y + 3; u <= x && v >= y; u++, v--) {
                            winBoard.setCharAt(index(u, v), checkChar);
                        }
                        return winBoard.toString();
                    }
                }
            }
        }

        return "";
    }

    public void makeMove(final int index, final User user) throws IOException {
        final String userO = (String) game.getProperty("userO");
        final String userX = (String) game.getProperty("userX");
        final String gameKey = (String) game.getProperty("game_key");
        final boolean moveX = (boolean)(Boolean)game.getProperty("moveX");
        final StringBuilder board = new StringBuilder((String)game.getProperty("board"));

        if(index >= 0 && user.getUserId().equals(userX) || user.getUserId().equals(userO)) {
            if(moveX == (user.getUserId().equals(userX))) {
                if(board.charAt(index) == ' ') {
                    if(moveX) {
                        board.setCharAt(index, 'X');
                    } else {
                        board.setCharAt(index, 'O');
                    }

                    game.setProperty("board", board.toString());
                    game.setProperty("moveX", !moveX);

                    final String winBoard = checkWin(index);
                    if(!"".equals(winBoard)) {
                        game.setProperty("winner", moveX ? userX : userO);
                        game.setProperty("winning_board", winBoard);

                        final String playerKeys[] = {userX, userO};
                        final String playerNames[] = {userX, userO};
                        final String weapons[] = {"X", "O"};
                        final int kills[];
                        final int deaths[];
                        final int ranks[];

                        if(moveX) {
                            kills = new int[] {1, 0};
                            deaths = new int[] {0, 1};
                            ranks = new int[] {1601, 1599};
                        } else {
                            kills = new int[] {0, 1};
                            deaths = new int[] {1, 0};
                            ranks = new int[] {1599, 1601};
                        }

                        MatchResults.setMatchResults(game, "connect4j", playerKeys,
                                playerNames, weapons, kills, deaths, ranks);

                        if(moveX) {
                            PlayerDeactivate.deactivatePlayer(game, userX, 1601, 10980);
                            PlayerDeactivate.deactivatePlayer(game, userO, 1599, 9000);
                        } else {
                            PlayerDeactivate.deactivatePlayer(game, userO, 1601, 10980);
                            PlayerDeactivate.deactivatePlayer(game, userX, 1599, 9000);
                        }
                    }

                    final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
                    datastore.put(game);

                    sendUpdate();
                }
            }
        }

    }

    public String getGameMessage() {
        final JSONObject json = new JSONObject();
        json.put("board", game.getProperty("board"));
        json.put("userX", game.getProperty("userX"));

        final String userO = (String) game.getProperty("userO");
        if(userO == null) {
            json.put("userO", null);
        } else {
            json.put("userO", userO);
        }

        json.put("moveX", game.getProperty("moveX"));
        json.put("winner", game.getProperty("winner"));
        json.put("winningBoard", game.getProperty("winning_board"));

        return json.toJSONString();
    }

}
