package de.twometer.amongus.render;

import de.twometer.neko.render.Color;
import de.twometer.neko.render.model.ModelPart;
import de.twometer.neko.render.shading.IShadingStrategy;
import de.twometer.neko.res.cache.ShaderProvider;
import de.twometer.neko.res.cache.TextureProvider;

public class UnshadedShadingStrategy implements IShadingStrategy {

    public Color color;

    @Override
    public boolean prepareRender(ModelPart modelPart, ShaderProvider shaderProvider, TextureProvider textureProvider) {
        var shader = shaderProvider.getShader(UnshadedShader.class);
        shader.bind();
        shader.color.set(color);
        shader.modelMatrix.set(modelPart.getTransform().getMatrix());
        return true;
    }

    @Override
    public boolean mayOverwrite() {
        return false;
    }

}
