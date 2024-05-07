package org.example.kmanage.Controller;


import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.kmanage.DAO.PlistDAO;
import org.example.kmanage.DAO.PlistDAOimp;
import org.example.kmanage.Main;
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

    public void logout(ActionEvent actionEvent) {

        UserSession.getInstance(null).cleanUserSession();
        Stage stage = (Stage) plist.getScene().getWindow();

        //start the main class
        Main main = new Main();
        try {
            main.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
            stage.close();
        }
    }

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

        plist.setRowFactory(tv -> new TableRow<Profile>() {
            @Override
            protected void updateItem(Profile item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                } else {
                    switch (item.getDepartment()) {
                        case "Tønder Bibliotekerne":
                            setStyle("-fx-background-color: lightblue;");
                            break;
                        case "Tønder Kulturskole":
                            setStyle("-fx-background-color: lightgreen;");
                            break;
                        case "Tønder Medborgerhus":
                            setStyle("-fx-background-color: lightyellow;");
                            break;
                        case "Schweizerhalle":
                            setStyle("-fx-background-color: lightpink;");
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });
        plist.setItems(profiles);


    }


    public void notifi(List<String> messages) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText("You have new notifications");

        // Opret en TextArea til at vise beskeder
        TextArea textArea = new TextArea();
        textArea.setEditable(false);  // Sørger for, at brugeren ikke kan redigere tekst
        textArea.setWrapText(true);   // Aktiverer tekstombrydning

        // Byg en streng af alle beskeder med linjeskift mellem hver besked
        StringBuilder allMessages = new StringBuilder();
        for (String message : messages) {
            allMessages.append(message).append("\n");
        }
        textArea.setText(allMessages.toString());

        // Opret en ScrollPane og tilføj TextArea til den
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(textArea);
        textArea.setPrefWidth(100);
        scrollPane.setFitToWidth(true);  // Sørger for at bredden tilpasses til TextArea
        scrollPane.setPrefHeight(200);   // Sætter en foretrukken højde for ScrollPane

        // Sæt scrollPane som indhold i dialogvinduet
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setContent(scrollPane);
        dialogPane.getStylesheets().add
                (Main.class.getResource("notiStyle.css").toExternalForm());
        dialogPane.getStyleClass().add("alert");

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
        LocalDate today = currentDate;

        // Opbygger viewet for den aktuelle dag
        VBox dayBox = new VBox();
        dayBox.setSpacing(10);
        dayBox.setPrefHeight(25);
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

        if (currentDate.equals(today)) {
            dayBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #ffdd55; -fx-padding: 20;");
        }

        calendarGrid.add(dayBox, 0, 0);

        int projectRow = 1;
        for (Project project : projects) {
            if (!project.getStartDate().isAfter(today) && !project.getEndDate().isBefore(today)) {
                Label projectLabel = new Label(project.getName());
                projectLabel.setStyle("-fx-background-color: lightblue; -fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.7;");

                VBox projectBox = new VBox(projectLabel);
                projectBox.setPadding(new Insets(2));
                projectBox.setStyle("-fx-background-color: lightblue; -fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.7;");
                calendarGrid.add(projectBox, 0, projectRow++);
            }
        }
    }

    private void weekView(){
        calendarGrid.getChildren().clear();

        // Definerer dagens dato, ugens start og slut
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = currentDate.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // Sporer rækkenummeret tilgængeligt for nye projektopslag
        Map<String, Integer> projectRowMap = new HashMap<>();
        int nextAvailableRow = 1;


        // Løkke igennem hver dag i ugen
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

            //Viser projekter, der spænder over den aktuelle dato
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

    private void monthView() {
        calendarGrid.getChildren().clear();

        // Definerer dagens dato samt start- og slutdatoer for den aktuelle måned
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        LocalDate lastDayOfMonth = firstDayOfMonth.plusMonths(1).minusDays(1);
        YearMonth yearMonth = YearMonth.from(currentDate);
        int daysInMonth = yearMonth.lengthOfMonth();
        DayOfWeek startDayOfWeek = firstDayOfMonth.getDayOfWeek();
        int startCol = (startDayOfWeek.getValue() - 1) % 7;
        LocalDate gridStartDate = firstDayOfMonth.minusDays(startCol);

        // Beregner det samlede antal celler, der skal udfyldes i gitteret
        int totalDays = daysInMonth + startCol;
        totalDays += (7 - (totalDays % 7)) % 7;

        // Hashmap til at spore den sidst brugte række for hver dato
        Map<LocalDate, Integer> lastRowUsedPerDay = new HashMap<>();

        // Udfylder grid med bokse
        for (int i = 0; i < totalDays; i++) {
            LocalDate date = gridStartDate.plusDays(i);
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, new Locale("da", "DK"));
            String formattedDate = date.format(DateTimeFormatter.ofPattern("dd MMM", new Locale("da", "DK")));

            VBox dayBox = new VBox();
            dayBox.setSpacing(0);
            dayBox.setPrefWidth(120);
            dayBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #ffffff; -fx-padding: 10;");

            // Ændrer stil for dage uden for den aktuelle måned
            if (date.isBefore(firstDayOfMonth) || date.isAfter(lastDayOfMonth)) {
                dayBox.setStyle("-fx-background-color: #bab3b3; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10;");
            } else if (date.equals(today)) {
                dayBox.setStyle("-fx-background-color: #ffdd55; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10;");
            }

            Label dayLabel = new Label(dayName + " " + formattedDate);
            dayLabel.setStyle("-fx-font-weight: bold;");
            dayBox.getChildren().add(dayLabel);

            int col = i % 7;
            int row = (i / 7) * 2;
            calendarGrid.add(dayBox, col, row);

            // Nulstil eller initialiser kortet over den sidst brugte række for denne dag
            lastRowUsedPerDay.putIfAbsent(date, row + 1);
        }

        // Viser projekter i kalender
        for (Project project : projects) {
            LocalDate startDate = project.getStartDate();
            LocalDate endDate = project.getEndDate();

            int startIdx = (int) ChronoUnit.DAYS.between(gridStartDate, startDate);
            int endIdx = (int) ChronoUnit.DAYS.between(gridStartDate, endDate);
            int startColProj = startIdx % 7;
            int endColProj = endIdx % 7;

            int startRow = lastRowUsedPerDay.getOrDefault(gridStartDate.plusDays(startIdx), (startIdx / 7) * 2 + 1);
            int endRow = startRow;

            VBox projectBox = new VBox(new Label(project.getName()));
            projectBox.setStyle("-fx-background-color: lightblue; -fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.7;");

            GridPane.setConstraints(projectBox, startColProj, startRow, endColProj - startColProj + 1, 1, HPos.LEFT, VPos.TOP);
            calendarGrid.getChildren().add(projectBox);


            for (int j = startIdx; j <= endIdx; j++) {
                lastRowUsedPerDay.put(gridStartDate.plusDays(j), endRow + 1);
            }
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
    @FXML
    private void handleToggleTheme(ActionEvent event) {
        Scene scene = ((MenuItem)event.getSource()).getParentPopup().getOwnerWindow().getScene();
        toggleTheme(scene);
    }

    private void toggleTheme(Scene scene) {
        ObservableList<String> stylesheets = scene.getStylesheets();
        // Opretter fulde stier for CSS-filerne
        String lightThemePath = getClass().getResource("/org/example/kmanage/maincss.css").toExternalForm();
        String darkThemePath = getClass().getResource("/org/example/kmanage/mainCssDark.css").toExternalForm();

        if (lightThemePath == null || darkThemePath == null) {
            System.err.println("Din tråd til css'erne er forkert gg");
            return;
        }

        if (stylesheets.contains(lightThemePath)) {
            // Skifter fra lys til mørk tema
            stylesheets.clear();
            stylesheets.add(darkThemePath);
        } else {
            // Skifter fra mørk til lys tema eller initialiserer lys tema
            stylesheets.clear();
            stylesheets.add(lightThemePath);
        }
    }

}