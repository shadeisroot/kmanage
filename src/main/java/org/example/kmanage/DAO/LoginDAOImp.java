package org.example.kmanage.DAO;

import org.example.kmanage.User.Permissions;
import org.example.kmanage.User.Profile;
import org.example.kmanage.User.User;

import java.sql.*;

public class LoginDAOImp implements LoginDAO{


    private Connection con;

    public LoginDAOImp() {
        try {
            con = DriverManager.getConnection("jdbc:sqlserver://10.176.111.34:1433;database=Kmanager2023;userName=CSe2023t_t_2;password=CSe2023tT2#23;encrypt=true;trustServerCertificate=true");

        } catch (SQLException e) {
            System.out.println("Cant connect to Database" + e);
        }
    }

    public User checkLogin(String username, String password) {
        try {
            String sql = "SELECT * from dbo.Login " +
                    "join dbo.Permissions P on P.id = Login.pid " +
                    "join dbo.employee e on e.userid = Login.uid " +
                    "WHERE Username = ? AND Password = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
            }
            User user = new User(rs.getString("Username"), new Permissions(rs.getInt("id"),
                    rs.getString("name")), new Profile(rs.getString("Navn"), rs.getString("Stilling"),
                    rs.getString("Afdeling")));
            return user;
        } catch (SQLException e) {
            System.out.println("Error" + e);
        }
        return null;
    }


}
