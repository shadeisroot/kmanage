package org.example.kmanage.User;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserSessionTest {

    @Test
    void getUser() {
        User user = new User("test", new Permissions(1,"test"), new Profile("test", "test", "test", 1));
        UserSession userSession = UserSession.getInstance(user);
        assertEquals(user, userSession.getUser());
    }
}