package org.example.kmanage;

import java.util.List;

public interface NotifiDAO {

    void addNotification(String message);

    List<String> getNotifications();
}
