package de.twometer.amongus3d.mesh;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Model {

    private final int vao;

    private final int vertexBuffer;
    private final int colorBuffer;
    private final int normalBuffer;
    private final int texCoordBuffer;

    private final int vertices;

    private final int primitiveType;

    private Model(int vao, int vertexBuffer, int colorBuffer, int normalBuffer, int texCoordBuffer, int vertices, int primitiveType) {
        this.vao = vao;
        this.vertexBuffer = vertexBuffer;
        this.colorBuffer = colorBuffer;
        this.normalBuffer = normalBuffer;
        this.texCoordBuffer = texCoordBuffer;
        this.vertices = vertices;
        this.primitiveType = primitiveType;
    }

    public static Model create(Mesh mesh, int primitiveType) {
        int dimensions = mesh.getDimensions();

        mesh.getVertices().flip();
        if (mesh.getColorCount() > 0) mesh.getColors().flip();
        if (mesh.getNormalCount() > 0) mesh.getNormals().flip();
        if (mesh.getTexCoordCount() > 0) mesh.getTexCoords().flip();

        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vertexBuffer = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
        glBufferData(GL_ARRAY_BUFFER, mesh.getVertices(), GL_STATIC_DRAW);
        glVertexAttribPointer(0, dimensions, GL_FLOAT, false, 0, 0);

        int colorBuffer = -1;
        if (mesh.getColorCount() > 0) {
            colorBuffer = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
            glBufferData(GL_ARRAY_BUFFER, mesh.getColors(), GL_STATIC_DRAW);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        }

        int normalBuffer = -1;
        if (mesh.getNormalCount() > 0) {
            normalBuffer = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, normalBuffer);
            glBufferData(GL_ARRAY_BUFFER, mesh.getNormals(), GL_STATIC_DRAW);
            glVertexAttribPointer(2, dimensions, GL_FLOAT, false, 0, 0);
        }

        int texCoordBuffer = -1;
        if (mesh.getTexCoordCount() > 0) {
            texCoordBuffer = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, texCoordBuffer);
            glBufferData(GL_ARRAY_BUFFER, mesh.getTexCoords(), GL_STATIC_DRAW);
            glVertexAttribPointer(3, 2, GL_FLOAT, false, 0, 0);
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        return new Model(vao, vertexBuffer, colorBuffer, normalBuffer, texCoordBuffer, mesh.getVertexCount(), primitiveType);
    }

    public void destroy() {
        glDeleteBuffers(vertexBuffer);
        glDeleteBuffers(colorBuffer);
        glDeleteBuffers(normalBuffer);
        glDeleteBuffers(texCoordBuffer);
        glDeleteVertexArrays(vao);
    }

    public void draw() {
        boolean hasColors = colorBuffer != -1;
        boolean hasNormalsOrTex = normalBuffer != -1 || texCoordBuffer != -1;

        glBindVertexArray(vao);

        glEnableVertexAttribArray(0);
        if (hasColors) glEnableVertexAttribArray(1);
        if (hasNormalsOrTex) glEnableVertexAttribArray(2);

        glDrawArrays(primitiveType, 0, vertices);

        if (hasNormalsOrTex) glDisableVertexAttribArray(2);
        if (hasColors) glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);

        glBindVertexArray(0);
    }

}
