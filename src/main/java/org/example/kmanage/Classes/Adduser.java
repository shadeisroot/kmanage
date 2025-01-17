package org.example.kmanage.Classes;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.kmanage.Controller.HelloController;
import org.example.kmanage.DAO.ProfileDAO;
import org.example.kmanage.DAO.ProfileDAOImp;

import java.sql.SQLException;

public class Adduser {
    HelloController helloController = new HelloController();
    ProfileDAO edi = new ProfileDAOImp();
    public Adduser(MouseEvent event) {
        //opstilling af stage og pane
        Stage addUserStage = new Stage();

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        //tilføjer tekst felter og labels til de passende elementer og sætter den ind i grid pane
        TextField usernameField = new TextField();
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);

        PasswordField passwordField = new PasswordField();
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);

        TextField Navnfield = new TextField();
        grid.add(new Label("Navn:"), 0, 2);
        grid.add(Navnfield, 1, 2);

        TextField StillingField = new TextField();
        grid.add(new Label("Stilling:"), 0, 3);
        grid.add(StillingField, 1, 3);

        TextField AfdelingField = new TextField();
        grid.add(new Label("Afdeling:"), 0, 4);
        grid.add(AfdelingField, 1, 4);
        //combobox til at vælge imellem admin eller user
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("admin", "user");
        grid.add(new Label("Role:"), 0, 5);
        grid.add(roleComboBox, 1, 5);

        //knap
        Button addButton = new Button("Add User");
        grid.add(addButton, 1, 6);
        //henter værdierne til de forskellige felter
        addButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String role = roleComboBox.getValue();
            String navn = Navnfield.getText();
            String Stilling = StillingField.getText();
            String Afdeling = AfdelingField.getText();
            int roleId = "admin".equals(role) ? 1 : 3;
            try {
                //giver værdierne til metode inde i profile
                edi.addEmployee(navn, Stilling, Afdeling);
                int id = edi.getUserid(navn, Stilling, Afdeling);
                // samme men med værdier der passer til login
                edi.createLogin(username, password, roleId, id);
                helloController.refreshplist();

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            // lukker vindue efter det er blevet tilføjet en user
            addUserStage.close();
        });

        // Create a Scene with the GridPane and set it on the Stage
        Scene scene = new Scene(grid, 300, 300);
        addUserStage.setResizable(false);
        addUserStage.setScene(scene);

        // Show the Stage
        addUserStage.show();
    }
}
