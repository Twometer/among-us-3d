package de.twometer.amongus3d.ui.screen;

import de.twometer.amongus3d.audio.SoundFX;
import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.core.GameState;
import de.twometer.amongus3d.model.player.Player;
import de.twometer.amongus3d.model.player.Role;
import de.twometer.amongus3d.ui.GuiRenderer;
import de.twometer.amongus3d.util.Timer;
import org.joml.Vector4f;

public class GameStartScreen extends GuiScreen {

    private final Timer timer = new Timer(50);

    public GameStartScreen() {


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

        if (time > 3.5f) {
            Game.instance().getGameState().setCurrentState(GameState.State.Running);
        }

        Player player = Game.instance().getSelf();
        Vector4f color = player.getRole() == Role.Impostor ? new Vector4f(1, 0, 0, vis) : new Vector4f(0, 0.5f, 1, vis);
        renderer.getFontRenderer().drawCentered(player.getRole().toString(), getW() / 2f, 60f + vis * 20f, 2.0f, color);

        if (player.getRole() == Role.Crewmate) {
            long impostors = Game.instance().getClient().users.values().stream().filter(p -> p.getRole() == Role.Impostor).count();
            renderer.getFontRenderer().drawCentered("There are " + impostors + " impostors among us.", getW() / 2f, 250, 0.5f, new Vector4f(1, 0, 0, time - 0.1f));
        }

        int y = 320;
        for (Player p : Game.instance().getClient().users.values()) {
            float o = (listvis * 35.0f) / (y - 285.0f);
            if (player.getRole() == Role.Impostor && p.getRole() == Role.Impostor) {
                renderer.getFontRenderer().drawCentered(p.getUsername(), getW() / 2f, y, 0.5f, new Vector4f(1, 0.0f, 0, o));
            } else if (player.getRole() == Role.Crewmate) {
                renderer.getFontRenderer().drawCentered(p.getUsername(), getW() / 2f, y, 0.5f, new Vector4f(0, 0.5f, 1, o));
            }
            y += 35;
        }
    }

    @Override
    public void onShown() {
        super.onShown();
        Game.instance().getGameState().stopMenuMusic();
        SoundFX.play("game_start");
    }

    @Override
    public boolean renderStarfield() {
        return false;
    }
}
