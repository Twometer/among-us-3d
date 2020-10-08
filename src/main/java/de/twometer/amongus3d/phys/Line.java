package de.twometer.amongus3d.phys;

import org.joml.Vector3f;

public class Line {

    private Vector3f a;

    private Vector3f b;

    public Line(Vector3f a, Vector3f b) {
        this.a = a;
        this.b = b;
    }

    public Vector3f getA() {
        return a;
    }

    public Vector3f getB() {
        return b;
    }
}
