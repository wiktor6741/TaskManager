package controllers;

import controllers.util.TaskBox;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import model.Task;
import model.TaskManager;

import java.time.LocalDateTime;

public class TaskViewController {
    @FXML
    private VBox taskVBox;

    private TaskManager taskManager;

    @FXML
    public void initialize(){
    }

    public void init(TaskManager taskManager){
        this.taskManager = taskManager;
        reloadTasks();
    }

    private void addTaskBox(Task task){
        taskVBox.getChildren().add(new TaskBox(task));
    }

    private void reloadTasks(){
        taskVBox.getChildren().clear();
        for (Task task : taskManager.getTasks()){
            addTaskBox(task);
        }
    }
}
