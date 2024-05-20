package org.example.kmanage.Classes;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.kmanage.Controller.HelloController;
import org.example.kmanage.User.Project;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;


public class Monthview {

    private LocalDate currentDate = LocalDate.now();
    HelloController controller = new HelloController();
    public Monthview(HelloController controller) {
        controller.getCalendarGrid().getChildren().clear();

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
        controller.getCalendarInfoLabel().setText("Måned: " + monthYear);

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

            if (controller.isDarkMode()) {
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
            controller.getCalendarGrid().add(dayBox, col, row);
        }


        for (Project project : controller.getProjects()) {
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
                        projectLabel.setOnMouseClicked(event -> controller.showProjectInfo(project));
                        dayBox.getChildren().add(projectLabel);
                    }
                }
            }

            // Highlight eventDate with a distinct style or add a special marker
            if (project.getEventDate() != null && dayBoxes.containsKey(project.getEventDate())) {
                VBox eventDayBox = dayBoxes.get(project.getEventDate());
                Label eventLabel = new Label("Event for " + project.getName());
                eventLabel.setStyle("-fx-background-color: #0077CC; -fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.8;");
                eventLabel.setOnMouseClicked(event -> controller.showEventInfo(project));
                eventDayBox.getChildren().add(eventLabel);
            }

            if (project.getMeetingDates() != null) {
                for (LocalDate meetingDate : project.getMeetingDates()) {
                    if (dayBoxes.containsKey(meetingDate)) {
                        VBox meetingDayBox = dayBoxes.get(meetingDate);
                        Label meetingLabel = new Label("Møde for " + project.getName());
                        meetingLabel.setStyle("-fx-background-color: #FF6347; -fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.8;");
                        meetingLabel.setOnMouseClicked(event -> controller.showProjectInfo(project));
                        meetingDayBox.getChildren().add(meetingLabel);
                    }
                }
            }
        }
    }
}
