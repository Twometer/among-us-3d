package de.twometer.amongus3d.core;

import de.twometer.amongus3d.engine.GameWindow;

import static org.lwjgl.opengl.GL11.*;

public class Game {

    private static final Game gameInstance = new Game();

    private final GameWindow window = new GameWindow("Among Us 3D - v0.1.0", 800, 600);

    private Game() {
    }

    public static Game getInstance() {
        return gameInstance;
    }

    public void run() {
        window.create();
        setup();

        while (!window.shouldClose()) {
            renderFrame();
            window.update();
        }

        window.destroy();
    }

    private void setup() {
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glClearColor(0, 0, 0, 0);
    }

    private void renderFrame() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


    }

}
