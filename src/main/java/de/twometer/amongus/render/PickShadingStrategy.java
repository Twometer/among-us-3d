package de.twometer.amongus.render;

import de.twometer.amongus.core.AmongUs;
import de.twometer.neko.render.Color;
import de.twometer.neko.render.model.ModelPart;
import de.twometer.neko.render.shading.IShadingStrategy;
import de.twometer.neko.res.cache.ShaderProvider;
import de.twometer.neko.res.cache.TextureProvider;

public class PickShadingStrategy implements IShadingStrategy {

    public boolean canInteract;

    @Override
    public boolean prepareRender(ModelPart modelPart, ShaderProvider shaderProvider, TextureProvider textureProvider) {
        var radius = modelPart.getSize().length() / 2;
        var distance = Math.abs(modelPart.getCenter().distance(AmongUs.get().getCamera().getInterpolatedPosition(AmongUs.get().getTimer().getPartial())) - radius);
        if (distance > 2)
            return false;

        var tag = modelPart.getTag();
        var id = canInteract ? (int) tag : 0;
        var shader = shaderProvider.getShader(UnshadedShader.class);
        shader.bind();
        var id_f = id / 255.0f;
        shader.color.set(new Color(id_f, 0, 0));
        shader.modelMatrix.set(modelPart.getTransform().getMatrix());
        return true;
    }

    @Override
    public boolean mayOverwrite() {
        return false;
    }

}
