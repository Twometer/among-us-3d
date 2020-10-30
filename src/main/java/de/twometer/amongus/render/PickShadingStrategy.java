package de.twometer.amongus.render;

import de.twometer.amongus.core.AmongUs;
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
        var shader = shaderProvider.getShader(PickShader.class);
        shader.bind();
        shader.modelId.set(id);
        shader.modelMatrix.set(modelPart.getTransform().getMatrix());
        return true;
    }

    @Override
    public boolean mayOverwrite() {
        return false;
    }

}
