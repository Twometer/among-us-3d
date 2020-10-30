package de.twometer.amongus.game;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.model.Player;
import de.twometer.neko.render.model.ModelBase;
import de.twometer.neko.render.model.ModelInstance;
import de.twometer.neko.res.ModelLoader;
import org.joml.Vector3f;

public class PlayerGameObject extends GameObject {

    private static ModelBase baseModel;

    private final Player trackedPlayer;

    public PlayerGameObject(Player trackedPlayer) {
        super(newModelInstance());
        getModel().setCascadeTransforms(true);
        this.trackedPlayer = trackedPlayer;
    }

    private void setPose(Vector3f position, float rotation) {
        if (position == null) position = new Vector3f();

        var transform = getModel().getTransform();
        transform.setScale(new Vector3f(0.25f, 0.25f, 0.25f));
        transform.setTranslation(position);
        transform.setRotationOrigin(getModel().getCenter());
        transform.setRotation(new Vector3f(0, rotation, 0));
    }

    @Override
    public void onUpdate() {
        setPose(trackedPlayer.position, trackedPlayer.rotation);
    }

    @Override
    public void onAdded() {
        AmongUs.get().getScene().addModel(getModel());
    }

    @Override
    public void onRemoved() {
        //AmongUs.get().getScene().removeModel(baseModel);
    }

    @Override
    public boolean canInteract() {
        return false;
    }

    private static ModelBase newModelInstance() {
        if (baseModel == null)
            baseModel = ModelLoader.loadModel("Astronaut.obj");
        return new ModelInstance(baseModel);
    }

    public Player getTrackedPlayer() {
        return trackedPlayer;
    }
}
