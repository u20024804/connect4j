package com.leetcoin.connect4j;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: liguangxia
 * Date: 21/4/14
 * Time: 6:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class GameFromRequest {

    private final HttpServletRequest request;
    private final Entity game;

    public GameFromRequest(final HttpServletRequest request) {
        this.request = request;

        final UserService userService = UserServiceFactory.getUserService();
        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        final String gameKey = request.getParameter("g");
        final User user = userService.getCurrentUser();

        if(user != null && gameKey != null && !gameKey.trim().isEmpty()) {
            final Key key = KeyFactory.createKey("Game", "game_key");
            final Query query = new Query("Game", key);
            query.setFilter(new Query.FilterPredicate(
                    "game_key", Query.FilterOperator.EQUAL, gameKey
            ));

            this.game = datastore.prepare(query).asSingleEntity();

        } else {
            this.game = null;
        }
    }

    public Entity getGame() {
        return game;
    }

}
