import dao.CategoryDAO;
import dao.DatabaseManager;
import dao.TaskDAO;
import model.Category;
import model.Task;

import java.sql.Connection;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    static DatabaseManager dbManager = new DatabaseManager();
    static Connection conn = dbManager.getConnection();
    static CategoryDAO categoryDAO = new CategoryDAO(conn);
    static TaskDAO taskDAO = new TaskDAO(conn);

    public void main() {
        clear();
        taskAdditions();
        categoryAdditions();
        taskModifications();
        categoryAssignments();
        printTasks();
        printCategories();
        printCategoryTasks(1);

    }


    public void taskAdditions(){
        Task task1 = new Task("Task1");
        task1.setPriority(4);
        task1.setDeadline(LocalDateTime.parse("2026-04-23T22:00"));
        taskDAO.addTask(task1);

    }

    public void categoryAdditions(){
        Category category = new Category("kategoria1");
        categoryDAO.addCategory(category);
    }

    public void taskModifications(){
        List<Task> tasks = taskDAO.getAllTasks();
        Task taskToEdit = tasks.get(0);
        taskToEdit.setDescription("blablable");
        taskToEdit.setName("taskito");
        taskToEdit.setExpectedDuration(Duration.parse("PT10H"));
        taskDAO.updateTask(taskToEdit);
    }

    public void categoryAssignments(){
        List<Task> tasks = taskDAO.getAllTasks();
        Task task = tasks.get(0);

        List<Category> categories = categoryDAO.getAllCategories();
        task.setCategoryID(categories.get(0).getId());
        taskDAO.updateTask(task);
    }

    public void printCategories(){
        List<Category> categories = categoryDAO.getAllCategories();
        for (Category c : categories){
            System.out.println(c);
        }
    }

    public void printTasks(){
        List<Task> tasks = taskDAO.getAllTasks();
        for (Task t : tasks){
            System.out.println(t);
        }
    }

    public void printCategoryTasks(int categoryID){
        List<Task> tasks = taskDAO.getCategoryTasks(categoryID);
        for (Task t : tasks){
            System.out.println(t);
        }
    }

    public void clear(){
        taskDAO.clear();
        categoryDAO.clear();
    }



}

