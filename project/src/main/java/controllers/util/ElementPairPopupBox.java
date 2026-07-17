package controllers.util;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import model.RoutineElement;
import model.RoutineService;
import util.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ElementPairPopupBox extends PopupBox {
    private final ComboBox<Integer> startHourComboBox;
    private final ComboBox<Integer> startMinuteComboBox;
    private final ComboBox<Integer> endHourComboBox;
    private final ComboBox<Integer> endMinuteComboBox;
    private final ComboBox<Weekday> weekdayComboBox;
    private final ComboBox<Integer> weekNumCombobox;
    private final ComboBox<RoutineElement> elementComboBox;
    private ElementTimePair pair;
    private final Label elementNameLabel, timeLabel, dayLabel;
    private final Button editButton, deleteButton, saveButton, cancelButton;
    private final Label errorLabel;
    private final Runnable onSave;
    private final List<Node> infoNodes;
    private final List<Node> editNodes;
    private Runnable onDelete;

    public ElementPairPopupBox(ElementTimePair pair, RoutineService routineService, Runnable onSave){
        super(routineService);
        this.pair = pair;
        this.onSave = onSave;
        elementNameLabel = new Label();
        timeLabel = new Label();
        timeLabel.getStyleClass().add("label-small");
        dayLabel = new Label();
        dayLabel.getStyleClass().add("label-small");

        elementComboBox = new ComboBox<>();

        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-message-label");

        startHourComboBox = new ComboBox<>();
        startMinuteComboBox = new ComboBox<>();
        HBox startTimeHbox = new HBox(startHourComboBox, new Label(":"), startMinuteComboBox);
        startTimeHbox.setSpacing(5);

        endHourComboBox = new ComboBox<>();
        endMinuteComboBox = new ComboBox<>();
        HBox endTimeHbox = new HBox(endHourComboBox, new Label(":"), endMinuteComboBox);
        endTimeHbox.setSpacing(5);

        weekdayComboBox = new ComboBox<>();
        weekNumCombobox = new ComboBox<>();
        HBox dayBox = new HBox(weekdayComboBox, new Label("Week"), weekNumCombobox);
        dayBox.setSpacing(5);

        elementComboBox.setItems(FXCollections.observableArrayList(routineService.getRoutineElementList()));

        for (int h = 0; h < 24; h++) {
            startHourComboBox.getItems().add(h);
            endHourComboBox.getItems().add(h);
        }

        for (int m = 0; m < 60; m += 5){
            startMinuteComboBox.getItems().add(m);
            endMinuteComboBox.getItems().add(m);
        }

        weekdayComboBox.setItems(FXCollections.observableArrayList(Weekday.values()));

        for (int i = 1; i <= routineService.getCurrentViewedRoutine().getWeekCount() ; i++) {
            weekNumCombobox.getItems().add(i);
        }

        setValues();

        Region spacer = new Region();
        setVgrow(spacer, Priority.ALWAYS);

        editButton = new Button("Edit");
        deleteButton = new Button("Delete");
        saveButton = new Button("Save");
        cancelButton = new Button("Cancel");

        HBox buttonHbox = new HBox(editButton, deleteButton, saveButton, cancelButton);
        buttonHbox.setStyle("-fx-spacing: 10");

        editNodes = new ArrayList<>(List.of(elementComboBox, startTimeHbox, endTimeHbox, weekNumCombobox, dayBox, saveButton, cancelButton));
        infoNodes = new ArrayList<>(List.of(elementNameLabel, timeLabel, dayLabel, editButton, deleteButton));

        getChildren().addAll(elementNameLabel, timeLabel, dayLabel, elementComboBox, startTimeHbox, endTimeHbox, dayBox, spacer, errorLabel, buttonHbox);
        toggleInfoMode();

        editButton.setOnMouseClicked(e -> toggleEditMode());
        cancelButton.setOnMouseClicked(e -> toggleInfoMode());
        saveButton.setOnMouseClicked(e -> handleSaveButton());
        deleteButton.setOnMouseClicked(e -> handleDeleteButton());
    }

    private void setValues(){
        elementNameLabel.setText(pair.element().getName());
        timeLabel.setText(pair.timeSpec().start().toString() + " - " + pair.timeSpec().end().toString());
        dayLabel.setText(pair.timeSpec().weekday().toString() + " Week " + pair.timeSpec().weekNum());
        elementComboBox.setValue(pair.element());
        startHourComboBox.setValue(pair.timeSpec().start().getHour());
        endHourComboBox.setValue(pair.timeSpec().end().getHour());
        startMinuteComboBox.setValue((pair.timeSpec().start().getMinute()));
        endMinuteComboBox.setValue((pair.timeSpec().end().getMinute()));
        weekdayComboBox.setValue(pair.timeSpec().weekday());
        weekNumCombobox.setValue(pair.timeSpec().weekNum());
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
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    public void handleSaveButton(){
        RoutineElement newElement = elementComboBox.getValue();
        LocalTime newStart = LocalTime.of(startHourComboBox.getValue(), startMinuteComboBox.getValue());
        LocalTime newEnd = LocalTime.of(endHourComboBox.getValue(), endMinuteComboBox.getValue());
        RoutineTimeSpec newSpec = new RoutineTimeSpec(newStart, newEnd, weekdayComboBox.getValue(), weekNumCombobox.getValue());

        ElementTimePair newPair = new ElementTimePair(newElement, newSpec);

        if (!pair.timeSpec().equals(newSpec)) {
            try {
                routineService.changeTimeSpec(routineService.getCurrentViewedRoutine(), pair.timeSpec(), newSpec);
                System.out.println("save ok");
            } catch (ConflictingTimeSpecsException e) {
                errorLabel.setText(e.getMessage());
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                System.out.println("save not ok");
                System.out.println("message: " + e.getMessage());
                return;
            }
        }
        if (!pair.element().equals(newElement)){
            routineService.swapElements(routineService.getCurrentViewedRoutine(), newSpec, newElement);
        }

        this.pair = newPair;
        setValues();
        toggleInfoMode();
        if (onSave != null) onSave.run();
    }

    public void setOnDelete(Runnable onDelete){
        this.onDelete = onDelete;
    }

    public void handleDeleteButton(){
        routineService.unassignRoutineElement(routineService.getCurrentViewedRoutine(), pair.timeSpec());
        if (onSave != null) onSave.run();
        if (onDelete != null) onDelete.run();
    }

}
