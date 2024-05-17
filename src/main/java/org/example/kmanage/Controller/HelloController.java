package org.example.kmanage.Controller;


import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.kmanage.DAO.*;
import org.example.kmanage.Main;
import org.example.kmanage.Notifications.Notification;
import org.example.kmanage.User.*;

import javax.swing.*;
import java.io.File;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;

public class HelloController {

    public TextField personSearchField;
    public Button adduserbutton;
    public Button removeuserbutton;
    private ObservableList<Project> projects = FXCollections.observableArrayList();
    public TableView plist;
    public TableColumn plistc1;
    public TableColumn plistc2;
    public TableColumn plistc3;
    @FXML
    private GridPane calendarGrid;
    @FXML
    private Label calendarInfoLabel;
    @FXML
    private ImageView personSearchButton;
    @FXML
    private DatePicker datePicker;
    private LocalDate currentDate = LocalDate.now();
    CalenderDAO cdi = new CalenderDAOimp();
    PlistDAO pdi = new PlistDAOimp();
    Notification not = new Notification();
    ProfileDAO edi = new ProfileDAOImp();
    private ObservableList<Profile> profiles = pdi.getprofile();


    private enum ViewMode {DAG, UGE, MÅNED} //test

    private ViewMode currentViewMode = ViewMode.UGE;

    @FXML
    private Button zoomInd, zoomOut, opretButton;

    User loggedInUser = UserSession.getInstance(null).getUser();

    private boolean darkMode = false;


    //-------------------------------------------------------------------------------


    public void initialize() throws Exception {
        weekView();
        initializeplist();
        updateCalender(LocalDate.now());
        filterplist();
        doubleclickeventplist();
        initializebutton();
        initializeevents();

    }

    public void initializebutton() {
        if (loggedInUser.getPermissions().getId() != 1) {
            adduserbutton.setVisible(false);
            removeuserbutton.setVisible(false);
        }
        ;
    }


    public void initializeevents() throws Exception {
        projects = cdi.getevents();
        updateCalender(currentDate);
    }

    public void refreshplist() {
        profiles = pdi.getprofile();
        plist.setItems(profiles);
    }


    public void Adduser(MouseEvent mouseEvent) {
        Stage addUserStage = new Stage();

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

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

        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("admin", "user");
        grid.add(new Label("Role:"), 0, 5);
        grid.add(roleComboBox, 1, 5);


        Button addButton = new Button("Add User");
        grid.add(addButton, 1, 6);

        addButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String role = roleComboBox.getValue();
            String navn = Navnfield.getText();
            String Stilling = StillingField.getText();
            String Afdeling = AfdelingField.getText();
            int roleId = "admin".equals(role) ? 1 : 3;
            try {
                edi.addEmployee(navn, Stilling, Afdeling);
                int id = edi.getUserid(navn, Stilling, Afdeling);
                edi.createLogin(username, password, roleId, id);
                refreshplist();

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            // Close the window after the user is added
            addUserStage.close();
        });

        // Create a Scene with the GridPane and set it on the Stage
        Scene scene = new Scene(grid, 300, 300);
        addUserStage.setScene(scene);

