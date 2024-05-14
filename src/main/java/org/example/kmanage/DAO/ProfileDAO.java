package org.example.kmanage.DAO;

import java.sql.SQLException;

public interface ProfileDAO {
    void editprofile(String Navn, String Stilling, String Afdeling, int userid) throws SQLException;



    void addEmployee(String Navn, String Stilling, String Afdeling) throws SQLException;

    void createLogin( String username, String password, int pid, int uid) throws SQLException;

    int getUserid(String Navn, String Stilling, String Afdeling) throws SQLException;
}
