package org.example.kmanage.DAO;

import org.example.kmanage.User.User;

public interface LoginDAO {
    User checkLogin(String username, String password);
}
