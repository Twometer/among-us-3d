package de.twometer.amongus3d.mesh.shading;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.mesh.Model;
import de.twometer.amongus3d.render.shaders.ShaderSimple;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class HighlightShadingStrategy implements ShadingStrategy {

    private Vector3f highlightColor = new Vector3f(0, 1, 0);

    @Override
    public void configureShaders(Model model, Matrix4f modelMatrix) {
        Game game = Game.instance();
        ShaderSimple shader = game.getShaderProvider().getShader(ShaderSimple.class);
        shader.bind();
        shader.setProjMatrix(game.getProjMatrix());
        shader.setViewMatrix(game.getViewMatrix());
        shader.setVertexColor(highlightColor);
        shader.setModelMatrix(modelMatrix);
    }

    public void setHighlightColor(Vector3f highlightColor) {
        this.highlightColor = highlightColor;
    }
}
