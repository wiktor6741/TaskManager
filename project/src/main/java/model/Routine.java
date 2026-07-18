package model;

import util.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Routine {
    private int id;
    private String name;
    private int weekCount;
    private Map<RoutineTimeSpec, RoutineElement> routineTimes;

    public Routine(int weekCount, String name){
        this.weekCount = weekCount;
        this.name = name;
        routineTimes = new HashMap<>();
    }

    public ValidationResult validateTimeSpec(RoutineTimeSpec timeSpec){
        if (!Duration.between(timeSpec.start(), timeSpec.end()).isPositive()){
            return new ValidationResult(false, "End must be greater than start");
        }

        for (RoutineTimeSpec spec : routineTimes.keySet()){
            if (spec.isConflicting(timeSpec)){
                return new ValidationResult(false, "There exists a confilcting routine element");
            }
        }

        return new ValidationResult(true, "ok");
    }

    public ValidationResult validateTimeSpecChange(RoutineTimeSpec oldSpec, RoutineTimeSpec newSpec){
        for (RoutineTimeSpec spec : routineTimes.keySet()){
            if (spec != oldSpec && spec.isConflicting(newSpec)){
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
        if (weekCount < 1){
            throw new IllegalArgumentException("Routine week count smaller than 1");
        }
        if (weekCount < this.weekCount) {
            for (RoutineTimeSpec spec : routineTimes.keySet()){
                if (spec.weekNum() > weekCount){
                    routineTimes.remove(spec);
                }
            }
        }
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

        return  elementTimePairs;
    }


    public List<ElementTimePair> getWeek(int weekNum){
        List<ElementTimePair> elementTimePairs = new ArrayList<>();
        for (Map.Entry<RoutineTimeSpec, RoutineElement> entry : routineTimes.entrySet()) {
            RoutineTimeSpec spec = entry.getKey();
            RoutineElement element = entry.getValue();
            if (spec.weekNum() == weekNum) {
                elementTimePairs.add(new ElementTimePair(element, spec));
            }
        }
        return  elementTimePairs;
    }

    @Override
    public String toString() {
        return name;
    }


}