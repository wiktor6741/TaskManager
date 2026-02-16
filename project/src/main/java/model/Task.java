package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private final int id;
    private Category category = null;
    private String name;
    private String description;
    private Duration expectedDuration;
    private LocalDateTime goalEndTime;
    private LocalDateTime deadline;

    private Task(int id, String name, String description, Duration expectedDuration,
                 LocalDateTime goalEndTime, LocalDateTime deadline){
        this.id = id;
        this.name = name;
        this.description = description;
        this.expectedDuration = expectedDuration;
        this.goalEndTime = goalEndTime;
        this.deadline = deadline;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setExpectedDuration(Duration expectedDuration) {
        this.expectedDuration = expectedDuration;
    }

    public Duration getExpectedDuration() {
        return expectedDuration;
    }

    public void setGoalEndTime(LocalDateTime goalEndTime) {
        this.goalEndTime = goalEndTime;
    }

    public LocalDateTime getGoalEndTime() {
        return goalEndTime;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }
}
