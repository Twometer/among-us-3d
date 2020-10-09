package de.twometer.amongus3d.model;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final String username;

    private final Vector3f position;

    private final Role role;

    private final List<Task> remainingTasks = new ArrayList<>();

    public Player(String username, Vector3f position, Role role) {
        this.username = username;
        this.position = position;
        this.role = role;
    }

    public boolean canDoTask(Task task) {
        return remainingTasks.contains(task);
    }

    public List<Task> getRemainingTasks() {
        return remainingTasks;
    }

    public String getUsername() {
        return username;
    }

    public Vector3f getPosition() {
        return position;
    }



    public Role getRole() {
        return role;
    }
}
