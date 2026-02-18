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

    public List<Task> getAllTasks(){
        String sql = "SELECT * FROM Tasks";
        List<Task> tasks = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Task task = new Task(rs.getString("TaskName"));
                task.setId(rs.getInt("TaskID"));
                Integer categoryId = rs.getInt("CategoryID");
                if (rs.wasNull()) {
                    categoryId = null;  // lub zostawiasz Integer = null
                }
                task.setCategoryID(categoryId);
                task.setDescription(rs.getString("Description"));
                String durationString = rs.getString("ExpectedDuration");
                String goalETString = rs.getString("GoalEndTime");
                String deadlineString = rs.getString("Deadline");
                if (durationString != null){
                    task.setExpectedDuration(Duration.parse(durationString));
                }

                if (goalETString != null){
                    task.setGoalEndTime(LocalDateTime.parse(goalETString));
                }

                if (deadlineString != null){
                    task.setGoalEndTime(LocalDateTime.parse(deadlineString));
                }

                tasks.add(task);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return tasks;
    }

    public List<Task> getCategoryTasks(Integer Category){
        String sql
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

        } catch (SQLException e) {
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
            System.out.println(task.getCategoryID());
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
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }


}

