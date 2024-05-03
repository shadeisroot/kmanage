package org.example.kmanage.Controller;


import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.kmanage.DAO.PlistDAO;
import org.example.kmanage.DAO.PlistDAOimp;
import org.example.kmanage.Notifications.Notification;
import org.example.kmanage.User.Profile;
import org.example.kmanage.User.Project;
import org.example.kmanage.User.User;
import org.example.kmanage.User.UserSession;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class HelloController {

    private ObservableList<Project> projects = FXCollections.observableArrayList();
    public TableView plist;
    public TableColumn plistc1;
    public TableColumn plistc2;
    public TableColumn plistc3;
    @FXML
    private GridPane calendarGrid;
    private LocalDate currentDate = LocalDate.now();

    PlistDAO pdi = new PlistDAOimp();
    private ObservableList<Profile> profiles = pdi.getprofile();

    private enum ViewMode { DAG, UGE, MÅNED, TRE_MÅNEDER }
    private ViewMode currentViewMode = ViewMode.UGE;

    @FXML
    private Button zoomInd, zoomOut, opretButton;

    User loggedInUser = UserSession.getInstance(null).getUser();

    Notification not = new Notification();

    public void initialize() {
        weekView();
        initializeplist();
        updateCalender();

    }

    public void opretonpressed(ActionEvent actionEvent) {
        createNewEventDialog();
    }
    public void createNewEventDialog () {
        // Create a new dialog
        Dialog<Event> dialog = new Dialog<>();
        dialog.setTitle("Create New Event");

        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create the event name and date labels and fields
        TextField eventName = new TextField();
        eventName.setPromptText("Event Name");
        DatePicker eventDate = new DatePicker();

        // Create a grid pane and set the labels and fields to it
        GridPane grid = new GridPane();
        grid.add(new Label("Event Name:"), 0, 0);
        grid.add(eventName, 1, 0);
        grid.add(new Label("Event Date:"), 0, 1);
        grid.add(eventDate, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to an Event object when the OK button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return null;
            }
            return null;
        });

        // Show the dialog and wait for the user to respond
        Optional<Event> result = dialog.showAndWait();
    }





    public void initializeplist() {
        plistc1.setCellValueFactory(new PropertyValueFactory<>("name"));
        plistc2.setCellValueFactory(new PropertyValueFactory<>("position"));
        plistc3.setCellValueFactory(new PropertyValueFactory<>("department"));;
        plist.setItems(profiles);
    }


    public void notifi(List<String> messages) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText("You have new notifications");
        StringBuilder allMessages = new StringBuilder();
        for (String message : messages) {
            allMessages.append(message).append("\n");
        }

        alert.setContentText(allMessages.toString());
        alert.showAndWait();
    }
    @FXML
    private void notButtonPressed(ActionEvent event) {
        notifi(not.showMessages());
    }

    private void updateCalender() {
        calendarGrid.getChildren().clear();

        switch (currentViewMode) {
            case DAG:
                dayView();
                break;
            case UGE:
                weekView();
                break;
            case MÅNED:
                monthView();
                break;
            case TRE_MÅNEDER:
                threeMonthView();
                break;
        }
    }

    private void dayView(){
        calendarGrid.getChildren().clear();

        // Gemmer dagens dato til sammenligning
        LocalDate today = LocalDate.now();

        // Opbygger viewet for den aktuelle dag
        VBox dayBox = new VBox();
        dayBox.setSpacing(10);
        dayBox.setPrefHeight(500);
        dayBox.setPrefWidth(840);
        dayBox.setAlignment(Pos.CENTER);
        dayBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #ffffff; -fx-padding: 20;");

        // Formaterer og viser datoen
        String dayName = currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("da", "DK"));
        String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("EEEE, d. MMMM yyyy", new Locale("da", "DK")));

        Label dayLabel = new Label(dayName);
        dayLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        Label dateLabel = new Label(formattedDate);
        dateLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 16px;");

        dayBox.getChildren().addAll(dayLabel, dateLabel);

        // Kontrollerer om den aktuelle dag er i dag og ændrer baggrundsfarven hvis sandt
        if (currentDate.equals(today)) {
            dayBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #ffdd55; -fx-padding: 20;");
        }

        calendarGrid.add(dayBox, 0, 0);
    }

    private void weekView(){
        calendarGrid.getChildren().clear();

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = currentDate.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        Map<String, Integer> projectRowMap = new HashMap<>();
        int nextAvailableRow = 1;



        for (int i = 0; i < 7; i++) {
            LocalDate date = startOfWeek.plusDays(i);
            VBox dayBox = new VBox();
            dayBox.setSpacing(0);
            dayBox.setPrefHeight(25);
            dayBox.setPrefWidth(120);
            dayBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #ffffff; -fx-padding: 10;");

            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, new Locale("da", "DK"));
            String formattedDate = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("da", "DK")));

            Label dayLabel = new Label(dayName);
            dayLabel.setStyle("-fx-font-weight: bold;");

            Label dateLabel = new Label(formattedDate);
            dateLabel.setStyle("-fx-text-fill: #666666;");

            dayBox.getChildren().addAll(dayLabel, dateLabel);
            if (date.equals(today)) {
                dayBox.setStyle("-fx-background-color: #ffdd55; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10;");
            }

            calendarGrid.add(dayBox, i, 0);

            for (Project project : projects) {
                if (!project.getStartDate().isAfter(date) && !project.getEndDate().isBefore(date)) {
                    int startCol = (int) ChronoUnit.DAYS.between(startOfWeek, project.getStartDate());
                    int endCol = (int) ChronoUnit.DAYS.between(startOfWeek, project.getEndDate());
                    int rowForProject = projectRowMap.getOrDefault(project.getName(), nextAvailableRow);

                    if (!projectRowMap.containsKey(project.getName())) {
                        projectRowMap.put(project.getName(), nextAvailableRow++);
                    }

                    VBox projectBox = new VBox(new Label(project.getName()));
                    projectBox.setStyle("-fx-background-color: lightblue; -fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.7;");

                    GridPane.setConstraints(projectBox, startCol, rowForProject, endCol - startCol + 1, 1);
                    GridPane.setFillWidth(projectBox, true);
                    calendarGrid.getChildren().add(projectBox);
                }
            }
        }
    }

    private void monthView(){
        calendarGrid.getChildren().clear();

        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        YearMonth yearMonth = YearMonth.from(today);
        int daysInMonth = yearMonth.lengthOfMonth();

        for (int i = 0; i < daysInMonth; i++) {
            LocalDate date = firstDayOfMonth.plusDays(i);
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, new Locale("da", "DK"));
            String formattedDate = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("da", "DK")));

            VBox dayBox = new VBox();
            dayBox.setSpacing(0);
            dayBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #ffffff; -fx-padding: 10;");

            if (date.equals(today)) {
                dayBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #ffdd55; -fx-padding: 10;");
            }

            Label dayLabel = new Label(dayName + " " + formattedDate);
            dayLabel.setStyle("-fx-font-weight: bold;");

            dayBox.getChildren().add(dayLabel);
            calendarGrid.add(dayBox, i % 7, i / 7);
        }

    }

    private void threeMonthView(){

    }

    public void zoomOutPressed(ActionEvent event) {
        if (currentViewMode == ViewMode.DAG) {
            currentViewMode = ViewMode.UGE;
        } else if (currentViewMode == ViewMode.UGE) {
            currentViewMode = ViewMode.MÅNED;
        } else if (currentViewMode == ViewMode.MÅNED) {
            currentViewMode = ViewMode.TRE_MÅNEDER;
        }

        updateCalender();
    }


    public void zoomIndPressed(ActionEvent event) {
        if (currentViewMode == ViewMode.TRE_MÅNEDER) {
            currentViewMode = ViewMode.MÅNED;
        } else if (currentViewMode == ViewMode.MÅNED) {
            currentViewMode = ViewMode.UGE;
        } else if (currentViewMode == ViewMode.UGE) {
            currentViewMode = ViewMode.DAG;
        }

        updateCalender();
    }

    public void tilbageButtonPressed(ActionEvent event) {
        switch (currentViewMode) {
            case DAG:
                currentDate = currentDate.minusDays(1);
                break;
            case UGE:
                currentDate = currentDate.minusWeeks(1);
                break;
            case MÅNED:
                currentDate = currentDate.minusMonths(1);
                break;
        }
        updateCalender();
    }

    public void fremButtonPressed(ActionEvent event) {
        switch (currentViewMode) {
            case DAG:
                currentDate = currentDate.plusDays(1);
                break;
            case UGE:
                currentDate = currentDate.plusWeeks(1);
                break;
            case MÅNED:
                currentDate = currentDate.plusMonths(1);
                break;
        }
        updateCalender();
    }

    public void opretButtonPressed(ActionEvent event) {
        Stage stage = new Stage();
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(25, 25, 25, 25));

        TextField nameField = new TextField();
        DatePicker startDatePick = new DatePicker();
        DatePicker endDatePick = new DatePicker();
        Button opretButton = new Button("opret projekt");

        pane.add(new Label("Navn:"), 0, 0);
        pane.add(nameField, 1, 0);
        pane.add(new Label("Start Dato:"), 0, 1);
        pane.add(startDatePick, 1, 1);
        pane.add(new Label("slut dato"), 0, 2);
        pane.add(endDatePick, 1, 2);
        pane.add(opretButton, 1, 3);

        opretButton.setOnAction(e -> {
            Project project = new Project(nameField.getText(), startDatePick.getValue(), endDatePick.getValue());
            projects.add(project);
            updateCalender();
            stage.close();
        });

        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
    }

}