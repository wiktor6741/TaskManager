package controllers.util;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.RoutineElement;
import model.RoutineService;
import util.ElementTimePair;
import util.Weekday;

import java.util.ArrayList;
import java.util.List;

public class RoutinePopupBox extends VBox {
    private ElementTimePair pair;
    private final Label elementNameLabel, timeLabel, dayLabel;
    private final Button editButton, deleteButton, saveButton, cancelButton;
    //private final RoutineService routineService;
    private final List<Node> infoNodes;
    private final List<Node> editNodes;

    public RoutinePopupBox(ElementTimePair pair){
        getStyleClass().add("routine-popup-box");
        //this.routineService = routineService;
        this.pair = pair;

        elementNameLabel = new Label(pair.element().getName());
        timeLabel = new Label(pair.timeSpec().start().toString() + " - " + pair.timeSpec().end().toString());
        timeLabel.getStyleClass().add("label-small");
        dayLabel = new Label(pair.timeSpec().weekday().toString() + " Week " + pair.timeSpec().weekNum());
        dayLabel.getStyleClass().add("label-small");

        ComboBox<RoutineElement> elementComboBox = new ComboBox<>();

        ComboBox<Integer> hourComboBox = new ComboBox<>();
        ComboBox<Integer> minuteComboBox = new ComboBox<>();
        HBox timeHbox = new HBox(hourComboBox, new Label(":"), minuteComboBox);
        timeHbox.setSpacing(5);

        ComboBox<Weekday> weekdayComboBox = new ComboBox<>();
        ComboBox<Integer> weekNumCombobox = new ComboBox<>();
        HBox dayBox = new HBox(weekdayComboBox, new Label("Week"), weekNumCombobox);
        dayBox.setSpacing(5);

        for (int h = 0; h < 24; h++) {
            hourComboBox.getItems().add(h);
        }

        for (int m = 0; m < 60; m += 5){
            minuteComboBox.getItems().add(m);
        }

        Region spacer = new Region();
        setVgrow(spacer, Priority.ALWAYS);

        editButton = new Button("Edit");
        deleteButton = new Button("Delete");
        saveButton = new Button("Save");
        cancelButton = new Button("Cancel");

        HBox buttonHbox = new HBox(editButton, deleteButton, saveButton, cancelButton);
        buttonHbox.setStyle("-fx-spacing: 10");

        editNodes = new ArrayList<>(List.of(elementComboBox, hourComboBox, minuteComboBox, timeHbox, weekdayComboBox, weekNumCombobox, dayBox, saveButton, cancelButton));
        infoNodes = new ArrayList<>(List.of(elementNameLabel, timeLabel, dayLabel, editButton, deleteButton));

        getChildren().addAll(elementNameLabel, timeLabel, dayLabel, elementComboBox, timeHbox, dayBox, spacer, buttonHbox);
        toggleInfoMode();

        editButton.setOnMouseClicked(e -> toggleEditMode());
        cancelButton.setOnMouseClicked(e -> toggleInfoMode());
    }

    private void toggleEditMode(){
        setSpacing(10);
        for (Node n : editNodes){
            n.setVisible(true);
            n.setManaged(true);
        }
        for (Node n : infoNodes){
            n.setVisible(false);
            n.setManaged(false);
        }
    }

    private void toggleInfoMode(){
        setSpacing(-10);
        for (Node n : editNodes){
            n.setVisible(false);
            n.setManaged(false);
        }
        for (Node n : infoNodes){
            n.setVisible(true);
            n.setManaged(true);
        }
    }
}
