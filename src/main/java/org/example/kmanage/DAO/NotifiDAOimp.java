package org.example.kmanage.DAO;

import org.example.kmanage.User.User;
import org.example.kmanage.User.UserSession;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotifiDAOimp implements NotifiDAO{

    User loggedInUser = UserSession.getInstance(null).getUser();

    private Connection con;

    public NotifiDAOimp() {
        try {
            con = DriverManager.getConnection("jdbc:sqlserver://10.176.111.34:1433;database=Kmanager2023;userName=CSe2023t_t_2;password=CSe2023tT2#23;encrypt=true;trustServerCertificate=true");

        } catch (SQLException e) {
            System.out.println("Cant connect to Database" + e);
        }
    }

    public void addallNotification(String message) {
        try {
            String sql = "INSERT INTO dbo.Notifications (Message, from_id, to_id) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, message);
            ps.setInt(2, loggedInUser.getProfile().getId());
            ps.setNull(3, Types.INTEGER);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error" + e);
        }
    }

    public void addtoNotification(String message, int to_id) {
        try {
            String sql = "INSERT INTO dbo.Notifications (Message, from_id, to_id) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, message);
            ps.setInt(2, loggedInUser.getProfile().getId());
            ps.setInt(3, to_id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error" + e);
        }
    }

    public List getNotifications() {
        List<String> notifications = new ArrayList<>();
        try {
            String sql = "SELECT * FROM dbo.Notifications WHERE to_id IS NULL OR to_id = ? ORDER BY Timestamp DESC";;
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, loggedInUser.getProfile().getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                notifications.add(rs.getString("Message"));
            }
        } catch (SQLException e) {
            System.out.println("Error" + e);
        }
        return notifications;
    }


}
