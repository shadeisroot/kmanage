package org.example.kmanage.DAO;

import java.util.List;

public interface NotifiDAO {

    void addNotification(String message);

    List<String> getNotifications();
}
