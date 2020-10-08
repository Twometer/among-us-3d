package de.twometer.amongus3d.obj;

import de.twometer.amongus3d.render.RenderLayer;
import org.joml.Vector3f;

public abstract class GameObject {

    private static int idCounter = 0;

    private final int id;

    private final String name;

    private boolean selected;

    public GameObject(String name) {
        this.id = newId();
        this.name = name;
    }

    public void init() {

    }

    public abstract void render(RenderLayer layer);

    public abstract boolean canPlayerInteract();

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private static int newId() {
        return ++idCounter;
    }

    public abstract Vector3f getPosition();

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void onClicked() {

    }
}
