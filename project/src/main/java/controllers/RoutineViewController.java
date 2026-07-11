package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class RoutineViewController {
    @FXML
    private Pane dayColumn;
    @FXML private ScrollPane scrollPane;
    private static final double HOUR_HEIGHT = 60;

    @FXML
    private void initialize() {
        drawHourLines();
    }

    private void drawHourLines() {
        for (int hour = 0; hour <= 24; hour++) {
            double y = hour * HOUR_HEIGHT;
            Line line = new Line(0, y, 0, y);
            line.endXProperty().bind(scrollPane.widthProperty()); // bind to the ScrollPane, not dayColumn
            line.getStyleClass().add("hour-line");
            dayColumn.getChildren().add(line);
        }
        dayColumn.setPrefHeight(24 * HOUR_HEIGHT);
    }
}
