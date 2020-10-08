package de.twometer.amongus3d.mesh.shading;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.mesh.Material;
import de.twometer.amongus3d.mesh.Model;
import de.twometer.amongus3d.render.shaders.ShaderSimple;
import de.twometer.amongus3d.render.shaders.ShaderTextured;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL13.glActiveTexture;

public class DefaultShadingStrategy implements ShadingStrategy {

    @Override
    public void configureShaders(Model model, Matrix4f modelMatrix) {
        ShaderSimple shader;
        Game game = Game.instance();

        Material material = model.getMaterial();

        if (material == null || material.getTexture().length() == 0) {
            shader = game.getShaderProvider().getShader(ShaderSimple.class);
            if (material != null)
                shader.setVertexColor(material.getDiffuseColor());
        } else {
            glActiveTexture(0);
            game.getTextureProvider().getTexture(material.getTexture()).bind();
            ShaderTextured tex = Game.instance().getShaderProvider().getShader(ShaderTextured.class);
            tex.setTexSampler(0);
            tex.setVertexColor(material.getDiffuseColor());
            shader = tex;
        }

        shader.bind();
        shader.setProjMatrix(game.getProjMatrix());
        shader.setViewMatrix(game.getViewMatrix());
        shader.setModelMatrix(modelMatrix);
    }

}
