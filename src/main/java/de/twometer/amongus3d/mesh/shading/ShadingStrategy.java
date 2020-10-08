package de.twometer.amongus3d.mesh.shading;

import de.twometer.amongus3d.mesh.Model;
import org.joml.Matrix4f;

public interface ShadingStrategy {

    void configureShaders(Model model, Matrix4f modelMatrix);

}
