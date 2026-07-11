package model;

import util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Routine {
    private int id;
    private String name;
    private int weekCount;
    private Map<RoutineTimeSpec, RoutineElement> routineTimes;

    public Routine(int weekNum, String name){
        this.weekCount = weekNum;
        this.name = name;
        routineTimes = new HashMap<>();
    }

    public ValidationResult validateTimeSpec(RoutineTimeSpec timeSpec){
        for (RoutineTimeSpec spec : routineTimes.keySet()){
            if (spec.isConflicting(timeSpec)){
                return new ValidationResult(false, "There exists a confilcting routine element");
            }
        }
        return new ValidationResult(true, "ok");
    }

    public void addElement(RoutineElement element, RoutineTimeSpec timeSpec) {
        routineTimes.put(timeSpec, element);
    }


    public void deleteElement(RoutineTimeSpec timeSpec){
        routineTimes.remove(timeSpec);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWeekCount(int weekCount) {
        this.weekCount = weekCount;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public int getWeekCount(){
        return weekCount;
    }

    public Map<RoutineTimeSpec, RoutineElement> getRoutineTimes() {
        return routineTimes;
    }

    public List<ElementTimePair> getDay(Weekday weekday, int weekNum){
        List<ElementTimePair> elementTimePairs = new ArrayList<>();
        for (Map.Entry<RoutineTimeSpec, RoutineElement> entry : routineTimes.entrySet()) {
            RoutineTimeSpec spec = entry.getKey();
            RoutineElement element = entry.getValue();
            if (spec.weekday() == weekday && spec.weekNum() == weekNum) {
                elementTimePairs.add(new ElementTimePair(element, spec));
            }
        }

        elementTimePairs.sort(null);
        return  elementTimePairs;
    }


}