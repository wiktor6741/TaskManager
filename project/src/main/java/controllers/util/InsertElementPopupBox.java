package controllers.util;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import model.RoutineElement;
import model.RoutineService;
import util.ConflictingTimeSpecsException;
import util.ElementTimePair;
import util.RoutineTimeSpec;
import util.Weekday;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class InsertElementPopupBox extends PopupBox{
    private final ComboBox<Integer> startHourComboBox;
    private final ComboBox<Integer> startMinuteComboBox;
    private final ComboBox<Integer> endHourComboBox;
    private final ComboBox<Integer> endMinuteComboBox;
    private final ComboBox<Weekday> weekdayComboBox;
    private final ComboBox<Integer> weekNumCombobox;
    private final ComboBox<RoutineElement> elementComboBox;
    private final List<ComboBox> comboBoxes;
    private final Button saveButton;
    private final Button cancelButton;
    private final Label errorLabel;
    private Runnable onSave;
    private Runnable onCancel;
    private CheckBox insertDailyCheckBox;


    public InsertElementPopupBox(RoutineService routineService){
        elementComboBox = new ComboBox<>();
        startHourComboBox = new ComboBox<>();
        startMinuteComboBox = new ComboBox<>();
        endHourComboBox = new ComboBox<>();
        endMinuteComboBox = new ComboBox<>();
        weekdayComboBox = new ComboBox<>();
        weekNumCombobox = new ComboBox<>();
        saveButton = new Button("Save");
        cancelButton = new Button("Cancel");
        insertDailyCheckBox = new CheckBox();
        super(routineService);

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
        Label elementLabel = new Label("Element:");

        HBox elementHbox = new HBox(elementLabel, elementComboBox);
        elementHbox.setAlignment(Pos.CENTER_LEFT);
        elementHbox.setSpacing(5);

        Label startLabel = new Label("Start:");
        startLabel.setStyle("-fx-font-size: 20");
        HBox startHbox = new HBox(startLabel, startHourComboBox, new Label(":"), startMinuteComboBox);
        startHbox.setAlignment(Pos.CENTER_LEFT);
        startHbox.setSpacing(5);

        Label endLabel = new Label("End:");
        HBox endHbox = new HBox(endLabel, endHourComboBox,  new Label(":"),  endMinuteComboBox);
        endHbox.setAlignment(Pos.CENTER_LEFT);
        endHbox.setSpacing(5);
        Label dayLabel = new Label("Day:");
        HBox dayHbox = new HBox(dayLabel, weekdayComboBox, weekNumCombobox);
        dayHbox.setSpacing(5);
        dayHbox.setAlignment(Pos.CENTER_LEFT);
        HBox buttonHbox = new HBox(saveButton, cancelButton);
        buttonHbox.setSpacing(10);

        elementComboBox.setPromptText("ELEMENT");
        startHourComboBox.setPromptText("HH");
        startMinuteComboBox.setPromptText("mm");
        endHourComboBox.setPromptText("HH");
        endMinuteComboBox.setPromptText("mm");
        weekdayComboBox.setPromptText("WEEKDAY");
        weekNumCombobox.setPromptText("WEEKNUM");

        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-message-label");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        saveButton.setOnMouseClicked(e -> handleSaveButton());
        cancelButton.setOnMouseClicked(e -> handleCancelButton());

        Label insertDailyLabel = new Label("Insert daily: ");
        //insertDailyLabel.setStyle("-fx-font-size: 16px");

        HBox insertDailyHbox = new HBox(insertDailyLabel, insertDailyCheckBox);
        insertDailyHbox.setAlignment(Pos.CENTER_LEFT);

        insertDailyCheckBox.setOnAction(e -> {
            boolean checked = insertDailyCheckBox.isSelected();
            if (checked) {
                dayHbox.setManaged(false);
                dayHbox.setVisible(false);
            } else {
                dayHbox.setManaged(true);
                dayHbox.setVisible(true);
            }
        });

        Region spacer = new Region();
        setVgrow(spacer, Priority.ALWAYS);

        comboBoxes = new ArrayList<>(List.of(elementComboBox, startHourComboBox, endHourComboBox, startMinuteComboBox, endMinuteComboBox,
                weekdayComboBox, weekNumCombobox));
        getChildren().addAll(elementHbox, startHbox, endHbox, dayHbox, errorLabel, insertDailyHbox, spacer, buttonHbox);
        setSpacing(10);
    }

    public void handleCancelButton(){
        if (onCancel != null) onCancel.run();
    }

    public void handleSaveButton(){
        for (ComboBox comboBox : comboBoxes){
            if (comboBox.getValue() == null && !(insertDailyCheckBox.isSelected() && (comboBox == weekdayComboBox || comboBox == weekNumCombobox))) {
                errorLabel.setManaged(true);
                errorLabel.setVisible(true);
                errorLabel.setText("Please select a value for all fields");
                return;
            }
        }

        RoutineElement element = elementComboBox.getValue();
        LocalTime start = LocalTime.of(startHourComboBox.getValue(), startMinuteComboBox.getValue());
        LocalTime end = LocalTime.of(endHourComboBox.getValue(), endMinuteComboBox.getValue());

        try {
            if (insertDailyCheckBox.isSelected()){
                routineService.assignRoutineElementDaily(routineService.getCurrentViewedRoutine() , element, start, end);
            }else {
                RoutineTimeSpec spec = new RoutineTimeSpec(start, end, weekdayComboBox.getValue(), weekNumCombobox.getValue());
                routineService.assignRoutineElement(routineService.getCurrentViewedRoutine(), element, spec);
            }
            if (onSave != null) onSave.run();
        } catch (ConflictingTimeSpecsException e) {
            errorLabel.setManaged(true);
            errorLabel.setVisible(true);
            errorLabel.setText(e.getMessage());
        }
    }

    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    public void setOnSave(Runnable onSave){
        this.onSave = onSave;
    }
}
