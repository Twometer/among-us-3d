package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.model.ClientSession;
import de.twometer.amongus.model.PlayerColor;
import de.twometer.amongus.model.SessionConfig;
import de.twometer.amongus.net.NetMessage;
import de.twometer.neko.util.Log;

public class CustomizePage extends BasePage {

    private SessionConfig config = new SessionConfig();
    private PlayerColor color = PlayerColor.Red;

    public CustomizePage() {
        super("Customize.html");
    }

    @Override
    public void onDomReady() {
        var i = 0;
        for (var color : PlayerColor.values()) {
            var col = color.getValue();
            var colStr = String.format("rgb(%d,%d,%d)", (int) (col.getR() * 255), (int) (col.getG() * 255), (int) (255 * col.getB()));
            context.call("setColorBox", i, colStr, color.name());
            i++;
        }
    }

    public void setConfig(String conf) {
        this.config = gson.fromJson(conf, SessionConfig.class);
        //Log.d("Config changed: " + config.getVotingTime() + " " + config.isConfirmEjects() + " " + config.getPlayerSpeed());
    }

    public void setColor(String color) {
        this.color = Enum.valueOf(PlayerColor.class, color);
        //Log.d("Set color to be " + color);
    }

    public void apply() {
        showLoading("Creating session...");
        // Create session
        amongUs.getClient().sendMessage(new NetMessage.SessionCreate())
                .await(NetMessage.SessionCreated.class, created -> {

                    if (created.result == NetMessage.SessionCreated.Result.MatchmakerFull) {
                        showError("The matchmaker is full. Please try again later.");
                        return;
                    }

                    // Join the session
                    showLoading("Joining...");
                    sendJoinMessage(created.code, () -> {
                        // Configure the session
                        showLoading("Configuring session...");
                        amongUs.getClient().sendMessage(new NetMessage.SessionConfigure(config))
                                .await(NetMessage.SessionConfigured.class, configured -> {

                                    // Configure the player
                                    showLoading("Configuring player...");
                                    amongUs.getClient().sendMessage(new NetMessage.ColorChange())
                                            .await(NetMessage.ColorChanged.class, changed -> {

                                                // Finally, let's go to the lobby
                                                AmongUs.get().getGuiManager().showPage(new LobbyPage());

                                            }).handleError(this::networkError);

                                }).handleError(this::networkError);

                    });

                }).handleError(this::networkError);
    }


    public void back() {
        goBack();
    }

}
