package de.twometer.amongus3d.mesh.shading;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.mesh.Model;
import de.twometer.amongus3d.render.shaders.ShaderPick;
import org.joml.Matrix4f;

public class PickShadingStrategy implements ShadingStrategy {

    @Override
    public void configureShaders(Model model, Matrix4f modelMatrix) {
        ShaderPick shader = Game.instance().getShaderProvider().getShader(ShaderPick.class);
        shader.bind();
        shader.setModelId(model.getModelId());
        shader.setModelMatrix(modelMatrix);
        shader.setViewMatrix(Game.instance().getViewMatrix());
        shader.setProjMatrix(Game.instance().getProjMatrix());
    }

}
