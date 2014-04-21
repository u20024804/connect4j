package com.leetcoin.connect4j.servlet;

import com.google.appengine.api.datastore.Entity;
import com.leetcoin.connect4j.GameFromRequest;
import com.leetcoin.connect4j.GameUpdater;

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
public class OpenedServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final Entity game = new GameFromRequest(req).getGame();
        new GameUpdater(game).sendUpdate();
    }

}
