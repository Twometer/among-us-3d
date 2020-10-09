package de.twometer.amongus3d.ui;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.core.GameState;
import de.twometer.amongus3d.net.*;
import de.twometer.amongus3d.util.Log;
import org.greenrobot.eventbus.Subscribe;

public class MainMenuScreen extends GuiScreen {

    public InputBoxComponent usernameBox;
    public InputBoxComponent gameCodeBox;
    public ButtonComponent joinGame;
    public ButtonComponent createGame;

    public MainMenuScreen() {
        addComponent(new EmptyComponent(0, 50));
        addComponent(new LabelComponent(0, 150, Game.NAME, 1.5f));
        addComponent(new LabelComponent(0, 30, Game.VERSION, 0.5f));
        addComponent(new EmptyComponent(0, 100));

        addComponent(new LabelComponent(0, 30, "Username", 0.25f));
        addComponent(usernameBox = new InputBoxComponent(200, 40));
        addComponent(new EmptyComponent(0, 10));
        addComponent(new LabelComponent(0, 30, "Game code", 0.25f));
        addComponent(gameCodeBox = new InputBoxComponent(200, 40));

        addComponent(new EmptyComponent(0, 100));
        addComponent(joinGame = new ButtonComponent(200, 40, "Join game"));
        addComponent(new EmptyComponent(0, 25));
        addComponent(createGame = new ButtonComponent(200, 40, "Create game"));

        joinGame.setClickListener(() -> {
            Game.instance().getClient().sendMessage(new JoinGameMessage(gameCodeBox.getText(), usernameBox.getText()));
        });
        createGame.setClickListener(() -> {
            if (usernameBox.getText().trim().length() == 0)
                return;
            Game.instance().getClient().sendMessage(new CreateGameMessage())
                    .awaitReply(CreateGameMessageReply.class, r -> {
                        Log.i("Game code: " + r.gameCode);

                        Game.instance().getClient().sendMessage(new JoinGameMessage(r.gameCode, usernameBox.getText()))
                                .awaitReply(JoinGameMessageReply.class, r2 -> {
                                    Log.d("Joining");
                                    if (r2.ok) {
                                        Game.instance().getGameState().setCurrentState(GameState.State.Lobby);
                                        Game.instance().getGuiRenderer().setCurrentScreen(new LobbyScreen(r.gameCode));
                                    }
                                });
                    });
        });
    }



}
