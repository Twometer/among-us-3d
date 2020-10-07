package de.twometer.amongus3d.mesh;

import java.util.ArrayList;
import java.util.List;

public class CompositeModel implements IRenderable {

    private final List<Model> models;

    public CompositeModel(List<Model> models) {
        this.models = new ArrayList<>(models);
    }

    @Override
    public void render() {
        for (Model model : models)
            model.render();
    }

}
