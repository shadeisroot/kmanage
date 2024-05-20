package org.example.kmanage.Classes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.kmanage.Controller.HelloController;
import org.example.kmanage.User.Project;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class DayView {

    private LocalDate currentDate = LocalDate.now();
    HelloController controller = new HelloController();

    public DayView(HelloController controller) {

        controller.getCalendarGrid().getChildren().clear();
        controller.getCalendarInfoLabel().setText(" ");

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

        if (controller.isDarkMode()) {
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

        controller.getCalendarGrid().add(dayBox, 0, 0);

        int projectRow = 1;
        for (Project project : controller.getProjects()) {
            boolean isProjectActiveToday = !currentDate.isBefore(project.getStartDate()) && !currentDate.isAfter(project.getEndDate());
            boolean isEventDay = currentDate.equals(project.getEventDate());

            if (isProjectActiveToday || isEventDay) {
                Label projectLabel = new Label(project.getName());
                projectLabel.getStyleClass().add("striped-background");
                projectLabel.setStyle("-fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.7;");
                projectLabel.setOnMouseClicked(event -> controller.showProjectInfo(project));

                VBox projectBox = new VBox(projectLabel);
                projectBox.setPadding(new Insets(2));
                projectBox.getStyleClass().add("striped-background");
                projectBox.setStyle("-fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.7;");
                controller.getCalendarGrid().add(projectBox, 0, projectRow++);

                if (isEventDay) {
                    projectLabel.setStyle("-fx-background-color: #0077CC; -fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.9;");
                    projectLabel.setText("Event for " + project.getName());
                    projectLabel.setOnMouseClicked(event -> controller.showEventInfo(project));
                }

                // Check og highlight mødedatoer
                if (project.getMeetingDates() != null && project.getMeetingDates().contains(currentDate)) {
                    projectLabel.setStyle("-fx-background-color: #FF6347; -fx-padding: 5; -fx-border-color: black; -fx-opacity: 0.9;");
                    projectLabel.setText("Møde til " + project.getName());
                    projectLabel.setOnMouseClicked(event -> controller.showProjectInfo(project));
                }
            }
        }
    }
    }
