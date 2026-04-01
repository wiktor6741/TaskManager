package controllers.util;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Consumer;

import static java.awt.Color.red;

public class TaskBox extends HBox {

    private final Task task;

    private Consumer<Task> onClick;
    private Consumer<Task> onComplete;
    private boolean selected = false;

    public TaskBox(Task task) {
        this.task = task;

        setAlignment(Pos.CENTER_LEFT);


        getStyleClass().add("task-box");

        Label nameLabel = new Label(task.getName());
        getChildren().add(nameLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        CheckBox checkBox = new CheckBox();
        checkBox.setOnMouseClicked(e -> e.consume());
        checkBox.setOnAction(e -> {
            if (checkBox.isSelected() && onComplete != null) {
                onComplete.accept(task); // Przekazujemy taska do usunięcia
            }
        });

        if (task.getDeadline() != null) {
            ClockIcon clockIcon = new ClockIcon(20, getClockColor());
            getChildren().add(clockIcon);
        }
        if (task.getPriority() != null) {
            ImportanceIndicator importanceIndicator = new ImportanceIndicator(task.getPriority(), 5);
            getChildren().add(importanceIndicator);
        }

        getChildren().add(spacer);
        getChildren().add(checkBox);

        addEventHandler(MouseEvent.MOUSE_ENTERED, e -> setStyle("-fx-background-color: #1b1e24; -fx-padding: 10; -fx-border-radius: 5;"));

        addEventHandler(MouseEvent.MOUSE_EXITED, e -> { if (!selected) {
            setStyle("-fx-background-color: #2d3138; -fx-padding: 10; -fx-border-radius: 5;");
        }});

        setOnMouseClicked(e -> { if (onClick != null) { onClick.accept(task); } });


    }
    public void setOnComplete(Consumer<Task> onComplete) {
        this.onComplete = onComplete;
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

    public void setOnClick(Consumer<Task> onClick) {
        this.onClick = onClick;
    }

    public Task getTask() {
        return task;
    }

    public void setSelected(boolean selected){

        this.selected = selected;
        if (!selected){
            setStyle("-fx-background-color: #2d3138; -fx-padding: 10; -fx-border-radius: 5;");
        }
        else {
            System.out.println("Selected box for task: " + task.getName());
            setStyle("-fx-background-color: #1b1e24; -fx-padding: 10; -fx-border-radius: 5;");
        }
    }
}
