package org.example.kmanage.DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.kmanage.User.Profile;

import java.sql.*;

public class PlistDAOimp implements PlistDAO {

    private Connection con;

    public PlistDAOimp() {
        try {
            con = DriverManager.getConnection("jdbc:sqlserver://10.176.111.34:1433;database=Kmanager2023;userName=CSe2023t_t_2;password=CSe2023tT2#23;encrypt=true;trustServerCertificate=true");

        } catch (SQLException e) {
            System.out.println("Cant connect to Database" + e);
        }
    }
        public ObservableList<Profile> getprofile() {
            ObservableList<Profile> profiles = FXCollections.observableArrayList();
            try {
                String sql = "SELECT * from dbo.employee";
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Profile profile = new Profile(rs.getString("Navn"), rs.getString("Stilling"), rs.getString("Afdeling"));
                    profiles.add(profile);
                }
            } catch (SQLException e) {
                System.out.println("Error" + e);
            }
            return profiles;

        }

    }
