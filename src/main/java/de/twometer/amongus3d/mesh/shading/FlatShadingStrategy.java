package de.twometer.amongus3d.mesh.shading;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.mesh.Model;
import de.twometer.amongus3d.render.shaders.ShaderFlat;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class FlatShadingStrategy implements ShadingStrategy {

    private Vector3f color = new Vector3f(0, 1, 0);

    @Override
    public void configureShaders(Model model, Matrix4f modelMatrix) {
        Game game = Game.instance();
        ShaderFlat shader = game.getShaderProvider().getShader(ShaderFlat.class);
        shader.bind();
        shader.setProjMatrix(game.getProjMatrix());
        shader.setViewMatrix(game.getViewMatrix());
        shader.setVertexColor(color);
        shader.setModelMatrix(modelMatrix);
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }
}
