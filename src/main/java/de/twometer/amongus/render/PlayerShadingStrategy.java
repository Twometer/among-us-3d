package de.twometer.amongus.render;

import de.twometer.amongus.model.Player;
import de.twometer.neko.render.model.ModelPart;
import de.twometer.neko.render.shading.AbstractGeometryShadingStrategy;
import de.twometer.neko.res.cache.ShaderProvider;
import de.twometer.neko.res.cache.TextureProvider;

public class PlayerShadingStrategy extends AbstractGeometryShadingStrategy {

    private final Player player;

    public PlayerShadingStrategy(Player player) {
        this.player = player;
    }

    @Override
    public boolean prepareRender(ModelPart part, ShaderProvider shaders, TextureProvider textures) {
        var mat = part.getMaterial();

        var shader = shaders.getShader(PlayerShader.class);
        shader.bind();
        shader.modelColor.set(mat.getDiffuseColor());
        shader.modelMatrix.set(part.getTransform().getMatrix());
        shader.hasTexture.set(mat.hasTexture());
        shader.playerColor.set(player.color.getValue());
        return true;
    }

}
