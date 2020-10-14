package de.twometer.amongus3d.render;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Camera {

    private static final float BREATHING_STRENGTH = 0.01f;
    private static final float BREATHING_SPEED = 1.44f;

    private static final float EYE_HEIGHT = 0.56f;

    private final Vector3f position = new Vector3f(30, 0, -21);
    //private final Vector3f position = new Vector3f(0,0,0);

    private final Vector2f angle = new Vector2f(0, 0);

    public Matrix4f calcViewMatrix() {
        float yaw = (float) Math.toRadians(angle.x);
        float pitch = (float) Math.toRadians(angle.y);

        Vector3f breathing = new Vector3f(0, BREATHING_STRENGTH * (float) Math.sin(BREATHING_SPEED * glfwGetTime()), 0);
        breathing.add(0, EYE_HEIGHT, 0);
        breathing.add(position);

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

        return new Matrix4f().lookAt(breathing, new Vector3f(breathing).add(direction), up);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector2f getAngle() {
        return angle;
    }


}
