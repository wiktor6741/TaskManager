package util;

import model.RoutineElement;

public record ElementTimePair(RoutineElement element, RoutineTimeSpec timeSpec) implements Comparable<ElementTimePair>{
    @Override
    public int compareTo(ElementTimePair other){
        return this.timeSpec.start().compareTo(other.timeSpec.start());
    }
}
