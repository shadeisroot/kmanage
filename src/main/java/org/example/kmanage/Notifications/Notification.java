package org.example.kmanage.Notifications;

import org.example.kmanage.DAO.NotifiDAO;
import org.example.kmanage.DAO.NotifiDAOimp;

import java.util.ArrayList;
import java.util.List;

public class Notification {
    private List<String> messages = new ArrayList<>();
    NotifiDAO ndi = new NotifiDAOimp();
    private boolean notificationsEnabled = true;

    public void addMessage(String message){
        messages.add(message);
        ndi.addNotification(message);
    }

    public void removeMessage(String message){
        messages.remove(message);
    }

    public void clearMessages(){
        messages.clear();
    }

    public void getMessages(){
        System.out.println("virker ikke endnu");
    }

    public List<String> showMessages(){
        messages = ndi.getNotifications();
        return messages;
    }

    public boolean isNotificationsEnabled(){
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
}
