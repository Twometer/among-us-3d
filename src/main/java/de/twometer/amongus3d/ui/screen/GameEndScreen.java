package de.twometer.amongus3d.ui.screen;

import de.twometer.amongus3d.audio.SoundFX;
import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.core.GameState;
import de.twometer.amongus3d.model.player.Player;
import de.twometer.amongus3d.model.player.Role;
import de.twometer.amongus3d.ui.GuiRenderer;
import de.twometer.amongus3d.util.Log;
import de.twometer.amongus3d.util.Timer;
import org.joml.Vector4f;

public class GameEndScreen extends GuiScreen {

    private final Timer timer = new Timer(50);

    public GameEndScreen() {


    }

    private float vis = 0;
    private float listvis = 0;
    private float time = 0;

    @Override
    public void render(GuiRenderer renderer) {
        super.render(renderer);

        if (timer.elapsed()) {
            if (vis < 1)
                vis += 0.05f * (1 - vis);
            if (vis > 0.8f)
                listvis += 0.05f;
            timer.reset();
            time += 0.02f;
        }

        if (time > 15) {
            Log.i("Game end, resetting...");
            for (Player p : Game.instance().getClient().users.values()) {
                p.setDead(false);
            }
            Game.instance().getGameState().setCurrentState(GameState.State.Menu);
            Game.instance().getGuiRenderer().setCurrentScreen(new LobbyScreen());
            return;
        }

        Role winners = Game.instance().getClient().winner;

        Vector4f color = winners == Role.Impostor ? new Vector4f(1, 0, 0, vis) : new Vector4f(0, 0.5f, 1, vis);
        renderer.getFontRenderer().drawCentered(winners == Role.Impostor ? "Defeat" : "Victory", getW() / 2f, 60f + vis * 20f, 2.0f, color);

        int y = 320;
        for (Player p : Game.instance().getClient().users.values()) {
            float o = (listvis * 35.0f) / (y - 285.0f);
            if (winners == Role.Impostor && p.getRole() == Role.Impostor) {
                renderer.getFontRenderer().drawCentered(p.getUsername(), getW() / 2f, y, 0.5f, new Vector4f(1, 0.0f, 0, o));
            } else if (winners == Role.Crewmate && p.getRole() == Role.Crewmate) {
                renderer.getFontRenderer().drawCentered(p.getUsername(), getW() / 2f, y, 0.5f, new Vector4f(0, 0.5f, 1, o));
            }
            y += 35;
        }
    }

    @Override
    public void onShown() {
        super.onShown();
        Game.instance().getGameState().stopMenuMusic();
        String sfx = Game.instance().getClient().winner == Role.Impostor ? "defeat" : "victory";
        SoundFX.play(sfx);
    }

    @Override
    public boolean renderStarfield() {
        return false;
    }
}
