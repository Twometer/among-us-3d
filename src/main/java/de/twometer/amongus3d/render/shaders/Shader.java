package de.twometer.amongus3d.render.shaders;

import de.twometer.amongus3d.io.ShaderLoader;
import org.joml.*;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public abstract class Shader {

    protected final int id;

    public Shader(String vertex, String fragment) {
        id = ShaderLoader.loadShader(vertex, fragment);

        bind();
        bindUniforms();
        unbind();
    }

    public abstract void bindUniforms();

    public final void bind() {
        glUseProgram(id);
    }

    public final void unbind() {
        glUseProgram(0);
    }

    protected final int getLocation(String name) {
        return glGetUniformLocation(id, name);
    }

    protected final void setMatrix(int location, Matrix4f mat) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        mat.get(buffer);
        glUniformMatrix4fv(location, false, buffer);
    }

    protected final void setVector4(int location, Vector4f vec) {
        glUniform4f(location, vec.x, vec.y, vec.z, vec.w);
    }

    protected final void setVector3(int location, Vector3f vec) {
        glUniform3f(location, vec.x, vec.y, vec.z);
    }

    protected final void setVector2(int location, Vector2f vec) {
        glUniform2f(location, vec.x, vec.y);
    }

    protected final void setFloat(int location, float f) {
        glUniform1f(location, f);
    }

}
