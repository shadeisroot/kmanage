package org.example.kmanage;

public class User {
    private String username;
    private Role role;
    private Profile profile;

    public User(String username, Role role, Profile profile) {
        this.username = username;
        this.role = role;
        this.profile = profile;
    }
}
