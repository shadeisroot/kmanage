package org.example.kmanage.DAO;

import java.sql.*;

public class EditprofileDAOImp implements EditprofileDAO {

    private Connection con;

    public EditprofileDAOImp() {
        try {
            con = DriverManager.getConnection("jdbc:sqlserver://10.176.111.34:1433;database=Kmanager2023;userName=CSe2023t_t_2;password=CSe2023tT2#23;encrypt=true;trustServerCertificate=true");

        } catch (SQLException e) {
            System.out.println("Cant connect to Database" + e);
        }
    }

    public void editprofile(String Navn, String Stilling, String Afdeling, int userid) throws SQLException {
        String sql = "UPDATE dbo.employee SET Navn = ?, Stilling = ?, Afdeling = ? WHERE userid = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, Navn);
        ps.setString(2, Stilling);
        ps.setString(3, Afdeling);
        ps.setInt(4, userid);
        ps.executeUpdate();
    }

    }
