module org.example.kmanage {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.calendarfx.view;


    opens org.example.kmanage to javafx.fxml;
    exports org.example.kmanage;
}