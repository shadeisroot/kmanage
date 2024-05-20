package org.example.kmanage.Classes;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.example.kmanage.Controller.HelloController;
import org.example.kmanage.User.Project;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Weekview {

    private LocalDate currentDate = LocalDate.now();
    HelloController controller = new HelloController();

    public Weekview(HelloController controller) {
        controller.getCalendarGrid().getChildren().clear();

        // Definerer dagens dato, ugens start og slut
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = currentDate.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int week = currentDate.get(woy);
            controller.getCalendarInfoLabel().setText("Uge: " + week);

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
            } else if (controller.isDarkMode()) {
                dayBox.setStyle("-fx-background-color: #121212; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10; -fx-text-fill: #ffffff");
                dateLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
            }

            controller.getCalendarGrid().add(dayBox, i, 0);

            //Viser projekter, der spænder over den aktuelle dato
            for (Project project : controller.getProjects()) {
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
                    projectBox.setOnMouseClicked(event -> controller.showProjectInfo(project));
                    GridPane.setConstraints(projectBox, startCol, rowForProject, endCol - startCol + 1, 1);
                    GridPane.setFillWidth(projectBox, true);
                    controller.getCalendarGrid().getChildren().add(projectBox);
                }

                // Vis eventdatoer
                if (project.getEventDate() != null && (int) ChronoUnit.DAYS.between(startOfWeek, project.getEventDate()) >= 0 && (int) ChronoUnit.DAYS.between(startOfWeek, project.getEventDate()) <= 6) {
                    int eventCol = (int) ChronoUnit.DAYS.between(startOfWeek, project.getEventDate());

                    VBox eventBox = new VBox(new Label("Event for " + project.getName()));
                    eventBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.7;");
                    eventBox.setOnMouseClicked(event -> controller.showEventInfo(project));
                    GridPane.setConstraints(eventBox, eventCol, rowForProject, 1, 1);
                    GridPane.setFillWidth(eventBox, true);
                    controller.getCalendarGrid().getChildren().add(eventBox);
                }

                // Vis møder
                if (project.getMeetingDates() != null) {
                    for (LocalDate meetingDate : project.getMeetingDates()) {
                        if (!meetingDate.isBefore(startOfWeek) && !meetingDate.isAfter(startOfWeek.plusDays(6))) {
                            int meetingCol = (int) ChronoUnit.DAYS.between(startOfWeek, meetingDate);

                            VBox meetingBox = new VBox(new Label("Møde for " + project.getName()));
                            meetingBox.setStyle("-fx-background-color: #FF6347; -fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.8;");
                            meetingBox.setOnMouseClicked(event -> controller.showProjectInfo(project));
                            GridPane.setConstraints(meetingBox, meetingCol, rowForProject, 1, 1);
                            GridPane.setFillWidth(meetingBox, true);
                            controller.getCalendarGrid().getChildren().add(meetingBox);
                        }
                    }
                }
            }
        }
    }
}
