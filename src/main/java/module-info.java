module org.example.kmanage {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.kmanage to javafx.fxml;
    exports org.example.kmanage;
}