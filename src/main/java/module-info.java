module org.example.kmanage {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.calendarfx.view;


    opens org.example.kmanage to javafx.fxml;
    exports org.example.kmanage;
    exports org.example.kmanage.DAO;
    opens org.example.kmanage.DAO to javafx.fxml;
    exports org.example.kmanage.User;
    opens org.example.kmanage.User to javafx.fxml;
    exports org.example.kmanage.Controller;
    opens org.example.kmanage.Controller to javafx.fxml;
    exports org.example.kmanage.Notifications;
    opens org.example.kmanage.Notifications to javafx.fxml;
}