package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.model.Location;
import de.twometer.amongus.model.VentConnections;
import de.twometer.amongus.physics.CollidingPlayerController;
import de.twometer.amongus.physics.VentPlayerController;
import org.joml.Vector2f;
import org.joml.Vector3f;

import javax.lang.model.element.AnnotationMirror;

public class VentPage extends BasePage {

    private final Location startingPoint;
    private final Location[] ends;

    private Location currentLocation;
    private Vector3f oldCamPos = new Vector3f();
    private Vector2f oldCamAng = new Vector2f();

    private VentPlayerController playerController;

    public VentPage(Location startingPoint) {
        super("Vent.html");
        this.startingPoint = startingPoint;
        ends = VentConnections.getVentEnds(startingPoint);
        oldCamPos.set(AmongUs.get().getCamera().getPosition());
        oldCamAng.set(AmongUs.get().getCamera().getAngle());
        playerController = new VentPlayerController(startingPoint);
        AmongUs.get().setPlayerController(playerController);
    }

    @Override
    public void onDomReady() {
        super.onDomReady();
        for (var end : ends) {
            context.call("addEnd", end.name());
        }
        AmongUs.get().getSoundFX().play("Vent.ogg");
    }

    public void exitVentMode() {
        goBack();
    }

    private void crawlOut() {
        AmongUs.get().setPlayerController(new CollidingPlayerController());
        if (currentLocation == startingPoint) {
            AmongUs.get().getCamera().getPosition().set(oldCamPos);
            AmongUs.get().getCamera().getAngle().set(oldCamAng);
        } else {
            AmongUs.get().getCamera().getPosition().y = 0;
        }
        AmongUs.get().getSoundFX().play("Vent.ogg");
    }

    public void crawlTo(String loc) {
        var location = Location.valueOf(loc);
        currentLocation = location;
        playerController.setVent(location);
    }

    @Override
    protected void goBack() {
        super.goBack();
        crawlOut();
    }

    @Override
    protected boolean escapeGoesBack() {
        return true;
    }
}
