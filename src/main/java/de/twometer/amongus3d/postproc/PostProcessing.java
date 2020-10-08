package de.twometer.amongus3d.postproc;

import de.twometer.amongus3d.render.Framebuffer;

import static org.lwjgl.opengl.GL30.*;

public class PostProcessing {

    private final float[] POSITIONS = {-1, 1, -1, -1, 1, 1, 1, -1};

    private int vao;


    public void initialize() {
        this.vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, POSITIONS, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);
    }

    public void begin() {
        glBindVertexArray(vao);
        glEnableVertexAttribArray(0);
    }

    public void bindTexture(int unit, int tex) {
        glActiveTexture(GL_TEXTURE0 + unit);
        glBindTexture(GL_TEXTURE_2D, tex);
    }

    public void copyTo(Framebuffer target) {
        if (target != null) target.bind();
        glClear(GL_COLOR_BUFFER_BIT);
        fullscreenQuad();
        if (target != null) target.unbind();
    }

    public void fullscreenQuad() {
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    }

    public void end() {
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        glActiveTexture(GL_TEXTURE0);
    }

}
