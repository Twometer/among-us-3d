package de.twometer.amongus.physics;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.model.SessionConfig;
import de.twometer.amongus.net.NetMessage;
import de.twometer.neko.gl.Window;
import de.twometer.neko.render.Camera;
import de.twometer.neko.util.MathF;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class CollidingPlayerController extends BasePlayerController {

    private float time = 0.0f;
    private Vector3f baseOffset;
    private final Collider collider;

    public CollidingPlayerController() {
        collider = ColliderLoader.load();
    }

    private float prevBob;

    @Override
    public void update(Window window, Camera camera) {
        var oldpos = new Vector3f().set(camera.getPosition());
        super.update(window, camera);
        collider.updatePosition(camera.getPosition());

        AmongUs.get().getClient().sendMessage(new NetMessage.PositionChange(camera.getPosition(), MathF.toRadians(camera.getAngle().x)));

        var diff = camera.getPosition().distanceSquared(oldpos);
        if (diff > 0.005) time += 0.1f;

        if (baseOffset == null)
            baseOffset = new Vector3f().set(camera.getOffset());

        var yaw = MathF.toRadians(camera.getAngle().x);
        var bobof = MathF.sin(time * 2) * 0.05f;
        var rightVec = new Vector3f(MathF.sin(yaw - MathF.PI / 2f), 0.0F, MathF.cos(yaw - MathF.PI / 2f))
                .normalize(bobof);
        var ubob  =MathF.sin(time * 2 + 1.5f) * 0.05f;
        var bob = Math.abs(ubob);
        if ((prevBob < 0 && ubob >= 0) || (prevBob >= 0 && ubob < 0))
            playFootstep();
        prevBob = ubob;


        camera.getOffset().set(0, bob, 0).add(baseOffset).add(rightVec);
    }

    @Override
    public float getSpeed() {
        if (!AmongUs.get().getStateController().isRunning()) return 0.0f;
        return AmongUs.get().getSession().getConfig().getPlayerSpeed() * SessionConfig.PLAYER_VISION_BASE_SPEED;
    }

    private void playFootstep() {
        var rand = (int) (MathF.rand() * 8) + 1;
        AmongUs.get().getSoundFX().play("Footsteps/Metal" + rand + ".ogg");
    }
}
