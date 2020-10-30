package de.twometer.amongus.physics;

public class GhostPlayerController extends BasePlayerController {

    public GhostPlayerController() {
        super(0.2f);
    }

    @Override
    boolean mayFly() {
        return true;
    }

}
