package model;

import dao.RoutineDAO;
import util.*;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalTime;
import java.util.*;

public class RoutineService {
    private RoutineDAO routineDAO;
    private Map<Integer, RoutineElement> routineElements;
    private Map<Integer, Routine> routines;
    private Routine currentViewedRoutine;
    private int currentViewedWeek;

    public RoutineService(Connection conn){
        routineDAO = new RoutineDAO(conn);
        routineElements = routineDAO.getAllRoutineElements();
        routines = routineDAO.getAllRoutines(routineElements);
        if (!routines.isEmpty()) {currentViewedRoutine = routines.get(Collections.min(routines.keySet())); }
        currentViewedWeek = 1;
    }

    public Routine createRoutine(String name, int weekCount) throws IOException {
        for (Routine routine : routines.values()){
            if (routine.getName().equals(name)) {
                throw new IOException("Name already exists");
            }
        }
        if (name.length() > 30){
            throw new IOException("Name cannot be longer than 30 characters");
        }

        if (name.isEmpty()){
            throw new IOException("Name cannot be empty");
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
        if (routine == currentViewedRoutine){
            if (!routines.isEmpty()) {
                currentViewedRoutine = routines.get(Collections.min(routines.keySet()));
            } else{
                currentViewedRoutine = null;
            }
            currentViewedWeek = 1;
        }
    }

    public RoutineElement createRoutineElement(String name) throws IOException{
        for (RoutineElement element : routineElements.values()){
            if (element.getName().equals(name)) {
                throw new IOException("Name already exists");
            }
        }

        if (name.length() > 30){
            throw new IOException("Name cannot be longer than 30 characters");
        }

        if (name.isEmpty()){
            throw new IOException("Name cannot be empty");
        }

        RoutineElement element = new RoutineElement(name);
        routineDAO.addRoutineElement(element);
        routineElements.put(element.getId(), element);
        return element;
    }

    public void editRoutineElementName(RoutineElement selected, String name) throws IOException {
        if (!routineElements.containsValue(selected)){
            throw new IllegalArgumentException("No such element in service");
        }
        for (RoutineElement element : routineElements.values()){
            if (selected != element && element.getName().equals(name)) {
                throw new IOException("Name already exists");
            }
        }
        selected.setName(name);
        routineDAO.updateRoutineElement(selected);
    }

    public void editRoutineName(Routine selected, String name) throws IOException {
        if (!routines.containsValue(selected)){
            throw new IllegalArgumentException("No such routine in service");
        }
        for (Routine routine : routines.values()){
            if (selected != routine && routine.getName().equals(name)) {
                throw new IOException("Name already exists");
            }
        }
        routineDAO.updateRoutine(selected);
        System.out.println(selected.getName());
        selected.setName(name);
    }

    public void editRoutineWeekCount(Routine selected, int weekCount){
        if (!routines.containsValue(selected)){
            throw new IllegalArgumentException("No such routine in service");
        }
        selected.setWeekCount(weekCount);
        if (selected == currentViewedRoutine && weekCount < currentViewedWeek){
            currentViewedWeek = weekCount;
            System.out.println("Weekcount right after update: " + currentViewedWeek);
        }

        routineDAO.updateRoutine(selected);
    }

    public void deleteRoutineElement(RoutineElement element){
        if (!routineElements.containsValue(element)){
            throw new IllegalArgumentException("No such element in service");
        }
        int id = element.getId();
        routineElements.remove(id);
        routineDAO.deleteRoutineElement(id);
        routines = routineDAO.getAllRoutines(routineElements);
        currentViewedRoutine = routines.get(currentViewedRoutine.getId());
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
        for (int i = 1; i <= routine.getWeekCount(); i++) {
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

    public void changeTimeSpec(Routine routine, RoutineTimeSpec oldSpec, RoutineTimeSpec newSpec) throws ConflictingTimeSpecsException {
        RoutineElement element = routine.getRoutineTimes().get(oldSpec);
        if (element != null) {
            ValidationResult result = routine.validateTimeSpecChange(oldSpec, newSpec);
            if (result.isValid()) {
                routine.deleteElement(oldSpec);
                routineDAO.deleteElementFromRoutine(routine, oldSpec);
                routine.addElement(element, newSpec);
                routineDAO.addElementToRoutine(routine, element, newSpec);
            } else {
                throw new ConflictingTimeSpecsException(result.message());
            }
        } else {
            throw new IllegalArgumentException("No element for such timestamp");
        }
    }

    public void swapElements(Routine routine, RoutineTimeSpec timeSpec, RoutineElement element){
        RoutineElement oldElement = routine.getRoutineTimes().get(timeSpec);
        if (oldElement != null) {
            routine.deleteElement(timeSpec);
            routineDAO.deleteElementFromRoutine(routine, timeSpec);
            routine.addElement(element, timeSpec);
            routineDAO.addElementToRoutine(routine, element, timeSpec);
        } else {
            throw new IllegalArgumentException("No element for such timestamp");
        }
    }

    public void setCurrentViewedRoutine(Routine routine){
        if (!routines.containsValue(routine)){
            throw new IllegalArgumentException("No such routine in routines");
        }
        currentViewedRoutine = routine;
        currentViewedWeek = 1;
        System.out.println("set routine called");
    }

    public Routine getCurrentViewedRoutine(){
        return currentViewedRoutine;
    }

    public void incrementWeek(){
        if (currentViewedRoutine != null && currentViewedRoutine.getWeekCount() >= currentViewedWeek + 1){
            currentViewedWeek += 1;
        }
    }

    public void decrementWeek(){
        if (currentViewedRoutine != null && currentViewedWeek > 1){
            currentViewedWeek -= 1;
        }
    }

    public List<ElementTimePair> getWeek(){
        if (currentViewedRoutine == null){
            return null;
        }
        return currentViewedRoutine.getWeek(currentViewedWeek);
    }

    public void clear(){
        routineDAO.clear();
        routineElements = new HashMap<>();
        routines = new HashMap<>();
    }

    public int getWeekNum(){
        return currentViewedWeek;
    }
}
