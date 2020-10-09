package de.twometer.amongus3d.ui;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.mesh.Mesh;
import de.twometer.amongus3d.mesh.Model;
import de.twometer.amongus3d.mesh.shading.ShadingStrategies;
import de.twometer.amongus3d.render.shaders.ShaderGuiFlat;
import de.twometer.amongus3d.render.shaders.ShaderGuiTex;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import static org.lwjgl.opengl.GL11.GL_LINES;

public class GuiRenderer {

    private Game game;

    private ShaderGuiFlat flatShader;
    private ShaderGuiTex texShader;

    private Matrix4f guiMatrix;

    private Model crosshairModel;

    public void init() {
        game = Game.instance();
        flatShader = game.getShaderProvider().getShader(ShaderGuiFlat.class);
        texShader = game.getShaderProvider().getShader(ShaderGuiTex.class);

        crosshairModel = Mesh.create(4, 2)
                .withColors()
                .putVertex(-10, 0)
                .putVertex(10, 0)
                .putVertex(0, -10)
                .putVertex(0, 10)
                .putColor(255, 255, 255)
                .putColor(255, 255, 255)
                .putColor(255, 255, 255)
                .putColor(255, 255, 255)
                .bake(GL_LINES);
    }

    public void onSizeChange(Matrix4f guiMatrix) {
        this.guiMatrix = guiMatrix;
    }

    public void render() {
        game.setShadingStrategy(ShadingStrategies.NOP);
        flatShader.bind();
        flatShader.setProjectionMatrix(guiMatrix);
        flatShader.setOffset(new Vector2f(game.getWindow().getWidth() / 2f - 5, game.getWindow().getHeight() / 2f - 5));
        crosshairModel.render();
    }

}
