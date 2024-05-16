package org.example.kmanage.User;

import org.example.kmanage.Controller.HelloController;
import org.example.kmanage.Notifications.Notification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Project {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate eventDate;

    private int owner;

    private String notes;
    private String location;
    private List<String> files;
    private boolean knockRequest = false;


    public Project(String name, LocalDate startDate, LocalDate endDate, int owner, String notes,  LocalDate eventDate) {

        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventDate = eventDate;
        this.owner = owner;
        this.location = "";
        this.notes = notes;
        this.files = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getFiles() {
        return files;
    }

    public void addFiles(String files) {
        this.files.add(files);
    }

    public void removeFiles(String files) {
        this.files.remove(files);
    }

    public boolean isKnockRequest(){
        return knockRequest;
    }

    public void requestKnock(User user){
        this.knockRequest = true;
        Notification notification = new Notification();
        notification.addMessage( user.getUsername() + " har banket på til projektet: " + name);
        System.out.println("banke på til " + name);
    }
}
