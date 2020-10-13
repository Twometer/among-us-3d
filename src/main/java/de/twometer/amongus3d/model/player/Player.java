package de.twometer.amongus3d.model.player;

import de.twometer.amongus3d.model.world.TaskDef;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String username;


    private float networkRotation = 0;
    private float nextTickRotation = 0;
    private float lastTickRotation = 0;

    private final Vector3f networkPosition = new Vector3f(0, 0, 0);
    private final Vector3f nextTickPosition = new Vector3f(0, 0, 0);
    private final Vector3f lastTickPosition = new Vector3f(0, 0, 0);

    private Role role = Role.Crewmate;

    private PlayerColor color = PlayerColor.Red;

    private List<PlayerTask> tasks = new ArrayList<>();

    private int ejectionVotes = 0;

    private boolean isDead = false;

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

    public Player setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getUsername() {
        return username;
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

    public int getEjectionVotes() {
        return ejectionVotes;
    }

    public void vote() {
        ejectionVotes++;
    }

    public void resetVotes() {
        ejectionVotes = 0;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public void setPosition(Vector3f v) {
        networkPosition.set(v);
    }

    public Vector3f getNextTickPosition() {
        return nextTickPosition;
    }

    public Vector3f getLastTickPosition() {
        return lastTickPosition;
    }

    public Vector3f getPosition() {
        return networkPosition;
    }

    public float getRotation() {
        return networkRotation;
    }

    public void setRotation(float rot) {
        networkRotation = rot;
    }

    public float getNextTickRotation() {
        return nextTickRotation;
    }

    public float getLastTickRotation() {
        return lastTickRotation;
    }

    public void updatePosition() {
        lastTickPosition.set(nextTickPosition);
        nextTickPosition.set(networkPosition);

        lastTickRotation = nextTickRotation;
        nextTickRotation = networkRotation;
    }
}
