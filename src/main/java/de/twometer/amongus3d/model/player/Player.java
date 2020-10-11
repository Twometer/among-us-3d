package de.twometer.amongus3d.model.player;

import de.twometer.amongus3d.model.world.TaskDef;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final String username;

    private Vector3f position;

    private Role role;

    private PlayerColor color;

    private List<PlayerTask> tasks = new ArrayList<>();

    public Player(String username) {
        this.username = username;
    }

    public boolean canDoTask(TaskDef task) {
        for (PlayerTask t2 : tasks)
            if (t2.nextTask().equals(task))
                return true;
        return false;
    }

    public List<PlayerTask> getTasks() {
        return tasks;
    }

    public String getUsername() {
        return username;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public PlayerColor getColor() {
        return color;
    }

    public void setColor(PlayerColor color) {
        this.color = color;
    }

    public void setTasks(List<PlayerTask> tasks) {
        this.tasks = tasks;
    }
}
