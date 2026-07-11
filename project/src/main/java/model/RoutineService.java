package model;

import dao.RoutineDAO;
import util.ConflictingTimeSpecsException;
import util.RoutineTimeSpec;
import util.ValidationResult;
import util.Weekday;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutineService {
    private RoutineDAO routineDAO;
    private Map<Integer, RoutineElement> routineElements;
    private Map<Integer, Routine> routines;

    public RoutineService(Connection conn){
        routineDAO = new RoutineDAO(conn);
        routineElements = routineDAO.getAllRoutineElements();
        routines = routineDAO.getAllRoutines(routineElements);
    }

    public Routine createRoutine(String name, int weekCount) throws IOException {
        for (Routine routine : routines.values()){
            if (routine.getName().equals(name)) {
                throw new IOException("Name already exists");
            }
        }
        Routine routine = new Routine(weekCount, name);
        routineDAO.addRoutine(routine);
        routines.put(routine.getId(), routine);
        return routine;
    }

    public void deleteRoutine(Routine routine){
        if (!routines.containsValue(routine)){
            throw new IllegalArgumentException("No such routine in service");
        }
        int id = routine.getId();
        routines.remove(id);
        routineDAO.deleteRoutine(id);
    }

    public RoutineElement createRoutineElement(String name) throws IOException{
        for (RoutineElement element : routineElements.values()){
            if (element.getName().equals(name)) {
                throw new IOException("Name already exists");
            }
        }
        RoutineElement element = new RoutineElement(name);
        routineDAO.addRoutineElement(element);
        routineElements.put(element.getId(), element);
        return element;
    }

    public void deleteRoutineElement(RoutineElement element){
        if (!routineElements.containsValue(element)){
            throw new IllegalArgumentException("No such element in service");
        }
        int id = element.getId();
        routineElements.remove(id);
        routineDAO.deleteRoutineElement(id);
        routines = routineDAO.getAllRoutines(routineElements);
    }

    public List<Routine> getRoutineList(){
        return new ArrayList<>(routines.values());
    }

    public List<RoutineElement> getRoutineElementList(){
        return new ArrayList<>(routineElements.values());
    }

    public void assignRoutineElement(Routine routine, RoutineElement element, RoutineTimeSpec timeSpec) throws ConflictingTimeSpecsException {
        ValidationResult result = routine.validateTimeSpec(timeSpec);
        if (result.isValid()) {
            routine.addElement(element, timeSpec);
            routineDAO.addElementToRoutine(routine, element, timeSpec);
        } else {
            throw new ConflictingTimeSpecsException(result.message());
        }
    }

    public void assignRoutineElementDaily(Routine routine, RoutineElement element, LocalTime start, LocalTime end) throws ConflictingTimeSpecsException{
        List<RoutineTimeSpec> timeSpecs = new ArrayList<>();
        for (int i = 0; i < routine.getWeekCount(); i++) {
            for (Weekday weekday : Weekday.values()){
                RoutineTimeSpec timeSpec = new RoutineTimeSpec(start, end, weekday, i);
                ValidationResult result = routine.validateTimeSpec(timeSpec);
                if (result.isValid()){
                    timeSpecs.add(timeSpec);
                } else {
                    throw new ConflictingTimeSpecsException(result.message());
                }
            }
        }
        for (RoutineTimeSpec timeSpec : timeSpecs){
            routine.addElement(element, timeSpec);
            routineDAO.addElementToRoutine(routine, element, timeSpec);
        }
    }

    public void unassignRoutineElement(Routine routine, RoutineTimeSpec timeSpec){
        RoutineElement element = routine.getRoutineTimes().get(timeSpec);
        if (element != null) {
            routine.deleteElement(timeSpec);
            routineDAO.deleteElementFromRoutine(routine, timeSpec);
        } else {
            throw new IllegalArgumentException("No element for such timestamp");
        }
    }

    public void clear(){
        routineDAO.clear();
        routineElements = new HashMap<>();
        routines = new HashMap<>();
    }
}
