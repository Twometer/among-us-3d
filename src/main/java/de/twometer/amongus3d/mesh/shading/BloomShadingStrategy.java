package de.twometer.amongus3d.mesh.shading;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.mesh.Model;
import de.twometer.amongus3d.render.shaders.ShaderFlat;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.List;

public class BloomShadingStrategy implements ShadingStrategy {

    private static final List<String> GLOWING_MATERIALS = Arrays.asList("_Color_H04_1.002", "textura_mapa.002", "Color_F06.002", "Color_H01.002", "monitor_3.002", "monitor_1.002", "monitor_2.002", "Color_E03.002", "Color_H04.002", "");

    @Override
    public void configureShaders(Model model, Matrix4f modelMatrix) {
        boolean isBloom = GLOWING_MATERIALS.contains(model.getMaterial().getName());

        Game game = Game.instance();
        ShaderFlat shader = game.getShaderProvider().getShader(ShaderFlat.class);
        shader.bind();
        shader.setProjMatrix(game.getProjMatrix());
        shader.setViewMatrix(game.getViewMatrix());
        shader.setVertexColor(isBloom ? model.getMaterial().getDiffuseColor() : new Vector3f(0, 0, 0));
        shader.setCameraPos(game.getCamera().getPosition());
        shader.setVision(game.getVision());
        shader.setModelMatrix(modelMatrix);
    }

}
