package de.twometer.amongus.physics;

import org.joml.Vector3f;

public class LineSegment {

    private final Vector3f a;

    private final Vector3f b;

    public LineSegment(Vector3f a, Vector3f b) {
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
