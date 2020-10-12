package de.twometer.amongus3d.postproc;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

import static de.twometer.amongus3d.util.MathUtil.lerp;
import static org.lwjgl.opengl.GL11.*;

public class SSAO {

    private static float rand() {
        return (float) Math.random();
    }


    public static Vector3f[] createSampleKernel(int size) {
        Vector3f[] kernel = new Vector3f[size];
        for (int i = 0; i < size; i++) {
            float scale = i / (float) size;
            scale = lerp(0.1f, 1.0f, scale * scale);

            kernel[i] = new Vector3f(
                    rand() * 2 - 1,
                    rand() * 2 - 1,
                    rand()
            ).normalize(scale);
        }
        return kernel;
    }

    public static int createNoiseTexture(int w, int h) {
        ByteBuffer buffer = BufferUtils.createByteBuffer(w * h * 3);
        for (int i = 0; i < w * h; i++) {
            buffer.put((byte) (255 * Math.random()));
            buffer.put((byte) (255 * Math.random()));
            buffer.put((byte) 0);
        }

        buffer.flip();

        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w, h, 0, GL_RGB, GL_UNSIGNED_BYTE, buffer);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);


        return textureId;
    }

}
