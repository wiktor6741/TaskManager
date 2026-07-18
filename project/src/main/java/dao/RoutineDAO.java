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
        String sql = "SELECT * FROM RoutineElements";
        Map<Integer,RoutineElement> routineElements = new HashMap<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RoutineElement routineElement = new RoutineElement(rs.getString("ElementName"));
                    int id = rs.getInt("RoutineElementID");
                    routineElement.setId(id);
                    routineElement.setDesc(rs.getString("Description"));
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
                INSERT INTO RoutineElements
                (ElementName, Description)
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
                UPDATE RoutineElements
                SET
                    ElementName = ?,
                    Description = ?
                WHERE RoutineElementID = ?
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
                DELETE FROM RoutineElements
                WHERE RoutineElementID = ?
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
                INSERT INTO Routines
                (RoutineName, WeekCount)
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
                DELETE FROM Routines
                WHERE RoutineID = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);

            ps.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void updateRoutine(Routine routine){
        System.out.println("DAO updating routine " + routine.getName());
        String sql = """
                UPDATE Routines
                SET
                    RoutineName = ?,
                    WeekCount = ?
                WHERE RoutineID =?
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
        String sql = "SELECT * FROM Routines";
        Map<Integer, Routine> routineIdMap = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Integer id = rs.getInt("RoutineID");
                    String name = rs.getString("RoutineName");
                    int weekCount = rs.getInt("WeekCount");
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
        String sql = "SELECT * FROM RoutineTimes";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    LocalTime startTime = LocalTime.parse(rs.getString("StartTime"));
                    LocalTime endTime = LocalTime.parse(rs.getString("EndTime"));
                    Weekday weekday = Weekday.parse(rs.getString("Weekday"));
                    int weeknum = rs.getInt("WeekNum");
                    RoutineTimeSpec timeSpec = new RoutineTimeSpec(startTime, endTime, weekday, weeknum);

                    Integer routineID = rs.getInt("RoutineID");
                    Integer routineElementID = rs.getInt("RoutineElementID");

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
                INSERT INTO RoutineTimes
                (RoutineID, RoutineElementID, WeekNum, Weekday, StartTime, EndTime)
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
                DELETE FROM RoutineTimes
                WHERE RoutineID = ? AND WeekNum = ? AND Weekday = ? AND StartTime = ?""";

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
                "DELETE FROM RoutineTimes",
                "DELETE FROM Routines",
                "DELETE FROM RoutineElements"
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
