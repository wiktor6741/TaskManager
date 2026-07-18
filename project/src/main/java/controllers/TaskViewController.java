package controllers;

import controllers.util.ImportanceIndicator;
import controllers.util.ImportanceSelector;
import javafx.animation.PauseTransition;
import mainapp.MainApp;
import util.DurationStringFormatter;
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
import model.TaskService;
import util.SortingMode;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static util.OperationMode.*;
import static util.SortingMode.*;

public class TaskViewController {
    @FXML
    private VBox taskVBox, taskDetailVBox, categoryMenuVBox;
    @FXML
    private HBox nameBox, categoryBox, goalTimeBox, deadlineBox, priorityBox, expectedDurationBox, taskSaveErrorHBox, taskDeleteConfirmationHBox, categorySaveErrorHBox;
    @FXML
    private Label nameLabel, categoryLabel, goalTimeLabel, deadlineLabel, durationLabel, hourSignLabel, minuteSignLabel, categoryNameLabel, categoryPickLabel, taskSaveErrorMessageLabel,
            categorySaveErrorMessageLabel;
    @FXML
    private TextField nameTextField, durationHourField, durationMinuteField, categoryNameTextField;
    @FXML
    private DatePicker goalTimeDatePicker, deadlineDatePicker;
    @FXML
    private ComboBox<Integer> goalHourBox, goalMinuteBox, deadlineMinuteBox, deadlineHourBox;

    @FXML
    private ComboBox<Category> categoryComboBox, categoryViewComboBox, categoryMenuComboBox;

    @FXML
    private Button newTaskButton, editTaskButton, saveButton, cancelButton, categorySaveButton, categoryCancelButton,
            addCategoryButton, editCategoryButton, categoryMenuButton, taskMenuButton, deleteTaskButton, taskDeleteYesButton,
            taskDeleteNoButton, deleteCategoryButton, routineViewButton;

    private ImportanceSelector importanceSelector;

    private ImportanceIndicator importanceIndicator;

    private List<Node> taskInfoNodes, taskCreateNodes, categoryInfoNodes, categoryCreateNodes;

    private TaskService taskManager;

    private MainApp mainApp;

    private ImportanceIndicator taskInfoImportanceIndicator;

    private TaskBox selectedTaskBox;

    private OperationMode mode = TASK_INFO;

    private final Category ALL_CATEGORY = new Category("All");

    private Category currentViewedCategory = ALL_CATEGORY;

    private List<ComboBox<Category>> categoryComboBoxes;

    private SortingMode sortingMode = PRIORITY;

    @FXML
    private ComboBox<SortingMode> sortingModeComboBox;

    @FXML
    public void initialize(){


        List<DatePicker> datePickers = List.of(deadlineDatePicker, goalTimeDatePicker);


        for (DatePicker dp : datePickers) {
            dp.setEditable(false);
            dp.setConverter(new StringConverter<LocalDate>() {
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
        }

        ComboBox[] comboBoxes = {categoryComboBox, goalMinuteBox, goalHourBox,
                deadlineMinuteBox, deadlineHourBox, categoryMenuComboBox};

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
        categoryViewComboBox.getItems().add(ALL_CATEGORY);
        categoryViewComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleCategoryChange();
            }
        });

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

        taskInfoNodes = new ArrayList<>(List.of(importanceIndicator, newTaskButton, editTaskButton, deleteTaskButton, nameLabel, categoryLabel, goalTimeLabel, deadlineLabel,
               durationLabel));

        taskCreateNodes = new ArrayList<>(List.of(importanceSelector, hourSignLabel, minuteSignLabel, saveButton, cancelButton, durationHourField, durationMinuteField, nameTextField, categoryComboBox, goalMinuteBox, goalHourBox, deadlineMinuteBox, deadlineHourBox,
                goalTimeDatePicker, deadlineDatePicker));

