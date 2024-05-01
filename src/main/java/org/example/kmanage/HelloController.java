package org.example.kmanage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class HelloController {



    @FXML
    private GridPane calendarGrid;



    private enum ViewMode { DAG, UGE, MÅNED, TRE_MÅNEDER }
    private ViewMode currentViewMode = ViewMode.DAG;

    @FXML
    private Button zoomInd, zoomOut;

    User loggedInUser = UserSession.getInstance(null).getUser();
    public void initialize() {
        updateCalender();
    }


    @FXML
    private void notButtonPressed(ActionEvent event) {
        System.out.println("test test");
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
        //array med uge navne
        String[] daysOfWeek = {"Onsdag"};
        // int array med datoer (dette er ikke rigtige datoer)
        int[] dates = {8};

        //looper igennem hver dag af ugen
        for (int i = 0; i < daysOfWeek.length; i++) {
            //laver vbox med labels
            VBox dayBox = new VBox();
            dayBox.setSpacing(1);
            //styler vbox
            dayBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #ffffff; -fx-padding: 300;");

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

    private void weekView(){
        //array med uge navne
        String[] daysOfWeek = {"Mandag", "Tirsdag", "Onsdag", "Torsdag", "Fredag"};
        // int array med datoer (dette er ikke rigtige datoer)
        int[] dates = {6, 7, 8, 9, 10};

        //looper igennem hver dag af ugen
        for (int i = 0; i < daysOfWeek.length; i++) {
            //laver vbox med labels
            VBox dayBox = new VBox();
            dayBox.setSpacing(1);
            //styler vbox
            dayBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #ffffff; -fx-padding: 65;");

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

    private void monthView(){
        //array med uge navne
        String[] daysOfWeek = {"Mandag", "Tirsdag", "Onsdag", "Torsdag", "Fredag", "Lørdag", "Søndag"};
        // int array med datoer (dette er ikke rigtige datoer)
        int[] dates = {6, 7, 8, 9, 10, 11, 12};

        //looper igennem hver dag af ugen
        for (int i = 0; i < daysOfWeek.length; i++) {
            //laver vbox med labels
            VBox dayBox = new VBox();
            dayBox.setSpacing(1);
            //styler vbox
            dayBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #ffffff; -fx-padding: 40;");

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
}