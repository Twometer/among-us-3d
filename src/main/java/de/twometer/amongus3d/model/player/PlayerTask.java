package de.twometer.amongus3d.model.player;

import de.twometer.amongus3d.model.world.TaskDef;

import java.util.ArrayList;
import java.util.List;

public class PlayerTask {

    private List<TaskDef> tasks = new ArrayList<>();

    private int progress = 0;

    public boolean hasRemaining(TaskDef def) {
        for (int i = progress; i < tasks.size(); i++)
            if (tasks.get(i).equals(def))
                return true;
        return false;
    }

    public boolean isLongTask() {
        return tasks.size() > 0;
    }

    public List<TaskDef> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDef> tasks) {
        this.tasks = tasks;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public PlayerTask addTask(TaskDef taskDef) {
        tasks.add(taskDef);
        return this;
    }
}
