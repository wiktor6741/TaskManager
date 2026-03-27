package controllers;

import controllers.util.ImportanceIndicator;
import controllers.util.ImportanceSelector;
import util.OperationMode;
import controllers.util.TaskBox;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import model.Category;
import model.Task;
import model.TaskManager;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static util.OperationMode.*;

public class TaskViewController {
    @FXML
    private VBox taskVBox;
    @FXML
    private HBox nameBox, categoryBox, goalTimeBox, deadlineBox, priorityBox, expectedDurationBox;
    @FXML
    private Label nameLabel, categoryLabel, goalTimeLabel, deadlineLabel, durationLabel, hourSignLabel, minuteSignLabel;
    @FXML
    private TextField nameTextField, durationHourField, durationMinuteField;
    @FXML
    private DatePicker goalTimeDatePicker, deadlineDatePicker;
    @FXML
    private ComboBox<Integer> goalHourBox, goalMinuteBox, deadlineMinuteBox, deadlineHourBox;

    @FXML
    private ComboBox<Category> categoryComboBox;

    @FXML
    private Button newTaskButton, editTaskButton, saveButton, cancelButton;

    private ImportanceSelector importanceSelector;

    private ImportanceIndicator importanceIndicator;

    private List<Node> taskInfoNodes;
    private List<Node> taskCreateNodes;

    private TaskManager taskManager;

    private ImportanceIndicator taskInfoImportanceIndicator;

    private TaskBox selectedTaskBox;

    OperationMode mode = INFO;

