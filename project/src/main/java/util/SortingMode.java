package util;

public enum SortingMode {
    PRIORITY,
    DEADLINE,
    PRIORITY_THEN_DEADLINE,
    DEADLINE_THEN_PRIORITY;

    @Override
    public String toString() {
        return switch (this){
            case PRIORITY -> "Priority";
            case DEADLINE -> "Deadline";
            case PRIORITY_THEN_DEADLINE -> "Priority -> Deadline";
            case DEADLINE_THEN_PRIORITY -> "Deadline -> Priority";
        };
    }
}
