package org.example.kmanage.DAO;

import javafx.collections.ObservableList;
import org.example.kmanage.User.Project;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface CalenderDAO {
    void addEvent(String name, String start, String end, int id, String notes, String event, String meeting) throws Exception;

    ObservableList<Project> getevents();

    int getprojectid(String name, String startdate, String enddate, int owner, String notes, String eventdate, String meetingdate) throws SQLException;


    void addProjectMember(int id, int memberid);

    void editEvent(String name, String start, String end, int id, String notes, String event, String meeting, int idofproject) throws SQLException;

    List<Integer> getProjectMembers(int id);
}
