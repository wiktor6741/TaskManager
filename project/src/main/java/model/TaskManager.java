package model;

import dao.CategoryDAO;
import dao.TaskDAO;
import util.OperationMode;
import util.ValidationResult;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.*;

import static util.OperationMode.EDIT;

public class TaskManager {
    private List<Task> tasks;
    private final Map<Integer, Category> categories = new HashMap<>();
    private final TaskDAO taskDAO;
    private final CategoryDAO categoryDAO;
    private Category currentViewedCategory;

    public TaskManager(Connection conn) {
        this.taskDAO = new TaskDAO(conn);
        this.categoryDAO = new CategoryDAO(conn);
        tasks = taskDAO.getAllTasks();
        for (Category category : categoryDAO.getAllCategories()){
            categories.put(category.getId(), category);
        }
    }

    public Category getTaskCategory(Task task){
        return categories.get(task.getCategoryID());
    }

    public void toggleCategory(int index) {
        if (index >= categories.size()) {
            throw new RuntimeException("no such index in categories");
        }
        Category category = categories.get(index);
        tasks = taskDAO.getCategoryTasks(category.getId());
        currentViewedCategory = category;
    }

    public void addTask(Task task) {
        taskDAO.addTask(task);
        if (currentViewedCategory == null || task.getCategoryID() == currentViewedCategory.getId()) {
            tasks.add(task);
        }
    }

    public void deleteTask(Task task) {
        taskDAO.deleteTask(task.getId());
        if (currentViewedCategory == null || task.getCategoryID() == currentViewedCategory.getId()) {
            tasks.remove(task);
        }
    }

    public void addCategory(Category category) {
        categoryDAO.addCategory(category);
        categories.put(category.getId(), category);
    }

    public void deleteCategory(Category category){
        categoryDAO.deleteCategory(category.getId());
        categories.remove(category);
    }

    public void editCategory(Category category){
        categoryDAO.updateCategory(category);
    }

    public void editTask(Task task){
        taskDAO.updateTask(task);
    }

    public void sortByDeadline(){
        tasks.sort(
                Comparator.comparing(Task::getDeadline, Comparator.nullsLast(LocalDateTime::compareTo))
        );
    }

    public void sortByPriority() {
        tasks.sort(
                Comparator.comparing(Task::getPriority, Comparator.nullsLast(Integer::compareTo))
        );
    }

    public List<Task> getTasks(){
        return tasks;
    }

    public List<Category> getCategoryList(){
        return new ArrayList<>(categories.values());
    }

    public ValidationResult validateTask(Task task, OperationMode mode){
        if (task.getName().length() > 20){
            return new ValidationResult(false, "Name should be less than 20 characters");
        }

        if (task.getDeadline() != null && task.getDeadline().isBefore(LocalDateTime.now())){
            return new ValidationResult(false, "Deadline cannot be in the past");
        }

        if (task.getDeadline() != null && task.getGoalEndTime() != null && task.getDeadline().isBefore(task.getGoalEndTime())){
            return new ValidationResult(false, "Deadline cannot be before goal finish time");
        }

        for (Task t : taskDAO.getAllTasks()){
            if (mode == EDIT && task.getId() == t.getId()){
                continue;
            }
            if (task.getName().equals(t.getName())) return new ValidationResult(false, "There already exists task " + task.getName());
        }

        return new ValidationResult(true, "ok");
    }
}
