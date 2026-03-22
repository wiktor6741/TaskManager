package controllers;

import controllers.util.ImportanceIndicator;
import controllers.util.ImportanceSelector;
import controllers.util.TaskBox;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Category;
import model.Task;
import model.TaskManager;

import java.time.LocalDateTime;

public class TaskViewController {
    @FXML
    private VBox taskVBox;

    @FXML
    private HBox nameBox, categoryBox, deadlineBox, priorityBox, expectedDurationBox;

    @FXML
    private Label nameLabel, categoryLabel, deadlineLabel, durationLabel;

    private TaskManager taskManager;

    private ImportanceIndicator taskInfoImportanceIndicator;

    private TaskBox selectedTaskBox;

    @FXML
    public void initialize(){
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

    }

    private void addTaskBox(Task task){
        TaskBox taskBox = new TaskBox(task);

        taskBox.setOnClick(t -> {
            displayTaskInfo(t);
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

        if (task.getDeadline() != null){
            String s = task.getDeadline().toString();
            deadlineLabel.setText(s.replace("T", " "));
        }

        if (priorityBox.getChildren().contains(taskInfoImportanceIndicator)){
            priorityBox.getChildren().remove(taskInfoImportanceIndicator);
        }

        if (task.getPriority() != null){
            taskInfoImportanceIndicator = new ImportanceIndicator(task.getPriority(), 5);
            priorityBox.getChildren().add(taskInfoImportanceIndicator);
        }
    }
}
