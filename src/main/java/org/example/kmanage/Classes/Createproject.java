package org.example.kmanage.Classes;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.kmanage.Controller.HelloController;
import org.example.kmanage.DAO.CalenderDAO;
import org.example.kmanage.DAO.CalenderDAOimp;
import org.example.kmanage.Main;
import org.example.kmanage.User.Profile;
import org.example.kmanage.User.Project;
import org.example.kmanage.User.User;
import org.example.kmanage.User.UserSession;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Createproject {
    CalenderDAO cdi = new CalenderDAOimp();
    HelloController hdi = new HelloController();
    public Createproject(ActionEvent event, HelloController hdi) {
        this.hdi = hdi;
        Stage stage = new Stage();
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(25, 25, 25, 25));

        TextField nameField = new TextField();
        nameField.setPromptText("Skriv Navn på projekt");
        TextField locationField = new TextField();
        locationField.setPromptText("Skriv lokation");
        DatePicker startDatePick = new DatePicker();
        startDatePick.setPromptText("Vælg Dato");
        DatePicker endDatePick = new DatePicker();
        endDatePick.setPromptText("Vælg Dato");
        DatePicker eventDatePick = new DatePicker();
        eventDatePick.setPromptText("Vælg Dato");
        startDatePick.setEditable(false);
        endDatePick.setEditable(false);

        TextField meetingNameField = new TextField();
        meetingNameField.setPromptText("Skriv overskrift til mødet");
        DatePicker meetingDatePick = new DatePicker();
        meetingDatePick.setPromptText("Vælg dato");
        meetingDatePick.setEditable(false);
        ComboBox<String> meetingTimeComboBox = new ComboBox<>();
        meetingTimeComboBox.getItems().addAll("08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30",
                "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00", "23:30");
        meetingTimeComboBox.setPromptText("Vælg tidspunkt");

        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Noter kan skrives her");
        notesArea.setWrapText(true);
        notesArea.setPrefRowCount(4);
        notesArea.setPrefColumnCount(20);
        notesArea.setPrefHeight(200);


        Button addFilesButton = new Button("Vedhæft fil");
        Button addMeetingButton = new Button("Tilføj møde");
        Button opretButton = new Button("Opret projekt");
        opretButton.setDisable(true);

        startDatePick.valueProperty().addListener((obs, oldDate, newDate) -> {
            opretButton.setDisable(endDatePick.getValue() == null || eventDatePick.getValue() == null || newDate == null);
        });
        endDatePick.valueProperty().addListener((obs, oldDate, newDate) -> {
            opretButton.setDisable(startDatePick.getValue() == null || eventDatePick.getValue() == null || newDate == null);
        });
        eventDatePick.valueProperty().addListener((obs, oldDate, newDate) -> {
            opretButton.setDisable(startDatePick.getValue() == null || endDatePick.getValue() == null || newDate == null);
        });


        pane.add(new Label("Navn:"), 0, 0);
        pane.add(nameField, 1, 0);
        pane.add(new Label("Lokation:"), 0, 1); // Tilføj label for lokation
        pane.add(locationField, 1, 1);
        pane.add(new Label("Start Dato:"), 0, 2);
        pane.add(startDatePick, 1, 2);
        pane.add(new Label("Slut dato"), 0, 3);
        pane.add(endDatePick, 1, 3);
        pane.add(new Label("Dato for begivenhed"), 0, 4);
        pane.add(eventDatePick, 1, 4);
        pane.add(new Label("Noter:"), 0, 5);
        pane.add(notesArea, 1, 5);
        pane.add(new Label("Møde Navn:"), 0, 6);
        pane.add(meetingNameField, 1, 6);
        pane.add(new Label("Møde Dato:"), 0, 7);
        pane.add(meetingDatePick, 1, 7);
        pane.add(new Label("Møde Tid:"), 0, 8);
        pane.add(meetingTimeComboBox, 1, 8);
        TableView Membertableview = hdi.initializecreatenewtable();
        pane.add(Membertableview, 2, 0);
        GridPane.setRowSpan(Membertableview, 9);
        TableView targettable = hdi.targettable();
        pane.add(targettable, 3, 0);
        GridPane.setRowSpan(targettable, 9);
        pane.add(addMeetingButton, 1, 9);
        pane.add(addFilesButton, 1, 10);
        pane.add(opretButton, 1, 11);

        List<String> files = new ArrayList<>();
        addFilesButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                files.add(selectedFile.getName());
            }
        });
        List<LocalDate> meetingDates = new ArrayList<>();

        targettable.setRowFactory(ts -> {
            TableRow<Profile> row = new TableRow<>();
            row.setOnDragDetected(tt -> {
                if (!row.isEmpty()) {
                    Dragboard db = row.startDragAndDrop(TransferMode.COPY);
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString(Integer.toString(row.getIndex()));
                    db.setContent(cc);
                    targettable.getItems().remove( row.getItem());
                    event.consume();
                }
            });
            return row;
        });



        Membertableview.setRowFactory(tv -> {
            TableRow<Profile> row = new TableRow<>();
            row.setOnDragDetected(tt -> {
                if (!row.isEmpty()) {
                    Dragboard db = row.startDragAndDrop(TransferMode.COPY);
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString(Integer.toString(row.getIndex()));
                    db.setContent(cc);
                    event.consume();
                }
            });
            return row;
        });

        targettable.setOnDragOver(tv -> {
            if (tv.getDragboard().hasString()) {
                tv.acceptTransferModes(TransferMode.COPY);
            }
        });

        targettable.setOnDragDropped(tv -> {
            Dragboard db = tv.getDragboard();
            if (db.hasString()) {
                int index = Integer.parseInt(db.getString());
                Profile profile = (Profile) Membertableview.getItems().get(index);
                targettable.getItems().add(profile);
                tv.setDropCompleted(true);
            } else {
                tv.setDropCompleted(false);
            }
            event.consume();
        });

        addMeetingButton.setOnAction(e -> {
            LocalDate meetingDate = meetingDatePick.getValue();
            if (meetingDate != null) {
                meetingDates.add(meetingDate); // Tilføj den valgte mødedato til listen
                String meetingInfo = "Møde: " + meetingNameField.getText() + " | " + meetingDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " " + meetingTimeComboBox.getValue();
                notesArea.appendText(meetingInfo + "\n");
                meetingNameField.clear();
                meetingTimeComboBox.getSelectionModel().clearSelection();
            }
        });

        opretButton.setOnAction(e -> {
            User loggedInUser = UserSession.getInstance(null).getUser();
            String projectName = nameField.getText();
            LocalDate startDate = startDatePick.getValue();
            LocalDate endDate = endDatePick.getValue();
            LocalDate eventDate = eventDatePick.getValue();

            if (startDate == null || endDate == null || projectName.isEmpty()) {
                // Vis en fejlmeddelelse til brugeren eller returnér tidligt
                System.out.println("Projektet skal have et navn og start- og slutdato.");
                return; // Stopper yderligere eksekvering hvis de nødvendige felter ikke er udfyldt
            }

            Project project = new Project(
                    projectName, startDate, endDate, loggedInUser.getProfile().getId(),
                    notesArea.getText(), eventDate, meetingDates
            );
            project.setLocation(locationField.getText());
            project.setNotes(notesArea.getText());
            files.forEach(project::addFiles);

            List<Profile> members = new ArrayList<>();

            // Iterate over the items in the targettable and add each item to the members list
            for (Object profile : targettable.getItems()) {
                members.add((Profile) profile);
            }

            project.setMembers(members);

            try {
                cdi.addEvent(project.getName(), project.getStartDate().toString(), project.getEndDate().toString(), loggedInUser.getProfile().getId(), project.getNotes(), project.getEventDate().toString(), project.getMeetingDates().toString());
                int id = cdi.getprojectid(project.getName(), project.getStartDate().toString(), project.getEndDate().toString(), loggedInUser.getProfile().getId(), project.getNotes(), project.getEventDate().toString(), project.getMeetingDates().toString());
                System.out.println(id);
                for (Profile member : members) {
                    cdi.addProjectMember(id, member.getId());
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            hdi.addProject(project);
            hdi.updateCalender(hdi.getCurrentDate());
            stage.close();

        });

        Scene scene = new Scene(pane);
        scene.getStylesheets().add
                (Main.class.getResource("notiStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
