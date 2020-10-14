package de.twometer.amongus3d.ui.screen;

import de.twometer.amongus3d.client.AmongUsClient;
import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.core.GameState;
import de.twometer.amongus3d.model.NetMessage;
import de.twometer.amongus3d.ui.component.ButtonComponent;
import de.twometer.amongus3d.ui.component.EmptyComponent;
import de.twometer.amongus3d.ui.component.InputBoxComponent;
import de.twometer.amongus3d.ui.component.LabelComponent;
import de.twometer.amongus3d.util.Log;

import java.util.function.Consumer;

public class MainMenuScreen extends GuiScreen {

    public InputBoxComponent usernameBox;
    public InputBoxComponent gameCodeBox;
    public ButtonComponent joinGame;
    public ButtonComponent createGame;

    private String gameId;

    public MainMenuScreen() {
        addComponent(new EmptyComponent(0, 50));
        addComponent(new LabelComponent(0, 150, Game.NAME, 1.5f));
        addComponent(new LabelComponent(0, 30, Game.VERSION, 0.5f));
        addComponent(new EmptyComponent(0, 100));

        addComponent(new LabelComponent(0, 30, "Username", 0.25f));
        addComponent(usernameBox = new InputBoxComponent(200, 40));
        addComponent(new EmptyComponent(0, 50));
        addComponent(new LabelComponent(0, 30, "Game code", 0.25f));
        addComponent(gameCodeBox = new InputBoxComponent(200, 40));

        addComponent(new EmptyComponent(0, 20));
        addComponent(joinGame = new ButtonComponent(200, 40, "Join game"));
        addComponent(new EmptyComponent(0, 50));
        addComponent(createGame = new ButtonComponent(200, 40, "Create game"));
        addComponent(new EmptyComponent(0, 50));
        addComponent(new ButtonComponent(200, 40, "Exit").setClickListener(() -> Game.instance().getWindow().close()));

        joinGame.setClickListener(() -> {
            Game.instance().getSelf().setUsername(usernameBox.getText());
            gameId = gameCodeBox.getText();
            Game.instance().getClient().sendMessage(new NetMessage.JoinGame(usernameBox.getText(), gameCodeBox.getText()))
                    .awaitReply(NetMessage.GameJoined.class, gameJoinHandler);
        });
        createGame.setClickListener(() -> {
            if (usernameBox.getText().trim().length() == 0)
                return;
            Game.instance().getSelf().setUsername(usernameBox.getText());
            Game.instance().getClient().sendMessage(new NetMessage.CreateGame())
                    .awaitReply(NetMessage.GameCreated.class, r -> {
                        Log.d("Game code: " + r.gameId);
                        gameId = r.gameId;

                        Game.instance().getClient().sendMessage(new NetMessage.JoinGame(usernameBox.getText(), r.gameId))
                                .awaitReply(NetMessage.GameJoined.class, gameJoinHandler);
                    });
        });
    }

    private Consumer<NetMessage.GameJoined> gameJoinHandler = r2 -> {
        Log.d("Joining...");
        if (r2.ok) {
            Log.d("Join OK");
            Game.instance().getClient().gameCode = gameId;
            Game.instance().getClient().isHost = r2.host;
            Game.instance().getGameState().setCurrentState(GameState.State.Lobby);
            Game.instance().getGuiRenderer().setCurrentScreen(new LobbyScreen());
        }
    };

}
