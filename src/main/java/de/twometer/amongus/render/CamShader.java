package de.twometer.amongus.render;

import de.twometer.neko.gl.Shader;
import de.twometer.neko.gl.Uniform;

public class CamShader extends Shader {

    public Uniform<Float> time;

    public CamShader() {
        super("PostVert.glsl", "PostCam.glsl");
    }

}
