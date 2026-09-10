package dao;

import model.Task;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    private final Connection conn;

    public TaskDAO(Connection conn) {
        this.conn = conn;
    }


    private List<Task> getQueryTasks(String sql, Object... params) {
        List<Task> tasks = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Task task = new Task(rs.getString("task_name"));

                    task.setId(rs.getInt("task_id"));

                    Integer categoryId = rs.getInt("category_id");

                    if (rs.wasNull()) {
                        categoryId = null;
                    }
                    task.setCategoryID(categoryId);

                    Integer priority = rs.getInt("priority");

                    if (rs.wasNull()) {
                        priority = null;
                    }

                    task.setPriority(priority);

                    task.setDescription(rs.getString("description"));

                    String durationString = rs.getString("expected_duration");
                    String goalETString = rs.getString("goal_end_time");
                    String deadlineString = rs.getString("deadline");

                    if (durationString != null) {
                        task.setExpectedDuration(Duration.parse(durationString));
                    }

                    if (goalETString != null) {
                        task.setGoalEndTime(LocalDateTime.parse(goalETString));
                    }

                    if (deadlineString != null) {
                        task.setDeadline(LocalDateTime.parse(deadlineString));
                    }

                    tasks.add(task);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return tasks;
    }

    public List<Task> getActiveTasks() {
        return getQueryTasks("SELECT * FROM tasks " +
                "WHERE deleted_at IS NULL AND completed_at IS NULL");
    }

    public List<Task> getCompletedTasks() {
        return getQueryTasks("SELECT * FROM tasks " +
                "WHERE completed_at IS NOT NULL");
    }

    public List<Task> getCategoryTasks(Integer CategoryID){
        return getQueryTasks("SELECT * FROM tasks WHERE category_id = ?",
                CategoryID);
    }

    public void addTask(Task task){
        String sql = """
            INSERT INTO Tasks 
            (category_id, task_name, description, expected_duration, goal_end_time, deadline, priority)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (task.getCategoryID() != null) {
                ps.setInt(1, task.getCategoryID());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            ps.setString(2, task.getName());
            ps.setString(3, task.getDescription());
            ps.setString(4, task.getExpectedDuration() != null ? task.getExpectedDuration().toString() : null);
            ps.setString(5, task.getGoalEndTime() != null ? task.getGoalEndTime().toString() : null);
            ps.setString(6, task.getDeadline() != null ? task.getDeadline().toString() : null);
            if (task.getPriority() != null) {
                ps.setInt(7, task.getPriority());
            }
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    task.setId(rs.getInt(1));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteTask(int id)  {
        String sql = """
                UPDATE tasks
                SET deleted_at = strftime('%Y-%m-%dT%H:%M:00', 'now')
                WHERE task_id = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);

            ps.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void completeTask(int id)  {
        String sql = """
                UPDATE tasks
                SET completed_at = strftime('%Y-%m-%dT%H:%M:00', 'now')
                WHERE task_id = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);

            ps.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void updateTask(Task task){
        String sql = """
                UPDATE Tasks
                SET
                    category_id = ?,
                    task_name = ?,
                    description = ?,
                    expected_duration = ?,
                    goal_end_time = ?,
                    deadline = ?,
                    priority = ?
                WHERE task_id = ?""";

        try (PreparedStatement ps = conn.prepareStatement(sql)){
            if (task.getCategoryID() != null) {
                ps.setInt(1, task.getCategoryID());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            ps.setString(2, task.getName());
            ps.setString(3, task.getDescription());
            ps.setString(4, task.getExpectedDuration() != null ? task.getExpectedDuration().toString() : null);
            ps.setString(5, task.getGoalEndTime() != null ? task.getGoalEndTime().toString() : null);
            ps.setString(6, task.getDeadline() != null ? task.getDeadline().toString() : null);
            if (task.getPriority() != null) {
                ps.setInt(7, task.getPriority());
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }

            ps.setInt(8, task.getId());

            ps.executeUpdate();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void clear(){
        String sql = "DELETE FROM tasks";
        try (Statement stmt = conn.createStatement()){
            stmt.executeUpdate(sql);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}

