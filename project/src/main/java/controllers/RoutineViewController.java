package controllers;

import controllers.util.RoutineBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import model.RoutineElement;
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
        RoutineBox routineBox = new RoutineBox(element, timeSpec);
        addRoutineBox(routineBox, LocalTime.of(15, 0), LocalTime.of(19, 30), Weekday.FRI);

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

    private void addRoutineBox(RoutineBox box, LocalTime start, LocalTime end, Weekday weekday) {
        double startY = timeToY(start);
        double endY = timeToY(end);
        double height = endY - startY;
        box.layoutXProperty().bind(hourGrid.widthProperty().divide(7).multiply(weekday.toInt()));
        box.setLayoutY(startY);  // vertical position = start time, fixed

        box.setPrefHeight(height); // fixed height — duration doesn't change with window size

        // width scales with the grid's actual width, minus a small margin on both sides
        box.prefWidthProperty().bind(hourGrid.widthProperty().divide(7));

        hourGrid.getChildren().add(box);
    }

    private double timeToY(LocalTime time) {
        return time.toSecondOfDay() / 3600.0 * HOUR_HEIGHT;
    }

}
