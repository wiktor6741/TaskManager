import dao.DatabaseManager;
import dao.TaskDAO;
import model.Task;

import java.sql.Connection;
import java.time.Duration;
import java.util.List;

public class Main {
    static void main() {
        DatabaseManager dbManager = new DatabaseManager();
        Connection conn = dbManager.getConnection();
        System.out.println(conn);
        TaskDAO taskDAO = new TaskDAO(conn);
        //Task task1 = new Task("Task1");
        List<Task> tasks = taskDAO.getAllTasks();
        Task taskToEdit = tasks.get(0);
        System.out.println(taskToEdit);
        taskToEdit.setDescription("blablable");
        taskToEdit.setName("taskito");
        taskToEdit.setExpectedDuration(Duration.parse("PT10H"));
        taskDAO.updateTask(taskToEdit);
    }
}
