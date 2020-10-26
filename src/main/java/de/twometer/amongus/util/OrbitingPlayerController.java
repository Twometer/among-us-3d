package de.twometer.amongus.util;

import de.twometer.neko.core.IPlayerController;
import de.twometer.neko.gl.Window;
import de.twometer.neko.render.Camera;
import de.twometer.neko.util.MathF;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class OrbitingPlayerController implements IPlayerController {

    private final Vector3f center = new Vector3f(22.5f, 0.75f, -16.2f);

    @Override
    public void update(Window window, Camera camera) {
        var time = (float) glfwGetTime() * 0.01f;
        var x = MathF.cos(time) * 40;
        var z = MathF.sin(time) * 40;
        camera.getPosition().x = x + center.x;
        camera.getPosition().y = 5.0f + center.y;
        camera.getPosition().z = z + center.z;

        var dirVec = new Vector3f(center.x - camera.getPosition().x, 0, center.z - camera.getPosition().z);
        dirVec = dirVec.normalize();
        var yaw = MathF.toDegrees(MathF.atan2(dirVec.x, dirVec.z));
        if (yaw < 0) yaw += 360;
        camera.getAngle().set(yaw, -20f);
    }

}
