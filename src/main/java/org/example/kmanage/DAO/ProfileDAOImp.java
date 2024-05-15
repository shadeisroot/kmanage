package org.example.kmanage.DAO;

import java.sql.*;

public class ProfileDAOImp implements ProfileDAO {

    private Connection con;

    public ProfileDAOImp() {
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

    public void addEmployee(String Navn, String Stilling, String Afdeling) throws SQLException {
        String sql = "INSERT INTO dbo.employee (Navn, Stilling, Afdeling) VALUES (?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, Navn);
        ps.setString(2, Stilling);
        ps.setString(3, Afdeling);
        ps.executeUpdate();
    }

        public int getUserid(String Navn, String Stilling, String Afdeling) throws SQLException {
            String sql = "SELECT userid FROM dbo.employee WHERE Navn = ? AND Stilling = ? AND Afdeling = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, Navn);
            ps.setString(2, Stilling);
            ps.setString(3, Afdeling);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("userid");
            } else {
                throw new SQLException("Failed to retrieve userid");
            }
        }

    public void createLogin( String username, String password, int pid, int uid) throws SQLException {
        String sql = "INSERT INTO dbo.login (username, password, pid, uid) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, password);
        ps.setInt(3, pid);
        ps.setInt(4, uid);
        ps.executeUpdate();
    }

    public void removeEmployee(int userid) throws SQLException {

        // SQL statement to delete from dbo.login
        String sqlLogin = "DELETE FROM dbo.Login WHERE uid = ?";
        PreparedStatement psLogin = con.prepareStatement(sqlLogin);
        psLogin.setInt(1, userid);
        psLogin.executeUpdate();

        // SQL statement to delete from dbo.employee
        String sqlEmployee = "DELETE FROM dbo.employee WHERE userid = ?";
        PreparedStatement psEmployee = con.prepareStatement(sqlEmployee);
        psEmployee.setInt(1, userid);
        psEmployee.executeUpdate();

    }
}
