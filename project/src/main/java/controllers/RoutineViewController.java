package controllers;

import controllers.util.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import model.Routine;
import model.RoutineService;
import util.ElementTimePair;
import util.Weekday;
import mainapp.MainApp;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RoutineViewController {
    private RoutineService routineService;
    private mainapp.MainApp mainApp;
    @FXML
    private Pane dayColumn;
    @FXML
    private StackPane stackPane;
    private Pane hourGrid;

    @FXML private ScrollPane scrollPane;

    @FXML private ComboBox<Routine> routineComboBox;

    @FXML
    private Button weekDownButton, weekUpButton, backButton, newRoutineButton, elementButton, insertElementButton, routineEditButton;
    @FXML
    private Label weekNumLabel;

    private static final double HOUR_HEIGHT = 60;
    private static final double LABEL_WIDTH = 50;

    private List<RoutineBox> routineBoxes;

    @FXML
    private void initialize() {
        CalendarBackground bg = buildCalendarBackground();
        scrollPane.setContent(bg.container());
        hourGrid = bg.gridArea();
        routineBoxes = new ArrayList<>();

        Platform.runLater(() -> {
            System.out.println("stackPane width: " + stackPane.getWidth());
            System.out.println("ScrollPane width: " + scrollPane.getWidth());
            System.out.println("hourgrid width: " + hourGrid.getWidth());
        });

        weekUpButton.setOnMouseClicked(e -> incrementWeek());
        weekDownButton.setOnMouseClicked(e -> decrementWeek());
        backButton.setOnMouseClicked(e -> {
            try {
                mainApp.loadTaskView();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        insertElementButton.setOnMouseClicked(e ->{ if (routineService.getCurrentViewedRoutine() != null){ showInsertPopup();}});
        newRoutineButton.setOnMouseClicked(e -> showNewRoutinePopup());
        routineEditButton.setOnMouseClicked(e -> { if (routineService.getCurrentViewedRoutine() != null){ showRoutineEditPopup();}});
        elementButton.setOnMouseClicked(e -> showElementPopup());
    }

    public void init(RoutineService routineService, MainApp mainApp){
        this.routineService = routineService;
        loadWeek();
        routineComboBox.setItems(FXCollections.observableArrayList(routineService.getRoutineList()));
        if (routineService.getCurrentViewedRoutine() != null){
            routineComboBox.setValue(routineService.getCurrentViewedRoutine());
        }
        routineComboBox.setOnAction(e -> {
            System.out.println("combo box action");
            Routine selected = routineComboBox.getValue();
            if (selected != null) {
                switchRoutine(selected);
            }});
        this.mainApp = mainApp;
    }

    private void loadWeek(){
        clearRoutineBoxes();
        List<ElementTimePair> pairs = routineService.getWeek();
        weekNumLabel.setText("Week " + routineService.getWeekNum());
        System.out.println("Weeknum in loadweek: " + routineService.getWeekNum());
        if (pairs != null){
            for (ElementTimePair pair : pairs){
                addRoutineBox(pair);
            }
        }
    }

    private void switchRoutine(Routine routine){
        routineService.setCurrentViewedRoutine(routine);
        loadWeek();
    }

    private void clearRoutineBoxes(){
        for (RoutineBox box : routineBoxes){
            hourGrid.getChildren().remove(box);
        }
        routineBoxes.clear();
    }

    private void incrementWeek(){
        routineService.incrementWeek();
        loadWeek();
    }

    private void decrementWeek(){
        routineService.decrementWeek();
        loadWeek();
    }

    private record CalendarBackground(HBox container, Pane gridArea) {}

    private CalendarBackground buildCalendarBackground() {
        HBox background = new HBox();
        background.setPrefHeight(24 * HOUR_HEIGHT);

        // --- Left: hour labels (fixed width) ---
        Pane labelColumn = new Pane();
        labelColumn.setPrefWidth(LABEL_WIDTH);
        labelColumn.setMinWidth(LABEL_WIDTH);
        labelColumn.setMaxWidth(LABEL_WIDTH);

        for (int hour = 0; hour <= 24; hour++) {
            Label label = new Label(String.format("%02d:00", hour));
            label.getStyleClass().add("hour-label");
            label.setLayoutX(5);
            label.setLayoutY(hour * HOUR_HEIGHT - 8);
            labelColumn.getChildren().add(label);
        }

        Pane gridArea = new Pane();
        HBox.setHgrow(gridArea, Priority.ALWAYS);

        for (int hour = 0; hour <= 24; hour++) {
            double y = hour * HOUR_HEIGHT;
            Line line = new Line(0, y, 0, y);
            line.endXProperty().bind(gridArea.widthProperty());
            line.getStyleClass().add("hour-line");
            gridArea.getChildren().add(line);
        }

        background.getChildren().addAll(labelColumn, gridArea);

        return new CalendarBackground(background, gridArea);
    }

    private void addRoutineBox(ElementTimePair pair) {
        System.out.println("adding box for element " + pair.element().getName() + "at time " + pair.timeSpec());
        LocalTime start = pair.timeSpec().start();
        LocalTime end = pair.timeSpec().end();
        Weekday weekday = pair.timeSpec().weekday();
        double startY = timeToY(start);
        double endY = timeToY(end);
        double height = endY - startY;
        RoutineBox box = new RoutineBox(pair, height);
        box.layoutXProperty().bind(hourGrid.widthProperty().divide(7).multiply(weekday.toInt()));
        box.setLayoutY(startY);

        box.setPrefHeight(height);
        box.setMaxHeight(height);
        box.setMinHeight(height);

        box.prefWidthProperty().bind(hourGrid.widthProperty().divide(7));
        box.setOnClick(p -> {showElementPairPopup(p);});
        routineBoxes.add(box);
        hourGrid.getChildren().add(box);
    }

    private double timeToY(LocalTime time) {
        return time.toSecondOfDay() / 3600.0 * HOUR_HEIGHT;
    }

    private void showElementPairPopup(ElementTimePair pair) {
        ElementPairPopupBox popup = new ElementPairPopupBox(pair, routineService, this::loadWeek);
        showPopup(popup);
    }

    private void showInsertPopup(){
        InsertElementPopupBox popup = new InsertElementPopupBox(routineService);
        showPopup(popup);
    }

    private void showNewRoutinePopup(){
        RoutinePopupBox popup = new NewRoutinePopupBox(routineService);
        showPopup(popup);
    }

    private void showRoutineEditPopup(){
        RoutineEditPopupBox popup = new RoutineEditPopupBox(routineService);
        showPopup(popup);
    }

    private void showElementPopup(){
        ElementPopupBox popup = new ElementPopupBox(routineService);
        showPopup(popup);
    }

    private void showPopup(PopupBox popup){
        Pane overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");
        overlay.setPickOnBounds(true);
        popup.setMaxSize(400, 300);
        stackPane.getChildren().addAll(overlay, popup);
        overlay.setOnMouseClicked(e -> stackPane.getChildren().removeAll(overlay, popup));
        if (popup instanceof ElementPairPopupBox){
            ((ElementPairPopupBox) popup).setOnDelete(() -> stackPane.getChildren().removeAll(overlay, popup));
        }
        if (popup instanceof InsertElementPopupBox){
            ((InsertElementPopupBox) popup).setOnCancel(() -> stackPane.getChildren().removeAll(overlay, popup));
            ((InsertElementPopupBox) popup).setOnSave(() -> {loadWeek(); stackPane.getChildren().removeAll(overlay, popup);});
        }
        if (popup instanceof RoutinePopupBox){
            popup.setMaxSize(400, 150);
            ((RoutinePopupBox) popup).setOnCancel(() -> stackPane.getChildren().removeAll(overlay, popup));
        }

        if (popup instanceof  NewRoutinePopupBox){
            ((NewRoutinePopupBox) popup).setOnSave(routine -> {switchRoutine(routine);
                routineComboBox.getItems().add(routine); routineComboBox.setValue(routine);
                stackPane.getChildren().removeAll(overlay, popup);});
        }

        if (popup instanceof RoutineEditPopupBox){
            ((RoutineEditPopupBox) popup).setOnSave(() -> {
                loadWeek();
                int idx = routineComboBox.getItems().indexOf(routineService.getCurrentViewedRoutine());
                if (idx >= 0) {
                    routineComboBox.getItems().set(idx, routineService.getCurrentViewedRoutine());
                }
                stackPane.getChildren().removeAll(overlay, popup);
            });
            ((RoutineEditPopupBox) popup).setOnDelete(routine -> {routineComboBox.getItems().remove(routine);
            routineComboBox.setValue(routineService.getCurrentViewedRoutine()); stackPane.getChildren().removeAll(overlay, popup);
            loadWeek();});
        }

        if (popup instanceof ElementPopupBox){
            popup.setMaxSize(400, 130);
            ((ElementPopupBox) popup).setOnEdit(() -> {loadWeek(); System.out.println("onEdit Run");});
        }
    }
}
