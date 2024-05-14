package org.example.kmanage.DAO;

import java.sql.SQLException;

public interface EditprofileDAO {
    void editprofile(String Navn, String Stilling, String Afdeling, int userid) throws SQLException;
}
