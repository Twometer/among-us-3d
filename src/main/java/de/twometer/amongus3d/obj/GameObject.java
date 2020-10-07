package de.twometer.amongus3d.obj;

public abstract class GameObject {

    private final String name;

    public GameObject(String name) {
        this.name = name;
    }

    public abstract void render();

    public String getName() {
        return name;
    }
}
