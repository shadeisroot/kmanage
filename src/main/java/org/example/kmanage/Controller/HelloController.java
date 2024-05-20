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
import org.example.kmanage.Classes.Createproject;
import org.example.kmanage.Classes.DayView;
import org.example.kmanage.Classes.Monthview;
import org.example.kmanage.Classes.Weekview;
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
    private GridPane calendarGrid = new GridPane();
    @FXML
    private Label calendarInfoLabel = new Label();
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
    private DayView dayView;
    private Weekview weekView;
    private Monthview monthView;

    //-------------------------------------------------------------------------------


    public void initialize() throws Exception {
        setWeekView(new Weekview(this));
        initializeplist();
        updateCalender(LocalDate.now());
        filterplist();
        doubleclickeventplist();
        initializebutton();
        initializeevents();

    }

    public void setDayView(DayView dayView) {
        this.dayView = dayView;
    }
    public void setWeekView(Weekview weekView) {
        this.weekView = weekView;
    }
    public void setMonthView(Monthview monthView) {
        this.monthView = monthView;
    }
    public ObservableList<Project> getProjects() {
        return projects;
    }
    public boolean isDarkMode() {
        return darkMode;
    }

    public GridPane getCalendarGrid() {
        return calendarGrid;
    }

    public Label getCalendarInfoLabel() {
        return calendarInfoLabel;
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
                setDayView(new DayView(this));
                break;
            case UGE:
                setWeekView(new Weekview(this));
                break;
            case MÅNED:
                setMonthView(new Monthview(this));
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
}