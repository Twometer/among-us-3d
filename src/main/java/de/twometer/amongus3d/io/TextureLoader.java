package de.twometer.amongus3d.io;

import de.twometer.amongus3d.render.Texture;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class TextureLoader {

    public static Texture loadTexture(String path) {
        try {
            BufferedImage image = ResourceLoader.loadImage(path);
            ByteBuffer buffer = loadPixels(image);

            int textureId = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureId);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glGenerateMipmap(GL_TEXTURE_2D);

            glBindTexture(GL_TEXTURE_2D, 0);

            return new Texture(textureId, image.getWidth(), image.getHeight());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ByteBuffer loadPixels(BufferedImage image) {
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y), true);
                buffer.put((byte) color.getRed());
                buffer.put((byte) color.getGreen());
                buffer.put((byte) color.getBlue());
                buffer.put((byte) color.getAlpha());
            }
        }
        buffer.flip();
        return buffer;
    }

}
