package de.twometer.amongus.model;

import java.util.Objects;

public class TaskStage {

    private final Location location;

    private final TaskType taskType;

    public TaskStage(Location location, TaskType taskType) {
        this.location = location;
        this.taskType = taskType;
    }

    public Location getLocation() {
        return location;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskStage taskStage = (TaskStage) o;
        return location == taskStage.location &&
                taskType == taskStage.taskType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, taskType);
    }

}
