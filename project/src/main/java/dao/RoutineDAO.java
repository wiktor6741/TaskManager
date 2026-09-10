package dao;

import model.Routine;
import model.RoutineElement;
import util.ConflictingTimeSpecsException;
import util.RoutineTimeSpec;
import util.Weekday;

import java.sql.*;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class RoutineDAO {
    private final Connection conn;

    public RoutineDAO(Connection conn) {
        this.conn = conn;
    }

    public Map<Integer,RoutineElement> getAllRoutineElements() {
        String sql = "SELECT * FROM routine_elements WHERE deleted_at IS NULL";
        Map<Integer,RoutineElement> routineElements = new HashMap<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RoutineElement routineElement = new RoutineElement(rs.getString("element_name"));
                    int id = rs.getInt("routine_element_id");
                    routineElement.setId(id);
                    routineElement.setDesc(rs.getString("description"));
                    routineElements.put(id ,routineElement);
                }
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return routineElements;
    }

    public void addRoutineElement(RoutineElement element){
        String sql = """
                INSERT INTO routine_elements
                (element_name, description)
                VALUES (?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, element.getName());
            ps.setString(2, element.getDesc());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    element.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateRoutineElement(RoutineElement element){
        String sql = """
                UPDATE routine_elements
                SET
                    element_name = ?,
                    description = ?
                WHERE routine_element_id = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, element.getName());
            ps.setString(2, element.getDesc());
            ps.setInt(3, element.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteRoutineElement(int id){
        String sql = """
                UPDATE routine_elements
                SET deleted_at = strftime('%Y-%m-%dT%H:%M:00', 'now')
                WHERE routine_element_id = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);

            ps.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void addRoutine(Routine routine){
        String sql = """
                INSERT INTO routines
                (routine_name, week_count)
                VALUES (?, ?)""";


        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, routine.getName());
            ps.setInt(2, routine.getWeekCount());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    routine.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteRoutine(int id){
        String sql = """
                UPDATE routines
                SET deleted_at = strftime('%Y-%m-%dT%H:%M:00', 'now')
                WHERE routine_id = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);

            ps.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void updateRoutine(Routine routine){
        String sql = """
                UPDATE routines
                SET
                    routine_name = ?,
                    week_count = ?
                WHERE routine_id =?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, routine.getName());
            ps.setInt(2, routine.getWeekCount());
            ps.setInt(3, routine.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<Integer, Routine> getAllRoutinesEmpty() {
        String sql = "SELECT * FROM routines WHERE deleted_at IS NULL";
        Map<Integer, Routine> routineIdMap = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Integer id = rs.getInt("routine_id");
                    String name = rs.getString("routine_name");
                    int weekCount = rs.getInt("week_count");
                    Routine routine = new Routine(weekCount, name);
                    routine.setId(id);

                    routineIdMap.put(id, routine);
                }
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return routineIdMap;
    }

    public Map<Integer, Routine> getAllRoutines(Map<Integer, RoutineElement> routineElementsIdMap){
        Map<Integer, Routine> routineIdMap = getAllRoutinesEmpty();
        String sql = "SELECT * FROM routine_times WHERE deleted_at IS NULL";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    LocalTime startTime = LocalTime.parse(rs.getString("start_time"));
                    LocalTime endTime = LocalTime.parse(rs.getString("end_time"));
                    Weekday weekday = Weekday.parse(rs.getString("weekday"));
                    int weeknum = rs.getInt("week_num");
                    RoutineTimeSpec timeSpec = new RoutineTimeSpec(startTime, endTime, weekday, weeknum);

                    Integer routineID = rs.getInt("routine_id");
                    Integer routineElementID = rs.getInt("routine_element_id");

                    RoutineElement routineElement = routineElementsIdMap.get(routineElementID);
                    Routine routine = routineIdMap.get(routineID);

                    routine.addElement(routineElement, timeSpec);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return routineIdMap;
    }

    public void addElementToRoutine(Routine routine, RoutineElement element, RoutineTimeSpec timeSpec){
        String sql = """
                INSERT INTO routine_times
                (routine_id, routine_element_id, week_num, weekday, start_time, end_time)
                VALUES (?, ?, ?, ?, ?, ?)""";

        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, routine.getId());
            ps.setInt(2, element.getId());
            ps.setInt(3, timeSpec.weekNum());
            ps.setString(4, timeSpec.weekday().toString());
            ps.setString(5, timeSpec.start().toString());
            ps.setString(6, timeSpec.end().toString());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void deleteElementFromRoutine(Routine routine, RoutineTimeSpec timeSpec){
        String sql = """
                UPDATE routine_times
                SET deleted_at = strftime('%Y-%m-%dT%H:%M:00', 'now')
                WHERE routine_id = ? AND week_num = ? AND weekday = ? AND start_time = ?""";

        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, routine.getId());
            ps.setInt(2, timeSpec.weekNum());
            ps.setString(3, timeSpec.weekday().toString());
            ps.setString(4, timeSpec.start().toString());

            ps.executeUpdate();
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        String[] statements = {
                "DELETE FROM routine_times",
                "DELETE FROM routines",
                "DELETE FROM routine_elements"
        };

        try (Statement stmt = conn.createStatement()) {
            for (String sql : statements) {
                stmt.executeUpdate(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
