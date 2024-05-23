package org.example.kmanage.Classes;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.kmanage.Controller.HelloController;
import org.example.kmanage.DAO.ProfileDAO;
import org.example.kmanage.DAO.ProfileDAOImp;
import org.example.kmanage.User.User;
import org.example.kmanage.User.UserSession;

import java.sql.SQLException;

public class Editprofile {
    HelloController helloController = new HelloController();
    ProfileDAO edi = new ProfileDAOImp();
    User loggedInUser = UserSession.getInstance(null).getUser();
    public Editprofile(ActionEvent event) {
        //laver stage og pane
        Stage stage = new Stage();
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(25, 25, 25, 25));
        //opretter felter og setter tekst(værdi)
        TextField nameField = new TextField();
        nameField.setText(loggedInUser.getProfile().getName());
        TextField positionField = new TextField();
        positionField.setText(loggedInUser.getProfile().getPosition());
        TextField departmentField = new TextField();
        departmentField.setText(loggedInUser.getProfile().getDepartment());
        Button editbutton = new Button("Ændre profil");
        //tilføjer label og fields til pane
        pane.add(new Label("Navn:"), 0, 0);
        pane.add(nameField, 1, 0);
        pane.add(new Label("Position:"), 0, 1);
        pane.add(positionField, 1, 1);
        pane.add(new Label("Afdeling"), 0, 2);
        pane.add(departmentField, 1, 2);
        pane.add(editbutton, 1, 3);
        //action for edit knap (setter name, positio og department ud fra hvad man har skrevet i passende felt
        editbutton.setOnAction(e -> {
            loggedInUser.getProfile().setName(nameField.getText());
            loggedInUser.getProfile().setPosition(positionField.getText());
            loggedInUser.getProfile().setDepartment(departmentField.getText());
            try {
                edi.editprofile(nameField.getText(), positionField.getText(), departmentField.getText(), loggedInUser.getProfile().getId());
                helloController.refreshplist();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            stage.close();
        });

        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
    }
}
