package de.twometer.amongus3d.ui;

import de.twometer.amongus3d.net.PlayerJoinedMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class LobbyScreen extends GuiScreen {

    private String gameCode;

    public LobbyScreen(String gameCode) {
        this.gameCode = gameCode;
        EventBus.getDefault().register(this);
        addComponent(new LabelComponent(0, 50, "Code: " + gameCode, 0.5f));
        addComponent(new LabelComponent(0, 50, "Players: 1/10", 0.5f));
        ButtonComponent startGame;
        addComponent(startGame = new ButtonComponent(200, 40, "Start game"));
        startGame.setClickListener(() -> {

        });
    }

    @Subscribe
    public void handleJoinGame(PlayerJoinedMessage message) {

    }

}
