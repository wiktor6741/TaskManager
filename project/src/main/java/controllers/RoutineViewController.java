package controllers;

import controllers.util.RoutineBox;
import controllers.util.RoutinePopupBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import model.Routine;
import model.RoutineElement;
import util.ElementTimePair;
import util.RoutineTimeSpec;
import util.Weekday;

import java.time.LocalTime;

public class RoutineViewController {
    @FXML
    private Pane dayColumn;
    @FXML
    private StackPane stackPane;
    private Pane hourGrid;

    @FXML private ScrollPane scrollPane;
    private static final double HOUR_HEIGHT = 60;
    private static final double LABEL_WIDTH = 50;
    @FXML
    private void initialize() {
        CalendarBackground bg = buildCalendarBackground();
        scrollPane.setContent(bg.container());
        hourGrid = bg.gridArea();
        RoutineElement element = new RoutineElement("umyc zeby");
        RoutineTimeSpec timeSpec = new RoutineTimeSpec(LocalTime.of(15, 0), LocalTime.of(19, 30), Weekday.FRI, 1);
        addRoutineBox(new ElementTimePair(element, timeSpec));

        Platform.runLater(() -> {
            System.out.println("stackPane width: " + stackPane.getWidth());
            System.out.println("ScrollPane width: " + scrollPane.getWidth());
            System.out.println("hourgrid width: " + hourGrid.getWidth());
        });
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
        RoutineBox box = new RoutineBox(pair);
        LocalTime start = pair.timeSpec().start();
        LocalTime end = pair.timeSpec().end();
        Weekday weekday = pair.timeSpec().weekday();
        double startY = timeToY(start);
        double endY = timeToY(end);
        double height = endY - startY;
        box.layoutXProperty().bind(hourGrid.widthProperty().divide(7).multiply(weekday.toInt()));
        box.setLayoutY(startY);

        box.setPrefHeight(height);

        box.prefWidthProperty().bind(hourGrid.widthProperty().divide(7));
        box.setOnClick(p -> {showEditPopup(p);});
        hourGrid.getChildren().add(box);
    }

    private double timeToY(LocalTime time) {
        return time.toSecondOfDay() / 3600.0 * HOUR_HEIGHT;
    }

    private void showEditPopup(ElementTimePair pair) {
        // 1. Dimming overlay — covers the whole StackPane
        Pane overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");
        overlay.setPickOnBounds(true); // ensures clicks anywhere on it register, even on "empty" areas


        RoutinePopupBox popup = new RoutinePopupBox(pair);
        popup.setMaxSize(400, 300); // keeps it from stretching to fill the StackPane


        // StackPane centers children by default — this popup will sit in the middle automatically
        stackPane.getChildren().addAll(overlay, popup);

        // click-outside-to-close: clicking the overlay (not the popup) removes both
        overlay.setOnMouseClicked(e -> stackPane.getChildren().removeAll(overlay, popup));
    }

}
