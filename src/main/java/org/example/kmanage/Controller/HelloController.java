package org.example.kmanage.Controller;


import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.kmanage.Classes.*;
import org.example.kmanage.DAO.*;
import org.example.kmanage.Main;
import org.example.kmanage.Notifications.Notification;
import org.example.kmanage.User.*;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;

import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.time.temporal.ChronoUnit;
import java.util.*;
public class HelloController {

    @FXML
    private GridPane calendarGrid = new GridPane();
    @FXML
    private Label calendarInfoLabel = new Label();
    @FXML
    private ImageView personSearchButton;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button zoomInd, zoomOut, opretButton;
    public TextField personSearchField;
    public Button adduserbutton;
    public Button removeuserbutton;
    public TableView plist;
    public TableColumn plistc1;
    public TableColumn plistc2;
    public TableColumn plistc3;
    private boolean darkMode = false;
    private LocalDate currentDate = LocalDate.now();
    CalenderDAO cdi = new CalenderDAOimp();
    PlistDAO pdi = new PlistDAOimp();
    Notification not = new Notification();
    ProfileDAO edi = new ProfileDAOImp();
    User loggedInUser = UserSession.getInstance(null).getUser();
    private ObservableList<Profile> profiles = pdi.getprofile();
    private ObservableList<Project> projects = FXCollections.observableArrayList();
    private enum ViewMode {DAG, UGE, MÅNED} //test
    private ViewMode currentViewMode = ViewMode.UGE;


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

    public LocalDate getCurrentDate() {
        return currentDate;
    }


    public ObservableList<Project> getProjects() {
        return projects;
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
        Adduser adduser = new Adduser(mouseEvent);
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
            Editprofile editprofile = new Editprofile(actionEvent);
            refreshplist();
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


    public void addProject(Project project) {
        this.projects.add(project);
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
        Createproject createproject = new Createproject(event);
    }

    public void showProjectInfo(Project project) {
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

    public void showEventInfo(Project project){
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

}