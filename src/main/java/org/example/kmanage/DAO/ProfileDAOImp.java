package org.example.kmanage.DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.kmanage.User.Profile;

import java.sql.*;
import java.util.List;

public class ProfileDAOImp implements ProfileDAO {

    private Connection con;

    public ProfileDAOImp() {
        try {
            con = DriverManager.getConnection("jdbc:sqlserver://10.176.111.34:1433;database=Kmanager2023;userName=CSe2023t_t_2;password=CSe2023tT2#23;encrypt=true;trustServerCertificate=true");

        } catch (SQLException e) {
            System.out.println("Cant connect to Database" + e);
        }
    }

    //metode til at redigere profil i databasen
    public void editprofile(String Navn, String Stilling, String Afdeling, int userid) throws SQLException {
        String sql = "UPDATE dbo.employee SET Navn = ?, Stilling = ?, Afdeling = ? WHERE userid = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, Navn);
        ps.setString(2, Stilling);
        ps.setString(3, Afdeling);
        ps.setInt(4, userid);
        ps.executeUpdate();
    }
    //metode til at tilføje en medarbejder til databasen
    public void addEmployee(String Navn, String Stilling, String Afdeling) throws SQLException {
        String sql = "INSERT INTO dbo.employee (Navn, Stilling, Afdeling) VALUES (?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, Navn);
        ps.setString(2, Stilling);
        ps.setString(3, Afdeling);
        ps.executeUpdate();
    }
    //metode til at hente userid fra profile
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
    //metode til at lave en login til en medarbejder
    public void createLogin(String username, String password, int pid, int uid) throws SQLException {
        String sql = "INSERT INTO dbo.login (username, password, pid, uid) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, password);
        ps.setInt(3, pid);
        ps.setInt(4, uid);
        ps.executeUpdate();
    }
    //metode til at fjerne en medarbejder fra databasen
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
    //metode til at hente en liste af profiler ved hjælp af id
    public ObservableList<Profile> getprofilebyid(List<Integer> ids) {
        ObservableList<Profile> profilesbyid = FXCollections.observableArrayList();
        for (int id : ids) {
            try {
                String sql = "SELECT * from dbo.employee WHERE userid = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    Profile profile = new Profile(rs.getString("Navn"), rs.getString("Stilling"), rs.getString("Afdeling"), rs.getInt("userid"));
                    profilesbyid.add(profile);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return profilesbyid;
    }
}
