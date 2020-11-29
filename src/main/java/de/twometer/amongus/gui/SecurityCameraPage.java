package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.physics.NopPlayerController;
import de.twometer.neko.core.IPlayerController;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class SecurityCameraPage extends BasePage {

    private final Vector3f prevPosition = new Vector3f();
    private final Vector2f prevRotation = new Vector2f();
    private IPlayerController prevController;

    private final CamLocation[] locations = {
            new CamLocation(10.807461f, 1.25f, -14.47564f, 198.48026f, -62.72f),
            new CamLocation(20.94838f, 0.5f, -21.486969f, 246.80066f, -37.999996f),
            new CamLocation(31.332027f, 0.375f, -12.794603f, 241.0806f, -32.360012f),
            new CamLocation(38.80089f, 0.125f, -11.5932045f, 158.8406f, -17.400011f)
    };
    private int locationIndex;

    public SecurityCameraPage() {
        super("SecurityCamera.html");
    }

    @Override
    public void onDomReady() {
        super.onDomReady();

        prevController = AmongUs.get().getPlayerController();

        prevPosition.set(AmongUs.get().getCamera().getPosition());
        prevRotation.set(AmongUs.get().getCamera().getAngle());

        AmongUs.get().setPlayerController(new NopPlayerController());
        tpToCam();
    }

    @Override
    public void onUnload() {
        super.onUnload();

        AmongUs.get().getCamera().getPosition().set(prevPosition);
        AmongUs.get().getCamera().getAngle().set(prevRotation);

        AmongUs.get().setPlayerController(prevController);
    }

    @Override
    protected boolean escapeGoesBack() {
        return true;
    }

    public void next() {
        locationIndex++;
        if (locationIndex >= locations.length) locationIndex = 0;
        tpToCam();
    }

    public void prev() {
        locationIndex--;
        if (locationIndex < 0) locationIndex = locations.length - 1;
        tpToCam();
    }

    private void tpToCam() {
        locations[locationIndex].apply();
    }

    private static class CamLocation {
        private final Vector3f position;
        private final Vector2f rotation;

        public CamLocation(float x, float y, float z, float xRot, float yRot) {
            this.position = new Vector3f(x, y, z);
            this.rotation = new Vector2f(xRot, yRot);
        }

        public void apply() {
            AmongUs.get().getCamera().getPosition().set(position);
            AmongUs.get().getCamera().getAngle().set(rotation);
        }
    }

}
