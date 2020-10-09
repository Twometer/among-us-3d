package de.twometer.amongus3d.model.player;

import de.twometer.amongus3d.model.world.TaskDef;

public class PlayerTask {

    private TaskDef task;

    private boolean isCompleted;

    public PlayerTask(TaskDef task) {
        this.task = task;
    }

    public TaskDef getTask() {
        return task;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
