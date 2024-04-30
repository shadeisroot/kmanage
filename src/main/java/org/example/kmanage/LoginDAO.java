package org.example.kmanage;

public interface LoginDAO {
    User checkLogin(String username, String password);
}
