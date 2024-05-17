package org.example.kmanage.DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.kmanage.User.Project;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class CalenderDAOimp implements CalenderDAO{

    private Connection con;

    public CalenderDAOimp() {
        try {
            con = DriverManager.getConnection("jdbc:sqlserver://10.176.111.34:1433;database=Kmanager2023;userName=CSe2023t_t_2;password=CSe2023tT2#23;encrypt=true;trustServerCertificate=true");

        } catch (SQLException e) {
            System.out.println("Cant connect to Database" + e);
        }
    }


    public void addEvent(String name, String start, String end, int id, String notes, String event, String meeting) throws SQLException {
        String sql = "INSERT INTO dbo.projects (name, startdate, enddate, owner, notes, eventdate, meetingdate) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, name);
        ps.setString(2, start);
        ps.setString(3, end);
        ps.setInt(4, id);
        ps.setString(5, notes);
        ps.setString(6, event);
        ps.setString(6, meeting);
        ps.executeUpdate();
    }

    public ObservableList<Project> getevents() {
        ObservableList<Project> projects = FXCollections.observableArrayList();
        try {
            String sql = "SELECT * from dbo.projects";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String dateString = rs.getString("startdate");
                String dateString2 = rs.getString("enddate");
                String dateString3 = rs.getString("eventdate");
                String dateString4 = rs.getString("meetingdate");
                LocalDate date = LocalDate.parse(dateString);
                LocalDate date2 = LocalDate.parse(dateString2);
                LocalDate date3 = LocalDate.parse(dateString3);
                LocalDate date4 = LocalDate.parse(dateString4);
                Project project = new Project(rs.getString("name"), date, date2, rs.getInt("owner"), rs.getString("notes"), date3, date4);

                projects.add(project);
            }
        } catch (SQLException e) {
            System.out.println("Error" + e);
        }
        return projects;
    }
}
