package util;

import model.Routine;

import java.time.LocalTime;

public record RoutineTimeSpec(LocalTime start, LocalTime end, Weekday weekday, int weekNum) {
    public boolean isConflicting(RoutineTimeSpec other) {
            return other.weekday == this.weekday && other.weekNum == this.weekNum &&
                    other.end().toSecondOfDay() > this.start.toSecondOfDay() &&
                    other.start.toSecondOfDay() < this.end.toSecondOfDay();
    }
}
