package org.example.kmanage.DAO;

import java.util.List;

public interface NotifiDAO {

    void addallNotification(String message);

    void addtoNotification(String message, int to_id);

    List<String> getNotifications();
}
