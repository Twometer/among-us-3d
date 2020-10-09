package de.twometer.amongus3d.model.world;

import java.util.Objects;

public class TaskDef {

    private final Room room;

    private final TaskType taskType;

    public TaskDef(Room room, TaskType taskType) {
        this.room = room;
        this.taskType = taskType;
    }

    public Room getRoom() {
        return room;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskDef task = (TaskDef) o;
        return room == task.room &&
                taskType == task.taskType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(room, taskType);
    }
}
