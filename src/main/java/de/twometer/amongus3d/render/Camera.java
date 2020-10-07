package de.twometer.amongus3d.render;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {

    private final Vector3f position = new Vector3f(1, 0, 0);

    private final Vector2f angle = new Vector2f(0, 0);

    public Matrix4f calcViewMatrix() {
        float yaw = (float) Math.toRadians(angle.x);
        float pitch = (float) Math.toRadians(angle.y);

        Vector3f direction = new Vector3f(
                (float) Math.cos(pitch) * (float) Math.sin(yaw),
                (float) Math.sin(pitch),
                (float) Math.cos(pitch) * (float) Math.cos(yaw)
        );
        Vector3f right = new Vector3f(
                (float) Math.sin(yaw - Math.PI / 2.0f),
                0,
                (float) Math.cos(yaw - Math.PI / 2.0f)
        );
        Vector3f up = new Vector3f(right).cross(direction);

        return new Matrix4f().lookAt(position, new Vector3f(position).add(direction), up);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector2f getAngle() {
        return angle;
    }


}
