package de.twometer.amongus3d.render.shaders;

import de.twometer.amongus3d.io.GLLoader;

public abstract class Shader {

    protected final int id;

    public Shader(String vertex, String fragment) {
        id = GLLoader.loadShader(vertex, fragment);
        bindUniforms();
    }

    public abstract void bindUniforms();

}
