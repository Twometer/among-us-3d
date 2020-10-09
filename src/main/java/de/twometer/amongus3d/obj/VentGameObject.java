package de.twometer.amongus3d.obj;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.mesh.Renderable;
import de.twometer.amongus3d.model.Role;
import de.twometer.amongus3d.model.Room;
import de.twometer.amongus3d.render.RenderLayer;
import de.twometer.amongus3d.util.Log;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class VentGameObject extends StaticGameObject {

    private final Room location;

    private final int ventIdx;

    private boolean open = false;

    public VentGameObject(String name, Renderable model, Room location, int ventIdx) {
        super(name, model);
        this.location = location;
        this.ventIdx = ventIdx;
    }

    @Override
    public String toString() {
        return String.format("VENT.%s.%d", location, ventIdx);
    }

    @Override
    public boolean canPlayerInteract() {
        return Game.instance().getSelf().getRole() == Role.Impostor;
    }

    @Override
    public void render(RenderLayer layer) {
        if (isOpen() && layer == RenderLayer.Base) {
            Matrix4f mat = new Matrix4f();
            mat.translate(0,0,0.35f);
            model.render(mat);
        } else super.render(layer);
    }

    @Override
    public void onClicked() {
        super.onClicked();
        Log.d("Clicked on " + toString());
        setOpen(true);
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
