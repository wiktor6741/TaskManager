import controllers.TaskViewController;
import dao.DatabaseManager;
import dao.TaskDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Task;
import model.TaskManager;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        DatabaseManager dbManager = new DatabaseManager();
        TaskManager taskManager = new TaskManager(dbManager.getConnection());

        StackPane root = new StackPane();
        Scene scene = new Scene(root, 1600, 1000);
        scene.getStylesheets().add(getClass().getResource("style2.css").toExternalForm());

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("taskview.fxml"));
        BorderPane taskView = loader.load();

        TaskViewController taskController = loader.getController();
        taskController.init(taskManager);

        root.getChildren().addAll(taskView);
        taskView.setVisible(true);
        primaryStage.setTitle("Task Manager");
        primaryStage.setScene(scene);
        primaryStage.show();

        // root.getChildren().add(tasksView); <- później po wczytaniu FXML
    }
}
