package de.twometer.amongus3d.render;

import de.twometer.amongus3d.render.Shader;

import java.util.HashMap;
import java.util.Map;

public class ShaderProvider {

    private final Map<Class<? extends Shader>, Shader> cache = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Shader> T getShader(Class<T> shaderClass) {
        Shader shader = cache.get(shaderClass);
        if (shader == null) {
            try {
                shader = shaderClass.newInstance();
                cache.put(shaderClass, shader);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return (T) shader;
    }

}
