package model;

import dao.CategoryDAO;
import dao.TaskDAO;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TaskManager {
    private List<Task> tasks;
    private List<Category> categories;
    private final TaskDAO taskDAO;
    private final CategoryDAO categoryDAO;
    private Category currentViewedCategory;

    public TaskManager(Connection conn) {
        this.taskDAO = new TaskDAO(conn);
        this.categoryDAO = new CategoryDAO(conn);
        tasks = taskDAO.getAllTasks();
        categories = categoryDAO.getAllCategories();
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
        categories.add(category);
    }

    public void deleteCategory(Category category){
        categoryDAO.deleteCategory(category.getId());
        categories.remove(category);
    }

    public void updateCategory(Category category){
        categoryDAO.updateCategory(category);
    }

    public void updateTask(Task task){
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
}
