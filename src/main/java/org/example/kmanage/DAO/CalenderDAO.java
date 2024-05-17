package org.example.kmanage.DAO;

import javafx.collections.ObservableList;
import org.example.kmanage.User.Project;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

public interface CalenderDAO {
    void addEvent(String name, String start, String end, int id, String notes, String event, String meeting) throws Exception;

    ObservableList<Project> getevents();

    int getprojectid(String name, String startdate, String enddate, int owner, String notes, String eventdate, String meetingdate);


    void addProjectMember(int id, int memberid);
    }
