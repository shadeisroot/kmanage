package org.example.kmanage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotifiDAOimp implements NotifiDAO{

    private Connection con;

    public NotifiDAOimp() {
        try {
            con = DriverManager.getConnection("jdbc:sqlserver://10.176.111.34:1433;database=Kmanager2023;userName=CSe2023t_t_2;password=CSe2023tT2#23;encrypt=true;trustServerCertificate=true");

        } catch (SQLException e) {
            System.out.println("Cant connect to Database" + e);
        }
    }

    public void addNotification(String message) {
        try {
            String sql = "INSERT INTO dbo.Notifications (Message) VALUES (?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, message);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error" + e);
        }
    }

    public List getNotifications() {
        List<String> notifications = new ArrayList<>();
        try {
            String sql = "SELECT * FROM dbo.Notifications ORDER BY Timestamp DESC";
            PreparedStatement ps = con.prepareStatement(sql);
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
