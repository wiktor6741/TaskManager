package dao;

import model.Task;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    private final Connection conn;

    public TaskDAO(Connection conn){
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
                    Task task = new Task(rs.getString("TaskName"));

                    task.setId(rs.getInt("TaskID"));

                    Integer categoryId = rs.getInt("CategoryID");
                    if (rs.wasNull()) {
                        categoryId = null;
                    }
                    task.setCategoryID(categoryId);

                    task.setDescription(rs.getString("Description"));

                    String durationString = rs.getString("ExpectedDuration");
                    String goalETString = rs.getString("GoalEndTime");
                    String deadlineString = rs.getString("Deadline");

                    if (durationString != null) {
                        task.setExpectedDuration(Duration.parse(durationString));
                    }

                    if (goalETString != null) {
                        task.setGoalEndTime(LocalDateTime.parse(goalETString));
                    }

                    if (deadlineString != null) {
                        task.setDeadline(LocalDateTime.parse(deadlineString)); // TU poprawiłem błąd
                    }

                    tasks.add(task);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return tasks;
    }

    public List<Task> getAllTasks(){
        return getQueryTasks("SELECT * FROM Tasks");
    }

    public List<Task> getCategoryTasks(Integer CategoryID){
        return getQueryTasks("SELECT * FROM Tasks WHERE CategoryID = ?",
                CategoryID);
    }

    public void addTask(Task task){
        String sql = """
            INSERT INTO Tasks 
            (CategoryID, TaskName, Description, ExpectedDuration, GoalEndTime, Deadline)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
                DELETE FROM TASKS
                WHERE TaskID = ?
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
                    CategoryID = ?,
                    TaskName = ?,
                    Description = ?,
                    ExpectedDuration = ?,
                    GoalEndTime = ?,
                    Deadline = ?
                WHERE TaskID = ?""";

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
            ps.setInt(7, task.getId());

            ps.executeUpdate();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void clear(){
        String sql = "DELETE FROM Tasks";
        try (Statement stmt = conn.createStatement()){
            stmt.executeUpdate(sql);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}

