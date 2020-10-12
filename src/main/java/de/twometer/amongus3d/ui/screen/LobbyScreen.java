package de.twometer.amongus3d.ui.screen;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.core.GameState;
import de.twometer.amongus3d.model.NetMessage;
import de.twometer.amongus3d.ui.GuiRenderer;
import de.twometer.amongus3d.ui.component.ButtonComponent;
import de.twometer.amongus3d.ui.component.LabelComponent;
import de.twometer.amongus3d.util.Log;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joml.Vector4f;

public class LobbyScreen extends GuiScreen {

    private String gameCode;

    private boolean host;

    private LabelComponent numPlayers;

    public LobbyScreen(String gameCode, boolean host) {
        this.gameCode = gameCode;
        addComponent(new LabelComponent(0, 50, "Code: " + gameCode, 0.5f));
        addComponent(numPlayers = new LabelComponent(0, 50, "Players: 0/10", 0.5f));
        numPlayers.setText("Players: " + Game.instance().getClient().users.size() + "/10");
        ButtonComponent startGame;
        if (host) {
            addComponent(startGame = new ButtonComponent(200, 40, "Start game"));
            startGame.setClickListener(() -> {
                Game.instance().getClient().sendMessage(new NetMessage.StartGame());
            });
        }

    }

    @Override
    public void render(GuiRenderer renderer) {
        super.render(renderer);
        int y = 250;
        for (String user : Game.instance().getClient().users.keySet()) {
            renderer.getFontRenderer().drawCentered(user, Game.instance().getWindow().getWidth() / 2f, y, 0.5f, new Vector4f(1, 1, 1, 1));
            y += 30;
        }
    }

    @Subscribe
    public void handleJoinGame(NetMessage.PlayerJoined message) {
        numPlayers.setText("Players: " + Game.instance().getClient().users.size() + "/10");
    }

    @Subscribe
    public void handleGameStarted(NetMessage.GameStarted message) {
        Log.i("game start");
    }

}
