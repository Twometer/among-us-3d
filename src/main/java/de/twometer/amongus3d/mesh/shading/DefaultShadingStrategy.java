package de.twometer.amongus3d.mesh.shading;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.mesh.Material;
import de.twometer.amongus3d.mesh.Model;
import de.twometer.amongus3d.render.shaders.ShaderFlat;
import de.twometer.amongus3d.render.shaders.ShaderTextured;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL13.glActiveTexture;

public class DefaultShadingStrategy implements ShadingStrategy {

    @Override
    public void configureShaders(Model model, Matrix4f modelMatrix) {
        ShaderFlat shader;
        Game game = Game.instance();

        Material material = model.getMaterial();

        if (material == null || material.getTexture().length() == 0) {
            shader = game.getShaderProvider().getShader(ShaderFlat.class);
            if (material != null)
                shader.setVertexColor(material.getDiffuseColor());
            else
                shader.setVertexColor(new Vector3f(1, 1, 1));
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
        shader.setCameraPos(game.getCamera().getPosition());
        shader.setVision(game.getVision());
    }

}
