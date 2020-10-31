package de.twometer.amongus.physics;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.net.NetMessage;
import de.twometer.neko.gl.Window;
import de.twometer.neko.render.Camera;
import de.twometer.neko.util.MathF;

public class CollidingPlayerController extends BasePlayerController {

    private final Collider collider;

    public CollidingPlayerController() {
        super(0.125f);
        collider = ColliderLoader.load();
    }

    @Override
    public void update(Window window, Camera camera) {
        super.update(window, camera);
        collider.updatePosition(camera.getPosition());

        AmongUs.get().getClient().sendMessage(new NetMessage.PositionChange(camera.getPosition(), MathF.toRadians(camera.getAngle().x)));
    }
}
