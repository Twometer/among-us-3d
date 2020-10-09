package de.twometer.amongus3d.model.player;

import de.twometer.amongus3d.model.world.TaskDef;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final String username;

    private final Vector3f position;

    private final Role role;

    private final PlayerColor color;

    private final List<TaskDef> remainingTasks = new ArrayList<>();

    public Player(String username, Vector3f position, Role role, PlayerColor color) {
        this.username = username;
        this.position = position;
        this.role = role;
        this.color = color;
    }

    public boolean canDoTask(TaskDef task) {
        return remainingTasks.contains(task);
    }

    public List<TaskDef> getRemainingTasks() {
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
