package org.example.kmanage.User;

public class User {
    //felter
    private String username;
    private Permissions permissions;
    private Profile profile;
    //constructor
    public User(String username, Permissions permissions, Profile profile) {
        this.username = username;
        this.permissions = permissions;
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", Permissions=" + permissions +
                ", profile=" + profile +
                '}';
    }
    //gets n sets
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