        // Show the Stage
        addUserStage.show();
    }

    public void removeUser(MouseEvent mouseEvent) {
        Profile profile = (Profile) plist.getSelectionModel().getSelectedItem();
        if (profile != null) {
            try {
                ;
                edi.removeEmployee(profile.getId());
                refreshplist();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

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


    public void Editprofile(ActionEvent actionEvent) {
        if (loggedInUser.getPermissions().equals("admin")) ;
        {
            Stage stage = new Stage();
            GridPane pane = new GridPane();
            pane.setAlignment(Pos.CENTER);
            pane.setHgap(10);
            pane.setVgap(10);
            pane.setPadding(new Insets(25, 25, 25, 25));

            TextField nameField = new TextField();
            nameField.setText(loggedInUser.getProfile().getName());
            TextField positionField = new TextField();
            positionField.setText(loggedInUser.getProfile().getPosition());
            TextField departmentField = new TextField();
            departmentField.setText(loggedInUser.getProfile().getDepartment());
            Button editbutton = new Button("Ændre profil");

            pane.add(new Label("Navn:"), 0, 0);
            pane.add(nameField, 1, 0);
            pane.add(new Label("Position:"), 0, 1);
            pane.add(positionField, 1, 1);
            pane.add(new Label("Afdeling"), 0, 2);
            pane.add(departmentField, 1, 2);
            pane.add(editbutton, 1, 3);

            editbutton.setOnAction(e -> {
                loggedInUser.getProfile().setName(nameField.getText());
                loggedInUser.getProfile().setPosition(positionField.getText());
                loggedInUser.getProfile().setDepartment(departmentField.getText());
                try {
                    edi.editprofile(nameField.getText(), positionField.getText(), departmentField.getText(), loggedInUser.getProfile().getId());
                    refreshplist();
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

    public void doubleclickeventplist() {
        plist.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2) {
                Profile profile = (Profile) plist.getSelectionModel().getSelectedItem();
                if (profile != null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Profil Information");
                    alert.setHeaderText("Profile: " + profile.getName());
                    alert.setContentText("Navn: " + profile.getName() + "\n" +
                            "Position: " + profile.getPosition() + "\n" +
                            "Afdeling: " + profile.getDepartment());


                    // Tilføj en knap til at invitere brugeren
                    ButtonType invitebutton = new ButtonType("inviter", ButtonBar.ButtonData.OK_DONE);
                    alert.getButtonTypes().add(invitebutton);
                    Button inviteButtonNode = (Button) alert.getDialogPane().lookupButton(invitebutton);
                    inviteButtonNode.addEventFilter(ActionEvent.ACTION, event2 -> {
                        System.out.println("Invite knap klikket for profil: " + profile.getName());
                        event2.consume(); // This prevents the alert from closing
                    });

                    alert.showAndWait();
                }

            }

        });
    }


    public void initializeplist() {
        plistc1.setCellValueFactory(new PropertyValueFactory<>("name"));
        plistc2.setCellValueFactory(new PropertyValueFactory<>("position"));
        plistc3.setCellValueFactory(new PropertyValueFactory<>("department"));
        ;

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


    public void filterplist() {


        FilteredList<Profile> filteredProfiles = new FilteredList<>(profiles, p -> true);


        personSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredProfiles.setPredicate(profile -> {
                // If filter text is empty, display all profiles.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (profile.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches name.
                } else if (profile.getPosition().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches position.
                } else if (profile.getDepartment().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches department.
                }
                return false; // Does not match.
            });
        });


        SortedList<Profile> sortedProfiles = new SortedList<>(filteredProfiles);

        sortedProfiles.comparatorProperty().bind(plist.comparatorProperty());

        plist.setItems(sortedProfiles);
    }


    public void notifi(List<String> messages) { //virker kun hvis du er logged in med en profil
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notifikationer");
        alert.setHeaderText("Her vises dine notifikationer");

        // Opret TextArea til at vise beskeder
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);

        if (not.isNotificationsEnabled()) {
            // Hvis notifikationer er aktiveret, vis beskeder
            textArea.setText(String.join("\n", messages));
        } else {
            // Hvis notifikationer er deaktiveret, vis en standardbesked
            textArea.setText("Notifikationer er slukket");
        }

        // ScrollPane
        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setPrefWidth(400);
        scrollPane.setPrefHeight(200);

        // CheckBox for at tænde/slukke for notifikationer
        CheckBox checkBox = new CheckBox("Tænd for notifikationer");
        checkBox.setSelected(not.isNotificationsEnabled());
        checkBox.setOnAction(e -> {
            not.setNotificationsEnabled(checkBox.isSelected());
            // Opdater textArea baseret på brugerens valg
            if (checkBox.isSelected()) {
                textArea.setText(String.join("\n", messages));
            } else {
                textArea.setText("Notifikationer er slukket");
            }
        });

        // VBox til at organisere ScrollPane og CheckBox
        VBox contentBox = new VBox(10, scrollPane, checkBox);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setContent(contentBox);
        dialogPane.getStylesheets().add(Main.class.getResource("notiStyle.css").toExternalForm());
        dialogPane.getStyleClass().add("alert");

        alert.showAndWait();
    }

    @FXML
    private void notButtonPressed(ActionEvent event) {
        notifi(not.showMessages());
    }

    private void updateCalender(LocalDate date) {
        currentDate = date;
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
        }
    }

    private void dayView() {
        calendarGrid.getChildren().clear();
        calendarInfoLabel.setText(" ");

        // Gemmer dagens dato til sammenligning
        LocalDate today = LocalDate.now();

        // Opbygger viewet for den aktuelle dag
        VBox dayBox = new VBox();
        dayBox.setSpacing(10);
        dayBox.setPrefHeight(25);
        dayBox.setPrefWidth(840);
        dayBox.setAlignment(Pos.CENTER);
        dayBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #ffffff; -fx-border-color: #121212; -fx-border-width: 1; -fx-padding: 20;");

        // Formaterer og viser datoen
        String dayName = currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("da", "DK"));
        String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("EEEE, d. MMMM yyyy", new Locale("da", "DK")));

        Label dayLabel = new Label(dayName);
        dayLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        Label dateLabel = new Label(formattedDate);
        dateLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 16px;");

        dayBox.getChildren().addAll(dayLabel, dateLabel);

        if (darkMode) {
            dayBox.setStyle("-fx-background-color: #121212; -fx-text-fill: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1;");
            dateLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 16px; ");
        } else {
            dayBox.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-border-color: #121212; -fx-border-width: 1;");
        }

        //highlighter dagen i dag
        if (currentDate.equals(today)) {
            dayBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #ffdd55; -fx-border-color: #121212; -fx-border-width: 1; -fx-padding: 20;");
            dayLabel.setStyle("-fx-text-fill: #121212; -fx-font-size: 18px;");
            dateLabel.setStyle("-fx-text-fill: #121212; -fx-font-weight: bold; -fx-font-size: 16px;");
        }

        calendarGrid.add(dayBox, 0, 0);

        int projectRow = 1;
        for (Project project : projects) {
            boolean isProjectActiveToday = !currentDate.isBefore(project.getStartDate()) && !currentDate.isAfter(project.getEndDate());
            boolean isEventDay = currentDate.equals(project.getEventDate());

            if (isProjectActiveToday || isEventDay) {
                Label projectLabel = new Label(project.getName());
                projectLabel.getStyleClass().add("striped-background");
                projectLabel.setStyle("-fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.7;");
                projectLabel.setOnMouseClicked(event -> showProjectInfo(project));

                VBox projectBox = new VBox(projectLabel);
                projectBox.setPadding(new Insets(2));
                projectBox.getStyleClass().add("striped-background");
                projectBox.setStyle("-fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.7;");
                calendarGrid.add(projectBox, 0, projectRow++);

                if (isEventDay) {
                    projectLabel.setStyle("-fx-background-color: #0077CC; -fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.9;");
                    projectLabel.setText("Event for " + project.getName());
                    projectLabel.setOnMouseClicked(event -> showEventInfo(project));
                }

                // Check og highlight mødedatoer
                if (project.getMeetingDates() != null && project.getMeetingDates().contains(currentDate)) {
                    projectLabel.setStyle("-fx-background-color: #FF6347; -fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.9;");
                    projectLabel.setText("Møde til " + project.getName());
                    projectLabel.setOnMouseClicked(event -> showProjectInfo(project));
                }
            }
        }
    }

    private void weekView() {
        calendarGrid.getChildren().clear();

        // Definerer dagens dato, ugens start og slut
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = currentDate.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int week = currentDate.get(woy);
        calendarInfoLabel.setText("Uge: " + week);

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
                dayLabel.setStyle("-fx-text-fill: #121212");
                dateLabel.setStyle("-fx-text-fill: #121212; -fx-font-weight: bold");
            } else if (darkMode) {
                dayBox.setStyle("-fx-background-color: #121212; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10; -fx-text-fill: #ffffff");
                dateLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
            }

            calendarGrid.add(dayBox, i, 0);

            //Viser projekter, der spænder over den aktuelle dato
            for (Project project : projects) {
                int rowForProject = projectRowMap.getOrDefault(project.getName(), nextAvailableRow);
                if (!projectRowMap.containsKey(project.getName())) {
                    projectRowMap.put(project.getName(), nextAvailableRow++);
                }

                // Vis projekter
                if (!project.getStartDate().isAfter(date) && !project.getEndDate().isBefore(date)) {
                    int startCol = (int) ChronoUnit.DAYS.between(startOfWeek, project.getStartDate());
                    int endCol = (int) ChronoUnit.DAYS.between(startOfWeek, project.getEndDate());
                    startCol = Math.max(startCol, 0);
                    endCol = Math.min(endCol, 6);

                    VBox projectBox = new VBox(new Label(project.getName()));
                    projectBox.getStyleClass().add("striped-background");
                    projectBox.setStyle("-fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.7;");
                    projectBox.setOnMouseClicked(event -> showProjectInfo(project));
                    GridPane.setConstraints(projectBox, startCol, rowForProject, endCol - startCol + 1, 1);
                    GridPane.setFillWidth(projectBox, true);
                    calendarGrid.getChildren().add(projectBox);
                }

                // Vis eventdatoer
                if (project.getEventDate() != null && (int) ChronoUnit.DAYS.between(startOfWeek, project.getEventDate()) >= 0 && (int) ChronoUnit.DAYS.between(startOfWeek, project.getEventDate()) <= 6) {
                    int eventCol = (int) ChronoUnit.DAYS.between(startOfWeek, project.getEventDate());

                    VBox eventBox = new VBox(new Label("Event for " + project.getName()));
                    eventBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.7;");
                    eventBox.setOnMouseClicked(event -> showEventInfo(project));
                    GridPane.setConstraints(eventBox, eventCol, rowForProject, 1, 1);
                    GridPane.setFillWidth(eventBox, true);
                    calendarGrid.getChildren().add(eventBox);
                }

                // Vis møder
                if (project.getMeetingDates() != null) {
                    for (LocalDate meetingDate : project.getMeetingDates()) {
                        if (!meetingDate.isBefore(startOfWeek) && !meetingDate.isAfter(startOfWeek.plusDays(6))) {
                            int meetingCol = (int) ChronoUnit.DAYS.between(startOfWeek, meetingDate);

                            VBox meetingBox = new VBox(new Label("Møde for " + project.getName()));
                            meetingBox.setStyle("-fx-background-color: #FF6347; -fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.8;");
                            meetingBox.setOnMouseClicked(event -> showProjectInfo(project));
                            GridPane.setConstraints(meetingBox, meetingCol, rowForProject, 1, 1);
                            GridPane.setFillWidth(meetingBox, true);
                            calendarGrid.getChildren().add(meetingBox);
                        }
                    }
                }
            }
        }
    }

    private void monthView() {
        calendarGrid.getChildren().clear();

        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        LocalDate lastDayOfMonth = firstDayOfMonth.plusMonths(1).minusDays(1);
        YearMonth yearMonth = YearMonth.from(currentDate);
        int daysInMonth = yearMonth.lengthOfMonth();
        DayOfWeek startDayOfWeek = firstDayOfMonth.getDayOfWeek();
        int startCol = (startDayOfWeek.getValue() - 1) % 7;
        LocalDate gridStartDate = firstDayOfMonth.minusDays(startCol);
        int totalDays = daysInMonth + startCol;
        totalDays += (7 - (totalDays % 7)) % 7;

        String monthYear = currentDate.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("da", "DK")));
        calendarInfoLabel.setText("Måned: " + monthYear);

        Map<LocalDate, VBox> dayBoxes = new HashMap<>();

        for (int i = 0; i < totalDays; i++) {
            LocalDate date = gridStartDate.plusDays(i);
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, new Locale("da", "DK"));
            String formattedDate = date.format(DateTimeFormatter.ofPattern("dd MMM", new Locale("da", "DK")));

            VBox dayBox = new VBox();
            dayBox.setSpacing(5);
            dayBox.setPrefWidth(120);
            dayBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #ffffff; -fx-padding: 10;");


            if (date.isBefore(firstDayOfMonth) || date.isAfter(lastDayOfMonth)) {
                dayBox.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10;");
            } else if (date.equals(today)) {
                dayBox.setStyle("-fx-background-color: #ffdd55; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10;-fx-text-fill: #121212");
            }


            Label dayLabel = new Label(dayName + " " + formattedDate);
            dayLabel.setStyle("-fx-font-weight: bold;");
            dayBox.getChildren().add(dayLabel);

            if (darkMode) {
                if (date.isEqual(today)) {
                    dayBox.setStyle("-fx-background-color: #ffdd55; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10");
                    dayLabel.setStyle("-fx-text-fill: #121212; -fx-font-weight: bold");
                } else if (date.isBefore(firstDayOfMonth) || date.isAfter(lastDayOfMonth)) {
                    dayBox.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10;");
                    dayLabel.setStyle("-fx-text-fill: #121212; -fx-font-weight: bold");
                } else {
                    dayBox.setStyle("-fx-background-color: #121212; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10; -fx-text-fill: #ffffff");
                    dayLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
                }
            }
            dayBoxes.put(date, dayBox);

            int col = i % 7;
            int row = i / 7;
            calendarGrid.add(dayBox, col, row);
        }


        for (Project project : projects) {
            LocalDate startDate = project.getStartDate();
            LocalDate endDate = project.getEndDate();

            Set<LocalDate> specialDays = new HashSet<>();


            if (project.getEventDate() != null && dayBoxes.containsKey(project.getEventDate())) {
                specialDays.add(project.getEventDate());
            }
            if (project.getMeetingDates() != null) {
                for (LocalDate meetingDate : project.getMeetingDates()) {
                    if (dayBoxes.containsKey(meetingDate)) {
                        specialDays.add(meetingDate);
                    }
                }
            }


            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                if (!specialDays.contains(date)) {  // Kun tilføj hvis datoen ikke er en speciel dag
                    VBox dayBox = dayBoxes.get(date);
                    if (dayBox != null) {
                        Label projectLabel = new Label(project.getName());
                        projectLabel.getStyleClass().add("striped-background");
                        projectLabel.setStyle("-fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.7;");
                        projectLabel.setOnMouseClicked(event -> showProjectInfo(project));
                        dayBox.getChildren().add(projectLabel);
                    }
                }
            }

            // Highlight eventDate with a distinct style or add a special marker
            if (project.getEventDate() != null && dayBoxes.containsKey(project.getEventDate())) {
                VBox eventDayBox = dayBoxes.get(project.getEventDate());
                Label eventLabel = new Label("Event for " + project.getName());
                eventLabel.setStyle("-fx-background-color: #0077CC; -fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.8;");
                eventLabel.setOnMouseClicked(event -> showEventInfo(project));
                eventDayBox.getChildren().add(eventLabel);
            }

            if (project.getMeetingDates() != null) {
                for (LocalDate meetingDate : project.getMeetingDates()) {
                    if (dayBoxes.containsKey(meetingDate)) {
                        VBox meetingDayBox = dayBoxes.get(meetingDate);
                        Label meetingLabel = new Label("Møde for " + project.getName());
                        meetingLabel.setStyle("-fx-background-color: #FF6347; -fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.8;");
                        meetingLabel.setOnMouseClicked(event -> showProjectInfo(project));
                        meetingDayBox.getChildren().add(meetingLabel);
                    }
                }
            }
        }
    }

    // knappe events

    public void zoomOutPressed(ActionEvent event) {
        if (currentViewMode == ViewMode.DAG) {
            currentViewMode = ViewMode.UGE;
        } else if (currentViewMode == ViewMode.UGE) {
            currentViewMode = ViewMode.MÅNED;
        }

        updateCalender(currentDate);
    }


    public void zoomIndPressed(ActionEvent event) {
        if (currentViewMode == ViewMode.MÅNED) {
            currentViewMode = ViewMode.UGE;
        } else if (currentViewMode == ViewMode.UGE) {
            currentViewMode = ViewMode.DAG;
        }

        updateCalender(currentDate);
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
        updateCalender(currentDate);
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
        updateCalender(currentDate);
    }


    public TableView<Profile> targettable() {
        TableView<Profile> targetTable = new TableView<>();
        TableColumn<Profile, String> nameColumn = new TableColumn<>("Name");
        TableColumn<Profile, String> positionColumn = new TableColumn<>("Position");
        TableColumn<Profile, String> departmentColumn = new TableColumn<>("Department");

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));

        targetTable.getColumns().add(nameColumn);
        targetTable.getColumns().add(positionColumn);
        targetTable.getColumns().add(departmentColumn);



        return targetTable;
    }

    public TableView<Profile> initializecreatenewtable() {
        // Create a TableView for profiles
        TableView<Profile> profileTable = new TableView<>();
        TableColumn<Profile, String> nameColumn = new TableColumn<>("Navn");
        TableColumn<Profile, String> positionColumn = new TableColumn<>("Position");
        TableColumn<Profile, String> departmentColumn = new TableColumn<>("Afdeling");

        // Set cell value factories
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));

        // Add columns to the table
        profileTable.getColumns().add(nameColumn);
        profileTable.getColumns().add(positionColumn);
        profileTable.getColumns().add(departmentColumn);


        profileTable.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2) {
                Profile profile = (Profile) profileTable.getSelectionModel().getSelectedItem();
                if (profile != null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Profil Information");
                    alert.setHeaderText("Profil: " + profile.getName());
                    alert.setContentText("Navn: " + profile.getName() + "\n" +
                            "Position: " + profile.getPosition() + "\n" +
                            "Afdeling: " + profile.getDepartment());

                    // Tilføj en knap til at invitere brugeren
                    ButtonType invitebutton = new ButtonType("invite", ButtonBar.ButtonData.OK_DONE);
                    alert.getButtonTypes().add(invitebutton);
                    Button inviteButtonNode = (Button) alert.getDialogPane().lookupButton(invitebutton);
                    inviteButtonNode.addEventFilter(ActionEvent.ACTION, event2 -> {
                        System.out.println("Inviter knap blev trykket for profilen: " + profile.getName());
                        event2.consume(); // This prevents the alert from closing
                    });

                    alert.showAndWait();
                }

            }

        });

        profileTable.setRowFactory(tv -> new TableRow<Profile>() {
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

        // Set data to the table
        profileTable.setItems(profiles);
        return profileTable;
    }

    //oprettelse af projekter
    public void opretButtonPressed(ActionEvent event) {
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
        TableView Membertableview = initializecreatenewtable();
        pane.add(Membertableview, 2, 0);
        GridPane.setRowSpan(Membertableview, 9);
        TableView targettable = targettable();
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
                meetingDatePick.setValue(null);
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

            // Kun tilføje en mødedato, hvis den er valgt
            if (meetingDatePick.getValue() != null) {
                meetingDates.add(meetingDatePick.getValue());
            }

            Project project = new Project(
                    projectName, startDate, endDate, loggedInUser.getProfile().getId(),
                    notesArea.getText(), eventDate, meetingDates
            );
            project.setLocation(locationField.getText());
            project.setNotes(notesArea.getText());
            files.forEach(project::addFiles);
            try {
                cdi.addEvent(project.getName(), project.getStartDate().toString(), project.getEndDate().toString(), loggedInUser.getProfile().getId(), project.getNotes(), project.getEventDate().toString(), project.getMeetingDate().toString());

            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
          
            projects.add(project);
            updateCalender(currentDate);
            stage.close();
        });

        Scene scene = new Scene(pane);
        scene.getStylesheets().add
                (Main.class.getResource("notiStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void showProjectInfo(Project project) {
        Stage infoStage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label("Projektnavn: " + project.getName());
        Label locationLabel = new Label("Lokation: " + project.getLocation());
        Label startLabel = new Label("Startdato: " + project.getStartDate().toString());
        Label endLabel = new Label("Slutdato: " + project.getEndDate().toString());
        Label eventDayLabel = new Label("Dato for begivenhed: " + project.getEventDate().toString());
        Label notesLabel = new Label("Noter: " + project.getNotes());

        // Håndtering af projektets filer
        Label filesLabel = new Label("Filer:");
        VBox filesList = new VBox(5);
        for (String file : project.getFiles()) {
            filesList.getChildren().add(new Label(file));
        }

        Button knockButton = new Button("Banke på");
        knockButton.setOnAction(e -> project.requestKnock(loggedInUser));


        layout.getChildren().addAll(nameLabel, locationLabel, startLabel, endLabel, eventDayLabel, notesLabel, filesLabel, filesList, knockButton);

        Scene scene = new Scene(layout);
        infoStage.setTitle("Projektinformation");
        infoStage.setScene(scene);
        infoStage.sizeToScene();
        infoStage.show();
    }

    private void showEventInfo(Project project){
        Stage infoStage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label("Projektnavn: " + project.getName());
        Label locationLabel = new Label("Lokation: " + project.getLocation());
        Label eventDayLabel = new Label("Dato for begivenhed: " + project.getEventDate().toString());
        Label notesLabel = new Label("Noter: " + project.getNotes());
        Label personLabel = new Label("Disse personer: " + "deltager at dette event");

        Button knockButton = new Button("Banke på");
        knockButton.setOnAction(e -> project.requestKnock(loggedInUser));

        layout.getChildren().addAll(nameLabel, locationLabel, eventDayLabel, notesLabel, personLabel, knockButton);


        Scene scene = new Scene(layout);
        infoStage.setTitle("Eventinformation");
        infoStage.setScene(scene);
        infoStage.sizeToScene();
        infoStage.show();
    }

    @FXML
    private void handleToggleTheme(ActionEvent event) {
        Scene scene = ((MenuItem)event.getSource()).getParentPopup().getOwnerWindow().getScene();
        toggleTheme(scene);
    }

    public void todayButtonPressed(ActionEvent event) {
        currentDate = LocalDate.now();

        updateCalender(currentDate);
    }



    public void datePickerPressed(ActionEvent event) {
        if (datePicker.getValue() != null) {
            LocalDate selectedDate = datePicker.getValue();
            navigateToSelectedDate(selectedDate);
        }
    }

    private void navigateToSelectedDate(LocalDate date) {
        updateCalender(date);
    }




    //skift tema

    private void toggleTheme(Scene scene) {
        ObservableList<String> stylesheets = scene.getStylesheets();

        String lightThemePath = getClass().getResource("/org/example/kmanage/maincss.css").toExternalForm();
        String darkThemePath = getClass().getResource("/org/example/kmanage/mainCssDark.css").toExternalForm();

        if (lightThemePath == null || darkThemePath == null) {
            System.err.println("Din tråd til css er forkert");
            return;
        }


        if (stylesheets.contains(lightThemePath)) {
            // Skifter fra lys til mørk tema
            stylesheets.clear();
            stylesheets.add(darkThemePath);
            darkMode = true;

        } else {
            // Skifter fra mørk til lys tema eller initialiserer lys tema
            stylesheets.clear();
            stylesheets.add(lightThemePath);
            darkMode = false;

        }
        updateCalender(currentDate);
        updateSearchIcon(darkMode);
    }
    //ændre billeder(søge ikonet)
    private void updateSearchIcon(boolean darkMode) {
        String imagePath;


        if (darkMode) {
            imagePath = "/org/example/kmanage/soeg_hvid.png";
        } else {
            imagePath = "/org/example/kmanage/soeg.png";
        }
        try {
            Image newImage = new Image(getClass().getResourceAsStream(imagePath));
            personSearchButton.setImage(newImage);
        } catch (NullPointerException e) {
            System.err.println("Kan ikke loade billede: " + imagePath);
            e.printStackTrace();
        }
    }
}