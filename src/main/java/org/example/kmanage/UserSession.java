package org.example.kmanage;

public class UserSession {

    private static UserSession instance;

    private User user;

    private UserSession(User user) {
        this.user = user;
    }

    public static UserSession getInstance(User user) {
        if(instance == null) {
            instance = new UserSession(user);
        }
        return instance;
    }

    public User getUser() {
        return user;
    }
}