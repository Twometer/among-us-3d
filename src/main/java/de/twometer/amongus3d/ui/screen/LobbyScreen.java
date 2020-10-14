package de.twometer.amongus3d.ui.screen;

import de.twometer.amongus3d.client.AmongUsClient;
import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.core.GameState;
import de.twometer.amongus3d.model.NetMessage;
import de.twometer.amongus3d.ui.GuiRenderer;
import de.twometer.amongus3d.ui.component.ButtonComponent;
import de.twometer.amongus3d.ui.component.EmptyComponent;
import de.twometer.amongus3d.ui.component.LabelComponent;
import de.twometer.amongus3d.util.Log;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joml.Vector4f;

public class LobbyScreen extends GuiScreen {

    private final LabelComponent numPlayers;

    public LobbyScreen() {
        AmongUsClient c = Game.instance().getClient();
        addComponent(new EmptyComponent(0, 25));
        addComponent(new LabelComponent(0, 50, "Code: " + c.gameCode, 0.5f));
        addComponent(new EmptyComponent(0, 25));
        ButtonComponent startGame;
        if (c.isHost) {
            addComponent(startGame = new ButtonComponent(200, 40, "Start game"));
            startGame.setClickListener(() -> {
                Game.instance().getClient().sendMessage(new NetMessage.StartGame());
            });
        }
        addComponent(new EmptyComponent(0, 25));
        addComponent(new ButtonComponent(200, 40, "Disconnect").setClickListener(() -> {
            Game.instance().getClient().disconnect();
            Game.instance().getGuiRenderer().setCurrentScreen(new MainMenuScreen());
        }));

        addComponent(new EmptyComponent(0, 30));
        addComponent(numPlayers = new LabelComponent(0, 50, "Players (x):", 0.5f));
        updateNumPlayers();
    }

    private void updateNumPlayers() {
        numPlayers.setText("Players (" + Game.instance().getClient().users.size() + "):");
    }

    @Override
    public void render(GuiRenderer renderer) {
        super.render(renderer);
        int y = Game.instance().getClient().isHost ? 285 : 250;
        for (String user : Game.instance().getClient().users.keySet()) {
            renderer.getFontRenderer().drawCentered(user, Game.instance().getWindow().getWidth() / 2f, y, 0.5f, new Vector4f(1, 1, 1, 1));
            y += 30;
        }
    }

    @Subscribe
    public void handleJoinGame(NetMessage.PlayerJoined message) {
        updateNumPlayers();
    }

    @Subscribe
    public void handleLeaveGame(NetMessage.PlayerLeft message) {
        updateNumPlayers();
    }

    @Subscribe
    public void handleGameStarted(NetMessage.GameStarted message) {
        Log.i("Lobby event: game started");
    }

}
