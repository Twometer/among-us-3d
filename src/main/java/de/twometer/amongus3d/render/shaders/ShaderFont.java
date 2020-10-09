package de.twometer.amongus3d.render.shaders;

import de.twometer.amongus3d.render.Shader;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class ShaderFont extends Shader {

    private int projectionMatrix;
    private int transformationMatrix;

    private int color;

    public ShaderFont() {
        super("shaders/font.v.glsl", "shaders/font.f.glsl");
    }

    @Override
    public void bindUniforms() {
        this.projectionMatrix = getLocation("projectionMatrix");
        this.transformationMatrix = getLocation("transformationMatrix");
        this.color = getLocation("color");
    }

    public void setProjectionMatrix(Matrix4f matrix) {
        setMatrix(projectionMatrix, matrix);
    }

    public void setTransformationMatrix(Matrix4f matrix) {
        setMatrix(transformationMatrix, matrix);
    }

    public void setColor(float r, float g, float b, float a) {
        setVector4(color, new Vector4f(r, g, b, a));
    }
}
