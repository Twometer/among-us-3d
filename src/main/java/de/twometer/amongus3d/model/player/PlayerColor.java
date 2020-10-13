package de.twometer.amongus3d.model.player;

import org.joml.Vector3f;

public enum PlayerColor {
    Purple(0.6f,0,1),
    Cyan(0, 1, 1),
    Red(1,0,0),
    Black(0.1f,0.1f,0.1f),
    Yellow(1,1,0),
    Lime(0,1,0),
    Green(0,0.6f,0),
    Orange(1,0.7f,0),
    White(1,1,1),
    Blue(0,0,1),
    Brown(0.5f,0.3f,0f),
    Pink(1,0,1);

    private final Vector3f color;

    PlayerColor(float r, float g, float b) {
        this.color = new Vector3f(r, g, b);
    }

    public Vector3f toVector() {
        return color;
    }
}
