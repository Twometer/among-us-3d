package de.twometer.amongus3d.ui;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.core.GameState;
import de.twometer.amongus3d.io.FontLoader;
import de.twometer.amongus3d.mesh.Mesh;
import de.twometer.amongus3d.mesh.Model;
import de.twometer.amongus3d.mesh.shading.ShadingStrategies;
import de.twometer.amongus3d.render.shaders.ShaderGuiFlat;
import de.twometer.amongus3d.render.shaders.ShaderGuiTex;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;

public class GuiRenderer {

    private Game game;

    private ShaderGuiFlat flatShader;
    private ShaderGuiTex texShader;

    private Matrix4f guiMatrix;

    private Model crosshairModel;

    private FontRenderer fontRenderer;

    private Model rectModel;

    private GuiScreen currentScreen;

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
                .putColor(1, 1, 1)
                .putColor(1, 1, 1)
                .putColor(1, 1, 1)
                .putColor(1, 1, 1)
                .bake(GL_LINES);

        rectModel = Mesh.create(4, 2)
                .withColors()
                .putVertex(0, 0)
                .putVertex(0, 1)
                .putVertex(1, 0)
                .putVertex(1, 1)
                .putColor(1, 1, 1)
                .putColor(1, 1, 1)
                .putColor(1, 1, 1)
                .putColor(1, 1, 1)
                .bake(GL_TRIANGLE_STRIP);

        Font font = FontLoader.loadFont("fonts/lucida.fnt", "fonts/lucida.png");
        fontRenderer = new FontRenderer(font);
    }

    public void onSizeChange(Matrix4f guiMatrix) {
        this.guiMatrix = guiMatrix;
        relayout();
    }

    public void render() {
        glClear(GL_DEPTH_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);
        game.setShadingStrategy(ShadingStrategies.NOP);
        switch (game.getGameState().getCurrentState()) {
            case Running: {
                flatShader.bind();
                flatShader.setProjectionMatrix(guiMatrix);
                flatShader.setTransformationMatrix(new Matrix4f().translation(game.getWindow().getWidth() / 2f - 5, game.getWindow().getHeight() / 2f - 5, 0));
                flatShader.setColor(new Vector4f(1,1,1,1));
                crosshairModel.render();
                break;
            }
            case Loading: {
                fontRenderer.drawCentered("Twometer Game Studios", game.getWindow().getWidth() / 2f, game.getWindow().getHeight() / 2f - 50.0f, 0.8f, new Vector4f(1, 1, 1, 1));
                fontRenderer.drawCentered("Loading...", game.getWindow().getWidth() / 2f, game.getWindow().getHeight() / 2f + 50.0f, 0.4f, new Vector4f(1, 1, 1, 1));
                break;
            }
            case Lobby:
            case Emergency:
            case Menu: {
                if (currentScreen != null)
                    currentScreen.render(this);
                break;
            }
        }
        glEnable(GL_DEPTH_TEST);
    }

    public void relayout() {
        if (currentScreen != null)
            currentScreen.relayout();
    }

    public void drawRect(int x, int y, int w, int h, Vector4f color) {
        flatShader.bind();
        flatShader.setProjectionMatrix(guiMatrix);
        flatShader.setTransformationMatrix(new Matrix4f().translate(x, y, 0).scale(w, h, 1));
        flatShader.setColor(color);
        rectModel.render();
    }

    public FontRenderer getFontRenderer() {
        return fontRenderer;
    }

    public void onClick(int x, int y) {
        if (currentScreen != null)
            currentScreen.handleClickEvent(x, y);
    }

    public void onCharTyped(char c) {
        currentScreen.onCharTyped(c);
    }

    public GuiScreen getCurrentScreen() {
        return currentScreen;
    }

    public void setCurrentScreen(GuiScreen currentScreen) {
        this.currentScreen = currentScreen;
        relayout();
    }
}
