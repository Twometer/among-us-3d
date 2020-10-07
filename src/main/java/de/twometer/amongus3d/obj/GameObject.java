package de.twometer.amongus3d.obj;

public abstract class GameObject {

    private static int idCounter = 0;

    private final int id;

    private final String name;

    public GameObject(String name) {
        this.id = newId();
        this.name = name;
    }

    public void init() {

    }

    public abstract void render();

    public abstract boolean canPlayerInteract();

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private static int newId() {
        return idCounter++;
    }

}
