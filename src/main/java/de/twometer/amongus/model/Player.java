package de.twometer.amongus.model;

import org.joml.Vector3f;

import java.util.List;

public class Player implements PlayerBehavior {

    public int id;

    public String username;

    public Vector3f position;

    public float rotation;

    public PlayerRole role;

    public PlayerColor color;

    public List<PlayerTask> tasks;

    public boolean alive;

    public int emergencyMeetings = 0;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public PlayerRole getRole() {
        return role;
    }

    public PlayerColor getColor() {
        return color;
    }

    public boolean canDoTask(Location location, TaskType taskType) {
        if (role == PlayerRole.Impostor) return false;
        return findRunningTaskByStage(location, taskType) != null;
    }

    public PlayerTask findRunningTaskByStage(Location location, TaskType taskType) {
        for (var task : tasks) {
            var stage = task.getNextStage();
            if (stage.getTaskType() == taskType && stage.getLocation() == location && !task.isCompleted())
                return task;
        }
        return null;
    }

}
