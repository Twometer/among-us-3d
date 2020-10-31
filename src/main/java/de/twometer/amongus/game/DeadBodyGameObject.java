package de.twometer.amongus.game;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.model.Player;
import de.twometer.amongus.model.PlayerRole;
import de.twometer.amongus.net.NetMessage;
import de.twometer.amongus.render.PlayerShadingStrategy;
import de.twometer.neko.render.Color;
import de.twometer.neko.render.model.ModelBase;
import de.twometer.neko.render.model.ModelInstance;
import de.twometer.neko.res.ModelLoader;
import de.twometer.neko.util.MathF;
import org.joml.Vector3f;

public class DeadBodyGameObject extends GameObject {

    private static ModelBase baseModel;

    private final Vector3f deathLocation;

    private final PlayerShadingStrategy shadingStrategy;

    public DeadBodyGameObject(Player deadPlayer, Vector3f deathLocation) {
        super(newModelInstance());
        shadingStrategy = new PlayerShadingStrategy(deadPlayer);
        getModel().setCascadeTransforms(true);
        getModel().overwriteShadingStrategy(shadingStrategy);
        this.deathLocation = deathLocation;
        setPose(deathLocation, MathF.rand());
    }

    private void setPose(Vector3f position, float rotation) {
        if (position == null) position = new Vector3f();

        var transform = getModel().getTransform();
        transform.setScale(new Vector3f(0.25f, 0.25f, 0.25f));
        transform.setTranslation(new Vector3f(position.x, position.y - 0.05f, position.z));
        transform.setRotationOrigin(getModel().getCenter());
        transform.setRotation(new Vector3f(0, rotation, 0));
    }

    @Override
    public void onAdded() {
        AmongUs.get().getScene().addModel(getModel());
    }

    @Override
    public void onRemoved() {
        AmongUs.get().getScene().removeModel(getModel());
    }

    @Override
    public boolean canInteract() {
        return AmongUs.get().getSession().getMyself().alive;
    }

    @Override
    public void onClick() {
        super.onClick();
        AmongUs.get().getClient().sendMessage(new NetMessage.CallEmergency(NetMessage.EmergencyCause.DeadBody));
    }

    @Override
    public Color getHighlightColor() {
        return new Color(1,1,0,0.75f);
    }

    private static ModelBase newModelInstance() {
        if (baseModel == null)
            baseModel = ModelLoader.loadModel("DeadBody.obj");
        return new ModelInstance(baseModel);
    }

    @Override
    public Vector3f getPosition() {
        return deathLocation;
    }

    @Override
    public float getRadius() {
        return super.getRadius() * 0.25f;
    }
}
