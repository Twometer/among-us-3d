package de.twometer.amongus3d.model.player;

import de.twometer.amongus3d.model.world.TaskDef;

import java.util.ArrayList;
import java.util.List;

public class PlayerTask {

    private List<TaskDef> tasks = new ArrayList<>();

    private int progress = 0;

    public TaskDef nextTask() {
        return tasks.get(progress);
    }

    public boolean isDone() {
        return progress == tasks.size();
    }

    public boolean isLongTask() {
        return tasks.size() > 1;
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

    @Override
    public String toString() {
        TaskDef nextTask = nextTask();
        String base = nextTask.getRoom().toString() + ": ";

        return super.toString();
    }
}
