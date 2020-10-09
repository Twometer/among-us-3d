package de.twometer.amongus3d.mesh.shading;

import de.twometer.amongus3d.mesh.Model;
import org.joml.Matrix4f;

public class NopShadingStrategy implements ShadingStrategy {

    @Override
    public void configureShaders(Model model, Matrix4f modelMatrix) {

    }

}
