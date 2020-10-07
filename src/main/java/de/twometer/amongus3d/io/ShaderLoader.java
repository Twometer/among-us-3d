package de.twometer.amongus3d.io;

import de.twometer.amongus3d.util.Log;

import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;

public class ShaderLoader {

    public static int loadShader(String vertPath, String fragPath) {
        try {
            Log.i("Loading shader " + vertPath + " | " + fragPath);

            int vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
            int fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);

            String vertexShaderCode = ResourceLoader.loadString(vertPath);
            String fragmentShaderCode = ResourceLoader.loadString(fragPath);

            glShaderSource(vertexShaderId, vertexShaderCode);
            glCompileShader(vertexShaderId);
            checkShaderError(vertexShaderId);

            glShaderSource(fragmentShaderId, fragmentShaderCode);
            glCompileShader(fragmentShaderId);
            checkShaderError(fragmentShaderId);

            int programId = glCreateProgram();
            glAttachShader(programId, vertexShaderId);
            glAttachShader(programId, fragmentShaderId);
            glLinkProgram(programId);

            // After the shader program is linked, the shader sources can be cleaned up
            glDetachShader(programId, vertexShaderId);
            glDetachShader(programId, fragmentShaderId);
            glDeleteShader(vertexShaderId);
            glDeleteShader(fragmentShaderId);

            return programId;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void checkShaderError(int shaderId) {
        String log = glGetShaderInfoLog(shaderId);
        if (log.length() > 0)
            Log.e(log);
    }

}