    @FXML
    public void initialize(){

        goalTimeDatePicker.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return string != null && !string.isEmpty()
                        ? LocalDate.parse(string, formatter)
                        : null;
            }
        });

        ComboBox[] comboBoxes = {categoryComboBox, goalMinuteBox, goalHourBox,
                deadlineMinuteBox, deadlineHourBox};

        for (ComboBox cb : comboBoxes) {
            cb.setButtonCell(new ListCell<Object>() { // Używamy Object, żeby przyjęło wszystko
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(cb.getPromptText());
                    } else {
                        // Jeśli to kategoria, użyj getName(), jeśli nie - toString()
                        if (item instanceof Category) {
                            setText(((Category) item).getName());
                        } else {
                            setText(item.toString());
                        }
                    }
                }
            });
        }
        for (int h = 0; h < 24; h++) {
            goalHourBox.getItems().add(h);
            deadlineHourBox.getItems().add(h);
        }

        for (int m = 0; m < 60; m += 5){
            goalMinuteBox.getItems().add(m);
            deadlineMinuteBox.getItems().add(m);
        }

        importanceSelector = new ImportanceSelector(5);
        importanceIndicator = new ImportanceIndicator(5, 5);

        priorityBox.getChildren().add(importanceSelector);
        priorityBox.getChildren().add(importanceIndicator);

        taskInfoNodes = new ArrayList<>(List.of(importanceIndicator, newTaskButton, editTaskButton, nameLabel, categoryLabel, goalTimeLabel, deadlineLabel,
               durationLabel));

        taskCreateNodes = new ArrayList<>(List.of(importanceSelector, hourSignLabel, minuteSignLabel, saveButton, cancelButton, durationHourField, durationMinuteField, nameTextField, categoryComboBox, goalMinuteBox, goalHourBox, deadlineMinuteBox, deadlineHourBox,
                goalTimeDatePicker, deadlineDatePicker));

        for (Node n : taskCreateNodes){
            n.setVisible(false);
            n.setManaged(false);
        }


        newTaskButton.setOnMouseClicked(_ -> toggleCreateMode());
        editTaskButton.setOnMouseClicked(_ -> toggleEditMode());
        cancelButton.setOnMouseClicked(_ -> toggleInfoMode());
        saveButton.setOnMouseClicked(_ -> handleSaveButton());
    }


    public void init(TaskManager taskManager){
        this.taskManager = taskManager;
        reloadTasks();
        //priorityBox.getChildren().add(new ImportanceIndicator(3, 5));
        Task task = new Task("gowno chuj");
        //taskManager.addCategory(new Category("kategoria sraka"));
        task.setDeadline(LocalDateTime.parse("2026-03-23T18:30"));
        task.setCategoryID(2);
        task.setPriority(1);

        displayTaskInfo(task);

        for (Category c : taskManager.getCategoryList()){
            System.out.println(c.toString());
            categoryComboBox.getItems().add(c);
        }

    }

    private void addTaskBox(Task task){
        TaskBox taskBox = new TaskBox(task);

        taskBox.setOnClick(t -> {
            selectTaskBox(taskBox);
        });

        taskVBox.getChildren().add(taskBox);
    }

    private void selectTaskBox(TaskBox taskBox) {
        if (selectedTaskBox != null){
            selectedTaskBox.setSelected(false);
        }
        taskBox.setSelected(true);
        selectedTaskBox = taskBox;
        displayTaskInfo(taskBox.getTask());
    }

    private void reloadTasks(){
        taskVBox.getChildren().clear();
        for (Task task : taskManager.getTasks()){
            addTaskBox(task);
        }
    }

    private void displayTaskInfo(Task task){
        nameLabel.setText(task.getName());

        categoryLabel.setText("");
        deadlineLabel.setText("");


        if (task.getCategoryID() != null) {
            categoryLabel.setText(taskManager.getTaskCategory(task).getName());
        }

        if (task.getGoalEndTime() != null){
            String s = task.getGoalEndTime().toString();
            goalTimeLabel.setText(s.replace("T", " "));
        }

        if (task.getDeadline() != null){
            String s = task.getDeadline().toString();
            deadlineLabel.setText(s.replace("T", " "));
        }

        if (task.getPriority() == null){
            importanceIndicator.setVisible(false);
            importanceIndicator.setManaged(false);
        } else {
            importanceIndicator.setValue(task.getPriority());
            if (mode == INFO) {
                importanceIndicator.setVisible(true);
                importanceIndicator.setManaged(true);
            }
        }
    }

    private void toggleCreateMode(){
        mode = CREATE;
        for (Node n : taskInfoNodes){
            n.setVisible(false);
            n.setManaged(false);
        }

        for (Node n : taskCreateNodes){
            n.setVisible(true);
            n.setManaged(true);
        }
        flushInput();
    }

    private void toggleEditMode() {
        mode = EDIT;
        for (Node n : taskInfoNodes) {
            n.setVisible(false);
            n.setManaged(false);
        }

        for (Node n : taskCreateNodes) {
            n.setVisible(true);
            n.setManaged(true);
        }
        flushInput();
        Task task = selectedTaskBox.getTask();
        nameTextField.setText(task.getName());
        categoryComboBox.setValue(taskManager.getTaskCategory(task));
        importanceSelector.setValue((task.getPriority() == null) ? 0 : task.getPriority());

        LocalDateTime goalEndTime = task.getGoalEndTime();
        if (goalEndTime != null) {
            goalTimeDatePicker.setValue(goalEndTime.toLocalDate());
            goalHourBox.setValue(goalEndTime.getHour());
            goalMinuteBox.setValue(goalEndTime.getMinute());
        }

        LocalDateTime deadline = task.getDeadline();
        if (deadline != null) {
            deadlineDatePicker.setValue(deadline.toLocalDate());
            deadlineHourBox.setValue(deadline.getHour());
            deadlineMinuteBox.setValue(deadline.getMinute());
        }

    }
    private void toggleInfoMode(){
        mode = INFO;
        for (Node n : taskInfoNodes){
            n.setVisible(true);
            n.setManaged(true);
        }

        for (Node n : taskCreateNodes){
            n.setVisible(false);
            n.setManaged(false);
        }
    }

    private Task createTask(){
        String name = nameTextField.getText().trim().replaceAll("\\s+", " ");
        if (name.isEmpty()) return null;
        return new Task(name);
    }

    private void feedInputDataToTask(Task task){
        Category selected = categoryComboBox.getValue();
        if (selected != null){
            task.setCategoryID(selected.getId());
        }

        LocalDate deadline = deadlineDatePicker.getValue();
        if (deadline != null){
            Integer hour = (deadlineHourBox.getValue() == null) ? 0 : deadlineHourBox.getValue();
            Integer minute = (deadlineMinuteBox.getValue() == null) ? 0 : deadlineMinuteBox.getValue();
            task.setDeadline(deadline.atTime(hour, minute));
        }

        LocalDate goalFinishTime = goalTimeDatePicker.getValue();
        if (deadline != null){
            Integer hour = (goalHourBox.getValue() == null) ? 0 : goalHourBox.getValue();
            Integer minute = (goalMinuteBox.getValue() == null) ? 0 : goalMinuteBox.getValue();
            task.setGoalEndTime(goalFinishTime.atTime(hour, minute));
        }

        int hours;
        int minutes;

        String hoursString = durationHourField.getText().trim().replaceAll("\\s+", " ");
        String minutesString = durationMinuteField.getText().trim().replaceAll("\\s+", " ");

        try {
            hours = Integer.parseInt(hoursString);
        } catch (NumberFormatException e) {
            hours = 0;
        }
        try {
            minutes = Integer.parseInt(minutesString);
        } catch (NumberFormatException e) {
            minutes = 0;
        }


        Duration expectedDuration = Duration.ofMinutes(hours * 60 + minutes);

        if (!expectedDuration.isZero()){
            task.setExpectedDuration(expectedDuration);
        }

        int priority = importanceSelector.getValue();

        if (priority > 0) task.setPriority(priority);
    }

    private void handleSaveButton() {
        Task task = null;
        if (mode == CREATE) task = createTask();
        if (mode == EDIT) task = selectedTaskBox.getTask();

        if (task != null) {
            feedInputDataToTask(task);
            if (taskManager.validateTask(task, mode).isValid()){
                if (mode == CREATE) taskManager.addTask(task);
                if (mode == EDIT) taskManager.editTask(task);

                displayTaskInfo(task);
                toggleInfoMode();
                reloadTasks();
            }
            else{
                System.out.println(taskManager.validateTask(task, mode).message());
            }
        }
    }

    private void flushInput(){
        importanceSelector.setValue(0);
        durationMinuteField.setText("");
        nameTextField.setText("");
        durationHourField.setText("");
        goalMinuteBox.setValue(null);
        goalHourBox.setValue(null);
        deadlineMinuteBox.setValue(null);
        deadlineHourBox.setValue(null);
        goalTimeDatePicker.setValue(null);
        deadlineDatePicker.setValue(null);
        categoryComboBox.setValue(null);
    }
}
