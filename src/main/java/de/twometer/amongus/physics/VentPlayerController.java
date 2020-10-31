package de.twometer.amongus.physics;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.game.VentGameObject;
import de.twometer.amongus.model.Location;
import de.twometer.amongus.net.NetMessage;
import de.twometer.neko.core.IPlayerController;
import de.twometer.neko.gl.Window;
import de.twometer.neko.render.Camera;
import de.twometer.neko.util.MathF;
import org.joml.Vector3f;

public class VentPlayerController implements IPlayerController {

    private Location vent;

    public VentPlayerController(Location vent) {
        this.vent = vent;
    }

    @Override
    public void update(Window window, Camera camera) {
        camera.getAngle().set(0, -90);
        for (var obj : AmongUs.get().getGameObjects())
            if (obj instanceof VentGameObject) {
                if (((VentGameObject) obj).getLocation() == vent)
                    camera.getPosition().set(obj.getPosition().x, obj.getPosition().y + 4, obj.getPosition().z);
            }

        AmongUs.get().getClient().sendMessage(new NetMessage.PositionChange(new Vector3f(camera.getPosition().x, -500, camera.getPosition().z), MathF.toRadians(camera.getAngle().x)));
    }

    public void setVent(Location vent) {
        this.vent = vent;
    }
}
