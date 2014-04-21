package com.leetcoin.connect4j.servlet;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
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
 * Time: 2:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class MoveServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final UserService userService = UserServiceFactory.getUserService();

        final Entity game = new GameFromRequest(req).getGame();
        final User user = userService.getCurrentUser();

        if(user != null && game != null) {
            final int i = Integer.parseInt(req.getParameter("i"));
            new GameUpdater(game).makeMove(i, user);
        }
    }

}
