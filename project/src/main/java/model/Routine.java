package model;

import util.ConflictingTimeSpecsException;
import util.RoutineTimeSpec;

import java.util.HashMap;
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

    public void addElement(RoutineElement element, RoutineTimeSpec timeSpec) throws ConflictingTimeSpecsException {
        for (RoutineTimeSpec spec : routineTimes.keySet()){
            if (spec.isConflicting(timeSpec)){
                throw new ConflictingTimeSpecsException("Conflicting time specs for elements of this routine");
            }
        }
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


}
