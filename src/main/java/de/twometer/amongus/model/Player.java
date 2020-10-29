package de.twometer.amongus.model;

import org.joml.Vector3f;

import java.util.List;

public class Player {

    public int id;

    public String username;

    public Vector3f position;

    public float location;

    public PlayerRole role;

    public PlayerColor color;

    public List<PlayerTask> tasks;

}
