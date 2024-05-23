package org.example.kmanage.User;

public class UserSession {
    //statisk variabel, der holder den eneste instans af UserSession (singleton mønster)
    private static UserSession instance;

    private User user;
    //constructor
    private UserSession(User user) {
        this.user = user;
    }
    //får instans af userssion
    public static UserSession getInstance(User user) {
        if(instance == null) {
            instance = new UserSession(user);
        }
        return instance;
    }
    //get
    public User getUser() {
        return user;
    }
    //rydder user session
    public void cleanUserSession() {
        user = null;
        instance = null;
    }
}