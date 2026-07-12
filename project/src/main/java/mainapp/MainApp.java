import controllers.RoutineViewController;
import controllers.TaskViewController;
import dao.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.RoutineService;
import model.TaskService;

import java.io.IOException;

public class MainApp extends Application {
    private DatabaseManager dbManager = new DatabaseManager();
    private TaskService taskManager = new TaskService(dbManager.getConnection());
    private RoutineService routineService = new RoutineService(dbManager.getConnection());
    private StackPane root = new StackPane();
    private Scene scene = new Scene(root, 1600, 1000);
    private Stage primaryStage;
    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        scene.getStylesheets().add(getClass().getResource("style2.css").toExternalForm());
        loadTaskView();
    }

    public void loadTaskView() throws IOException{
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
    }

    public void loadRoutineView() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("routineview.fxml"));
        StackPane routineView = loader.load();

        RoutineViewController routineController = loader.getController();
        routineController.init(routineService);
        root.getChildren().addAll(routineView);
        routineView.setVisible(true);
        primaryStage.setTitle("Task Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
