import dao.CategoryDAO;
import dao.DatabaseManager;
import dao.RoutineDAO;
import dao.TaskDAO;
import model.*;
import util.ConflictingTimeSpecsException;
import util.RoutineTimeSpec;
import util.Weekday;

import java.io.IOException;
import java.sql.Connection;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class Main {
    static DatabaseManager dbManager = new DatabaseManager();
    static Connection conn = dbManager.getConnection();
    static CategoryDAO categoryDAO = new CategoryDAO(conn);
    static TaskDAO taskDAO = new TaskDAO(conn);
    static RoutineService routineService = new RoutineService(conn);
    static RoutineDAO routineDAO = new RoutineDAO(conn);

    public void main() {
        routineService.clear();
        routineSetup();
        routineElementAssignments();
    }

    public void routineSetup(){
        try {
            Routine routine = routineService.createRoutine("Rutyna bambika", 1);
            Routine routine1 = routineService.createRoutine("Rutyna bambika 1", 2);
            RoutineElement element = routineService.createRoutineElement("Element bambika");
            RoutineElement element1 = routineService.createRoutineElement("Element bambika 1");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void routineElementAssignments(){
        List<Routine> routines = routineService.getRoutineList();
        List<RoutineElement> elements = routineService.getRoutineElementList();
        LocalTime start1 = LocalTime.of(7, 30);
        LocalTime end1 = LocalTime.of(9, 30);
        LocalTime start2 = LocalTime.of(13, 30);
        LocalTime end2 = LocalTime.of(15, 30);
        RoutineTimeSpec timeSpec1 = new RoutineTimeSpec(start1, end1, Weekday.MON, 1);
        RoutineTimeSpec timeSpec2 = new RoutineTimeSpec(start2, end2, Weekday.THU, 2);
        try {
            routineService.assignRoutineElement(routines.get(0), elements.get(0), timeSpec1);
            routineService.assignRoutineElement(routines.get(1), elements.get(1), timeSpec2);
            routineService.assignRoutineElementDaily(routines.get(1), elements.get(0), start1, end1);
        } catch (ConflictingTimeSpecsException e) {
            throw new RuntimeException(e);
        }
    }

    public void taskAdditions(){
        for (int i = 0; i < 8; i++) {
            Task task1 = new Task("Task" + Integer.toString(i));
            task1.setPriority(4);
            task1.setDeadline(LocalDateTime.parse("2026-04-23T22:00"));
            taskDAO.addTask(task1);
        }
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