        categoryInfoNodes = new ArrayList<>(List.of(categoryPickLabel, categoryMenuComboBox, addCategoryButton, editCategoryButton, taskMenuButton, deleteCategoryButton));
        categoryCreateNodes = new ArrayList<>(List.of(categoryNameLabel, categoryNameTextField, categorySaveButton, categoryCancelButton, categorySaveErrorHBox));

        categoryComboBoxes = new ArrayList<>(List.of(categoryComboBox, categoryViewComboBox, categoryMenuComboBox));

        for (Node n : taskCreateNodes){
            n.setVisible(false);
            n.setManaged(false);
        }

        for (SortingMode sm : SortingMode.values()){
            sortingModeComboBox.getItems().add(sm);
        }

        sortingModeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleSortingModeChange();
            }
        });


        categoryMenuVBox.setVisible(false);
        categoryMenuVBox.setManaged(false);

        taskSaveErrorHBox.setVisible(false);
        taskSaveErrorHBox.setManaged(false);

        taskDeleteConfirmationHBox.setVisible(false);
        taskDeleteConfirmationHBox.setManaged(false);

        categorySaveErrorHBox.setVisible(false);
        categorySaveErrorHBox.setManaged(false);


        newTaskButton.setOnMouseClicked(_ -> toggleCreateMode());
        editTaskButton.setOnMouseClicked(_ -> toggleEditMode());
        cancelButton.setOnMouseClicked(_ -> toggleInfoMode());
        saveButton.setOnMouseClicked(_ -> handleSaveButton());
        categoryMenuButton.setOnMouseClicked(_ -> toggleCategoryMenu());
        addCategoryButton.setOnMouseClicked(_ -> toggleCategoryCreateMode());
        editCategoryButton.setOnMouseClicked(_ -> toggleCategoryEditMode());
        categoryCancelButton.setOnMouseClicked(_ -> toggleCategoryInfoMode());
        taskMenuButton.setOnMouseClicked(_ -> toggleTaskMenu());
        categorySaveButton.setOnMouseClicked(_ -> handleCategorySaveButton());
        deleteTaskButton.setOnMouseClicked(_ -> handleDeleteTaskButton());
        taskDeleteNoButton.setOnMouseClicked(_ -> {taskDeleteConfirmationHBox.setVisible(false); taskDeleteConfirmationHBox.setManaged(false);});
        taskDeleteYesButton.setOnMouseClicked(_ -> {deleteTask(); taskDeleteConfirmationHBox.setVisible(false); taskDeleteConfirmationHBox.setManaged(false);});
        deleteCategoryButton.setOnMouseClicked(_ -> handleDeleteCategoryButton());
        routineViewButton.setOnMouseClicked(_ -> {
            try {
                mainApp.loadRoutineView();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void init(TaskService taskManager, MainApp mainApp){
        this.taskManager = taskManager;
        reloadTasks();

        for (Category c : taskManager.getCategoryList()){
            System.out.println(c.toString());
            categoryComboBox.getItems().add(c);
            categoryViewComboBox.getItems().add(c);
            categoryMenuComboBox.getItems().add(c);
        }

        sortingModeComboBox.setValue(PRIORITY);

        this.mainApp = mainApp;
    }

    private TaskBox addTaskBox(Task task){
        TaskBox taskBox = new TaskBox(task);

        taskBox.setOnClick(t -> {
            selectTaskBox(taskBox);
        });

        taskBox.setOnComplete(t -> {
            PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(0.5));
            pause.setOnFinished(event -> {
                taskManager.deleteTask(t);
                reloadTasks();
            });
            pause.play();

        });
        taskVBox.getChildren().add(taskBox);

        return taskBox;
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
        reloadTasks(null);
    }

    private void reloadTasks(Task taskToSelect){
        taskVBox.getChildren().clear();
        switch (sortingMode){
            case PRIORITY -> taskManager.sortByPriority();
            case DEADLINE -> taskManager.sortByDeadline();
            case PRIORITY_THEN_DEADLINE -> taskManager.sortByPriorityAndDeadline();
            case DEADLINE_THEN_PRIORITY -> taskManager.sortByDeadlineAndPriority();
        }
        List<Task> tasks = taskManager.getTasks();
        if (tasks.isEmpty()){
            selectedTaskBox = null;
            Task exanpleTask = new Task("Add your first task!");
            exanpleTask.setPriority(5);
            exanpleTask.setDeadline(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));;
            displayTaskInfo(exanpleTask);
        }else {
            for (Task task : tasks) {
                TaskBox taskBox = addTaskBox(task);
                if (taskToSelect == null && tasks.indexOf(task) == 0) {
                    selectTaskBox(taskBox);
                }
                if (taskToSelect != null && task == taskToSelect){
                    selectTaskBox(taskBox);
                }
            }
        }
    }


    private void displayTaskInfo(Task task){
        taskDeleteConfirmationHBox.setVisible(false);
        taskDeleteConfirmationHBox.setManaged(false);
        nameLabel.setText(task.getName());

        categoryLabel.setText("");
        deadlineLabel.setText("");
        goalTimeLabel.setText("");


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
            if (mode == TASK_INFO) {
                importanceIndicator.setVisible(true);
                importanceIndicator.setManaged(true);
            }
        }
        Duration expectedDuration = task.getExpectedDuration();
        if (expectedDuration != null) durationLabel.setText(DurationStringFormatter.formatDuration(expectedDuration));
        else {
            durationLabel.setText("");
        }
    }

    private void toggleCreateMode(){
        mode = TASK_CREATE;
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
        if (selectedTaskBox == null){
            return;
        }
        mode = TASK_EDIT;
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

        Duration expectedDuration = task.getExpectedDuration();
        if (expectedDuration != null){
            durationHourField.setText(String.valueOf((int) expectedDuration.toHours()));
            durationMinuteField.setText(String.valueOf((int) expectedDuration.toMinutes() % 60));
        }

    }
    private void toggleInfoMode(){
        taskSaveErrorHBox.setVisible(false);
        taskSaveErrorHBox.setManaged(false);
        mode = TASK_INFO;
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
        String name = nameTextField.getText().trim().replaceAll("\\s+", " ");
        if (!name.isEmpty()) task.setName(name);

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
        if (goalFinishTime != null){
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
        if (mode == TASK_CREATE) task = createTask();
        if (mode == TASK_EDIT) task = selectedTaskBox.getTask();

        if (task != null) {
            feedInputDataToTask(task);
            if (taskManager.validateTask(task, mode).isValid()){
                if (mode == TASK_CREATE) taskManager.addTask(task);
                if (mode == TASK_EDIT) taskManager.editTask(task);

                displayTaskInfo(task);
                toggleInfoMode();
                reloadTasks(task);
                taskSaveErrorHBox.setVisible(false);
                taskSaveErrorHBox.setManaged(false);
            } else{
                taskSaveErrorHBox.setVisible(true);
                taskSaveErrorHBox.setManaged(true);
                taskSaveErrorMessageLabel.setText(taskManager.validateTask(task, mode).message());
            }
        }
    }

    private void handleCategoryChange(){
        if (categoryViewComboBox.getValue() == ALL_CATEGORY){
            taskManager.getAllTasks();
            reloadTasks();
        } else {
            Category category = categoryViewComboBox.getValue();
            taskManager.toggleCategory(category.getId());
            reloadTasks();
        }
    }

    private void toggleCategoryMenu(){
        taskDetailVBox.setVisible(false);
        taskDetailVBox.setManaged(false);
        categoryMenuVBox.setVisible(true);
        categoryMenuVBox.setManaged(true);
        toggleCategoryInfoMode();
    }

    private void toggleTaskMenu(){
        taskDetailVBox.setVisible(true);
        taskDetailVBox.setManaged(true);
        categoryMenuVBox.setVisible(false);
        categoryMenuVBox.setManaged(false);
        toggleInfoMode();
    }

    private void toggleCategoryInfoMode() {
        mode = CATEGORY_INFO;
        for (Node n : categoryCreateNodes){
            n.setVisible(false);
            n.setManaged(false);
        }
        for (Node n : categoryInfoNodes){
            n.setVisible(true);
            n.setManaged(true);
        }
    }

    private void toggleCategoryCreateMode(){
        mode = CATEGORY_CREATE;
        for (Node n : categoryInfoNodes){
            n.setVisible(false);
            n.setManaged(false);
        }

        for (Node n : categoryCreateNodes){
            n.setVisible(true);
            n.setManaged(true);
        }
        flushInput();
    }

    private void toggleCategoryEditMode(){
        Category c = categoryMenuComboBox.getValue();
        if (c == null || c == ALL_CATEGORY){
            return;
        }
        mode = CATEGORY_EDIT;
        for (Node n : categoryInfoNodes){
            n.setVisible(false);
            n.setManaged(false);
        }

        for (Node n : categoryCreateNodes){
            n.setVisible(true);
            n.setManaged(true);
        }
        categoryNameTextField.setText(c.getName());
        flushInput();
    }


    private Category createCategory(){
        String name = categoryNameTextField.getText().trim().replaceAll("\\s+", " ");
        if (name.isEmpty()) return null;
        return new Category(name);
    }

    private void feedInputIntoCategory(Category category){
        String name = categoryNameTextField.getText().trim().replaceAll("\\s+", " ");
        if (!name.isEmpty()) category.setName(name);
    }

    private void handleCategorySaveButton(){
        Category category = null;
        if (mode == CATEGORY_CREATE) {
            category = createCategory();
        }
        if (mode == CATEGORY_EDIT){
            category = categoryMenuComboBox.getValue();
        }

        feedInputIntoCategory(category);
        System.out.println(category.getName());

        if (category != null && taskManager.validateCategory(category, mode).isValid()){
            if (mode == CATEGORY_CREATE) taskManager.addCategory(category);
            if (mode == CATEGORY_EDIT) taskManager.editCategory(category);
            toggleCategoryInfoMode();
            reloadCategories();
            reloadTasks();
         }else{
            categorySaveErrorHBox.setVisible(true);
            categorySaveErrorHBox.setManaged(true);
            categorySaveErrorMessageLabel.setText(taskManager.validateCategory(category, mode).message());
        }
    }

    private void reloadCategories(){
        Category taskViewChosenCategory = categoryViewComboBox.getValue();
        for (ComboBox<Category> cb : categoryComboBoxes){
            cb.getItems().clear();
            cb.getItems().add(ALL_CATEGORY);
            for (Category c : taskManager.getCategoryList()){
                cb.getItems().add(c);
            }
        }
        if (categoryViewComboBox.getItems().contains(taskViewChosenCategory)) {
            categoryViewComboBox.setValue(taskViewChosenCategory);
        } else {
            categoryViewComboBox.setValue(ALL_CATEGORY);
        }
    }

    private void handleSortingModeChange(){
        sortingMode = sortingModeComboBox.getValue();
        reloadTasks();
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
        categoryNameTextField.setText("");
        categorySaveErrorHBox.setVisible(false);
        categorySaveErrorHBox.setManaged(false);
    }


    private void deleteTask(){
        if (selectedTaskBox != null) {
            taskManager.deleteTask(selectedTaskBox.getTask());
            reloadTasks();
        }
    }

    private void handleDeleteTaskButton(){
        if (selectedTaskBox != null){
            taskDeleteConfirmationHBox.setVisible(true);
            taskDeleteConfirmationHBox.setManaged(true);
        }
    }

    private void handleDeleteCategoryButton(){
        Category category = categoryMenuComboBox.getValue();
        if (category != null){
            taskManager.deleteCategory(category);
            reloadCategories();
            reloadTasks();
        }
    }
}
