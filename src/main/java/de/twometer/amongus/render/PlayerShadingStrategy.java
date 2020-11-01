package de.twometer.amongus.render;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.model.Player;
import de.twometer.amongus.model.PlayerRole;
import de.twometer.amongus.model.Sabotage;
import de.twometer.amongus.model.SessionConfig;
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
        shader.visionRadius.set(getVisionRadius());
        return true;
    }

    private float getVisionRadius() {
        var session = AmongUs.get().getSession();
        var me = session.getMyself();
        if (!me.alive)
            return 100.0f;
        else if (session.currentSabotage == Sabotage.Lights)
            return 1.5f;
        else if (me.role == PlayerRole.Impostor)
            return session.getConfig().getImpostorVision() * SessionConfig.PLAYER_VISION_BASE_RADIUS;
        else
            return session.getConfig().getPlayerVision() * SessionConfig.PLAYER_VISION_BASE_RADIUS;
    }

}
