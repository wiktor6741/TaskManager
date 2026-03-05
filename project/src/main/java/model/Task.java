package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private int id;
    private Integer categoryID = null;
    private String name;
    private String description;
    private Duration expectedDuration;
    private LocalDateTime goalEndTime;
    private LocalDateTime deadline;

    public Task(String name) {
        this.name = name;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setCategoryID(Integer categoryID) {
        this.categoryID = categoryID;
    }

    public Integer getCategoryID() {
        return categoryID;
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

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
