package org.example.kmanage.DAO;

import javafx.collections.ObservableList;
import org.example.kmanage.User.Profile;

import java.sql.SQLException;
import java.util.List;

public interface ProfileDAO {
    void editprofile(String Navn, String Stilling, String Afdeling, int userid) throws SQLException;



    void addEmployee(String Navn, String Stilling, String Afdeling) throws SQLException;

    void createLogin( String username, String password, int pid, int uid) throws SQLException;

    int getUserid(String Navn, String Stilling, String Afdeling) throws SQLException;

    void removeEmployee(int userid) throws SQLException;

    ObservableList<Profile> getprofilebyid(List<Integer> ids);
}
