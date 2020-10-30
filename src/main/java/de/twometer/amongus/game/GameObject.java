package de.twometer.amongus.game;

import de.twometer.neko.render.model.ModelBase;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class GameObject {

    private static final AtomicInteger idCounter = new AtomicInteger(0);

    private final int id;

    private final ModelBase model;

    public GameObject(ModelBase model) {
        this.id = idCounter.incrementAndGet();
        this.model = model;
    }

    public int getId() {
        return id;
    }

    public ModelBase getModel() {
        return model;
    }

    public abstract boolean canInteract();

    public void onUpdate() {
    }

    public void onClick() {
    }

}
