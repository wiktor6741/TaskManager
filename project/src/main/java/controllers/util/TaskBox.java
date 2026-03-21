package controllers.util;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.awt.Color.red;

public class TaskBox extends HBox {

    private final Task task; // referencja do taska

    public TaskBox(Task task) {
        this.task = task;

        setAlignment(Pos.CENTER_LEFT);


        setStyle("-fx-background-color: #ffffff; -fx-padding: 10; -fx-border-radius: 5;");
        setSpacing(10);

        Label nameLabel = new Label(task.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        getChildren().add(nameLabel);

        if (task.getDeadline() != null) {
            ClockIcon clockIcon = new ClockIcon(20, getClockColor());
            getChildren().add(clockIcon);
        }
        if (task.getPriority() != null) {
            ImportanceIndicator importanceIndicator = new ImportanceIndicator(task.getPriority(), 5);
            getChildren().add(importanceIndicator);
        }

        addEventHandler(MouseEvent.MOUSE_ENTERED, e -> setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10; -fx-border-radius: 5;"));
        addEventHandler(MouseEvent.MOUSE_EXITED, e -> setStyle("-fx-background-color: #ffffff; -fx-padding: 10; -fx-border-radius: 5;"));
    }

    private Color getClockColor() {
        LocalDateTime deadline = task.getDeadline();
        Duration timeLeft = Duration.between(LocalDateTime.now(), deadline);

        if (timeLeft.isNegative()) {
            return Color.BLACK;
        }
        if (timeLeft.toDays() < 2) {
            return Color.RED;
        }

        if (timeLeft.toDays() < 7){
            return Color.ORANGE;
        }
        return Color.GREEN;
    }

    public Task getTask() {
        return task;
    }
}
