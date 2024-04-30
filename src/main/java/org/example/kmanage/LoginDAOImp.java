package org.example.kmanage;

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

    public String checkLogin(String username, String password) {
        try {
            String sql = "SELECT * FROM Login WHERE Username = ? AND Password = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("Username"); // return the username if login is successful
            }
        } catch (SQLException e) {
            System.out.println("Error" + e);
        }
        return null; // return null if login is unsuccessful
    }


}
