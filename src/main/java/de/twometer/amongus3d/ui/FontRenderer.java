package de.twometer.amongus3d.ui;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.render.shaders.ShaderFont;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class FontRenderer {

    private Font font;

    private ShaderFont shader;

    private int vao;
    private int vbo;

    public FontRenderer(Font font) {
        this.font = font;
        this.shader = Game.instance().getShaderProvider().getShader(ShaderFont.class);
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
    }

    public float getStringWidth(String string, float fontSize) {
        fontSize *= Game.instance().getWindow().getScale();
        float cursor = 0;
        for (char c : string.toCharArray()) {
            Glyph glyph = font.getGlyphs().get((int)c);
            if (glyph == null) continue;
            cursor += (glyph.advance - 12) * fontSize;
        }
        return cursor;
    }

    public void drawCentered(String text, float x, float y, float fontSize, Vector4f color) {
        float width = getStringWidth(text, fontSize);
        draw(text, x - width / 2f, y, fontSize, color);
    }

    public void draw(String value, float x, float y, float size, Vector4f color) {
        size *= Game.instance().getWindow().getScale();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBindVertexArray(vao);
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0);

        shader.bind();
        shader.setProjectionMatrix(Game.instance().getGuiMatrix());
        shader.setColor(color.x, color.y, color.z, color.w);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, font.getTexture().getTextureId());

        float tw = font.getTexture().getWidth();
        float th = font.getTexture().getHeight();

        for (char c : value.toCharArray()) {
            Glyph glyph = font.getGlyphs().get((int)c);
            if (glyph == null)
                continue;

            float x0 = x + glyph.xOffset * size;
            float y0 = y + glyph.yOffset * size;
            float x1 = x0 + glyph.width * size;
            float y1 = y0 + glyph.height * size;

            float u0 = glyph.x / tw;
            float v0 = glyph.y / th;
            float u1 = (glyph.x + glyph.width) / tw;
            float v1 = (glyph.y + glyph.height) / th;

            float box[] = {
                x0, y0, u0, v0,
                        x1, y0, u1, v0,
                        x0, y1, u0, v1,
                        x1, y1, u1, v1
            };
            glBufferData(GL_ARRAY_BUFFER, box, GL_DYNAMIC_DRAW);
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

            x += (glyph.advance - 12.0f) * size;
        }


        shader.unbind();

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

}
