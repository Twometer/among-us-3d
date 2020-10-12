package de.twometer.amongus3d.ui.screen;

import de.twometer.amongus3d.audio.SoundFX;
import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.core.GameState;
import de.twometer.amongus3d.ui.GuiRenderer;
import de.twometer.amongus3d.util.Log;
import de.twometer.amongus3d.util.Timer;
import org.joml.Vector4f;

public class EjectScreen extends GuiScreen {

    private String message;

    private String message2;

    private int progress;

    private Timer timer;
    private Timer timer2 = new Timer(50);

    private float vis = -5f;

    public EjectScreen(String message, String message2) {
        this.message = message;
        this.message2 = message2;
        long ms = 1500 / message.length();
        timer = new Timer((int) (1000.0 / ms));
    }

    @Override
    public void render(GuiRenderer renderer) {
        super.render(renderer);
        if (progress < message.length() && timer.elapsed()) {
            progress++;
            timer.reset();
        }
        if (progress >= message.length()) {
            if (timer2.elapsed()) {
                vis += 0.1f;
                timer2.reset();
            }
            if (vis > 20) {
                if (Game.instance().getClient().gameEnded)
                    Game.instance().getGuiRenderer().setCurrentScreen(new GameEndScreen());
                else
                    Game.instance().getGameState().setCurrentState(GameState.State.Running);
            }
            renderer.getFontRenderer().drawCentered(message2, getW() / 2f, getH() / 2f + 25, 0.45f, new Vector4f(1, 0.15f, 0.15f, vis));
        }

        renderer.getFontRenderer().drawCentered(message.substring(0, progress), getW() / 2f, getH() / 2f - 25, 0.5f, new Vector4f(1, 1, 1, 1));
    }

    @Override
    public void onShown() {
        super.onShown();
        timer.reset();
        SoundFX.play("eject");
    }
}
