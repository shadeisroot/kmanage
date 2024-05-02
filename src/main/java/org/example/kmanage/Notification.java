package org.example.kmanage;

import java.util.ArrayList;
import java.util.List;

public class Notification {
    private List<String> messages = new ArrayList<>();
    NotifiDAO ndi = new NotifiDAOimp();

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

    public List showMessages(){
        messages = ndi.getNotifications();
        return messages;
    }
}
