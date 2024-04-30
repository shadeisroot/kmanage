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

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", role=" + role +
                ", profile=" + profile +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
