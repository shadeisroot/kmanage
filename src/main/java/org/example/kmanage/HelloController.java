package org.example.kmanage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class HelloController {

@FXML
private GridPane calendarGrid;


    public void initialize() {
        //array med uge navne
        String[] daysOfWeek = {"Mandag", "Tirsdag", "Onsdag", "Torsdag", "Fredag"};
        // int array med datoer (dette er ikke rigtige datoer)
        int[] dates = {6, 7, 8, 9, 10};

        //looper igennem hver dag af ugen
        for (int i = 0; i < daysOfWeek.length; i++) {
            //laver vbox med labels
            VBox dayBox = new VBox();
            dayBox.setSpacing(5);
            //styler vbox
            dayBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #ffffff; -fx-padding: 10;");

            //label for hvilken dag + label for hvilken dato
            Label dayLabel = new Label(daysOfWeek[i]);
            dayLabel.setStyle("-fx-font-weight: bold;");

            Label dateLabel = new Label(String.valueOf(dates[i]));
            dateLabel.setStyle("-fx-text-fill: #666666;");

            //tilføjer labels til vbox
            dayBox.getChildren().addAll(dayLabel, dateLabel);
            //tilføjer vbox til grid
            calendarGrid.add(dayBox, i, 0);
        }
    }

    @FXML
    private void notButtonPressed(ActionEvent event) {
        System.out.println("test test");
    }
}