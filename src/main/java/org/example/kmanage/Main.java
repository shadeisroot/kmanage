package org.example.kmanage;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.example.kmanage.DAO.LoginDAO;
import org.example.kmanage.DAO.LoginDAOImp;
import org.example.kmanage.User.User;
import org.example.kmanage.User.UserSession;
import java.util.prefs.Preferences;


//taken from https://www.javaguides.net/2022/11/javafx-login-form-validation-example.html
public class Main extends Application {
    private LoginDAO ldi = new LoginDAOImp();
    private Preferences prefs = Preferences.userNodeForPackage(Main.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        //sætter scene op grid spacing
        primaryStage.setTitle("XX Login");
        GridPane grid = new GridPane();
        primaryStage.setResizable(false);
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // text med Login
        Text sceneTitle = new Text("Login");
        sceneTitle.setTextAlignment(TextAlignment.CENTER);
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));
        grid.add(sceneTitle, 0, 0, 2, 1);

        //labels og field til navn og kode
        Label userName = new Label("Email Id:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Kode:");
        grid.add(pw, 0, 2);

        PasswordField passwordBox = new PasswordField();
        grid.add(passwordBox, 1, 2);

        //husk mig check knap
        CheckBox rememberMeCheckBox = new CheckBox("Husk mig");
        grid.add(rememberMeCheckBox, 1, 3);

        String savedUserEmail = prefs.get("userEmail", "");
        if (!savedUserEmail.isEmpty()) {
            userTextField.setText(savedUserEmail);
            rememberMeCheckBox.setSelected(true);
        }


        //Login in knap
        Button btn = new Button("Log ind");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        //event handler til login knap når man trykker på enter
        passwordBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                btn.fire();
            }
        });

        //event handler til login knap ved tryk
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

                String username = userTextField.getText().toString();
                String password = passwordBox.getText().toString();
                boolean rememberMe = rememberMeCheckBox.isSelected();

                User loggedInUser = ldi.checkLogin(username, password);

                //håndter om login er vellykket eller fejlet
                if (loggedInUser != null) {
                    if (rememberMe) {
                        prefs.put("userEmail", username);
                    } else {
                        prefs.remove("userEmail");
                    }
                    infoBox("Du logger nu ind som " + loggedInUser.getUsername() , null, "Det virkede");
                    primaryStage.close();
                    UserSession.getInstance(loggedInUser);

                    //åbner næste vindue
                    HelloApplication helloApplication = new HelloApplication();
                    try {

                        helloApplication.start(new Stage());

                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                } else {
                    infoBox("Skriv en gyldig email eller kode", null, "Fejlede");
                }
            }
        });

        //sætter scenen og viser vindue (login)
        Scene scene = new Scene(grid, 300, 300);
        scene.getStylesheets().add
                (Main.class.getResource("login.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    //laver alert box
    private static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
    //laver selve layout for infobox
    public static void infoBox(String infoMessage, String headerText, String title){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(infoMessage);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }
}