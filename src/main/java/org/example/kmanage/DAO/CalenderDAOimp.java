package org.example.kmanage.DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.kmanage.User.Project;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
        String sql = "INSERT INTO dbo.projects (name, startdate, enddate, owner, notes, eventdate, meetingdates) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, name);
        ps.setString(2, start);
        ps.setString(3, end);
        ps.setInt(4, id);
        ps.setString(5, notes);
        ps.setString(6, event);
        ps.setString(7, meeting);
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
                List<LocalDate> meetingDates = new ArrayList<>();

                String dateString4 = rs.getString("meetingdates");
                if (dateString4 != null) {
                    dateString4 = dateString4.replace("[", "").replace("]", ""); // Fjern parenteser
                    for (String dateStr : dateString4.split(",")) {
                        try {
                            meetingDates.add(LocalDate.parse(dateStr.trim()));
                        } catch (DateTimeParseException e) {
                            System.out.println("Fejl ved parsing af dato: " + e.getMessage());
                        }
                    }
                }
                LocalDate date = LocalDate.parse(dateString);
                LocalDate date2 = LocalDate.parse(dateString2);
                LocalDate date3 = LocalDate.parse(dateString3);

                Project project = new Project(rs.getString("name"), date, date2, rs.getInt("owner"), rs.getString("notes"), date3, meetingDates);

                projects.add(project);
            }
        } catch (SQLException e) {
            System.out.println("Error" + e);
        }
        return projects;
    }

    public int getprojectid(String name, String startdate, String enddate, int owner, String notes, String eventdate, String meetingdates) throws SQLException {
            String sql = "SELECT id from dbo.projects WHERE name = ? AND startdate = ? AND enddate = ? AND owner = ? AND eventdate = ? AND meetingdates = ? ";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, startdate);
            ps.setString(3, enddate);
            ps.setInt(4, owner);
            ps.setString(5, eventdate);
            ps.setString(6, meetingdates);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new SQLException("Failed to retrieve projectid");
            }
    }

        public void addProjectMember(int id, int memberid){
            try {
                String sql = "INSERT INTO dbo.ProjectUSER (Project_ID, Member_ID) VALUES (?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, id);
                ps.setInt(2, memberid);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Error" + e);
            }
        }

        public void editEvent(String name, String start, String end, int id, String notes, String event, String meeting, int idofproject) throws SQLException {
            String sql = "UPDATE dbo.projects SET name = ?, startdate = ?, enddate = ?, owner = ?, notes = ?, eventdate = ?, meetingdates = ? WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, start);
            ps.setString(3, end);
            ps.setInt(4, id);
            ps.setString(5, notes);
            ps.setString(6, event);
            ps.setString(7, meeting);
            ps.setInt(8, idofproject);
            ps.executeUpdate();
        }

        public List<Integer> getProjectMembers(int id) {

            List<Integer> members = new ArrayList<>();
            try {
                String sql = "SELECT * from dbo.ProjectUSER WHERE Project_ID = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    members.add(rs.getInt("Member_ID"));
                }
            } catch (SQLException e) {
                System.out.println("Error" + e);
            }
            return members;
        }

        public void removeprojectmember(int id, int memberid){
            try {
                String sql = "DELETE FROM dbo.ProjectUSER WHERE Project_ID = ? AND Member_ID = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, id);
                ps.setInt(2, memberid);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Error" + e);
            }
        }

    public int getprojectidnoowner(String name, String startdate, String enddate, String notes, String eventdate, String meetingdates) throws SQLException {
        String sql = "SELECT id from dbo.projects WHERE name = ? AND startdate = ? AND enddate = ? AND eventdate = ? AND meetingdates = ? ";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, name);
        ps.setString(2, startdate);
        ps.setString(3, enddate);
        ps.setString(4, eventdate);
        ps.setString(5, meetingdates);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        } else {
            throw new SQLException("Failed to retrieve projectid");
        }
    }
    }
