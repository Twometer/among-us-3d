package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.model.GameState;
import de.twometer.amongus.model.PlayerRole;
import de.twometer.amongus.net.NetMessage;

public class EjectPage extends BasePage {

    private final int player;
    private final NetMessage.OnPlayerEjected.Result result;

    public EjectPage(int player, NetMessage.OnPlayerEjected.Result result) {
        super("Eject.html");
        this.player = player;
        this.result = result;
    }

    @Override
    public void onDomReady() {
        super.onDomReady();

        amongUs.getScheduler().runLater(1500, () -> {
            switch (result) {
                case Tie:
                    context.call("drawEjectMessage", "No one was ejected (Tie)", "");
                    break;
                case Skip:
                    context.call("drawEjectMessage", "No one was ejected (Skipped)", "");
                    break;
                case Eject:
                    var ejectedPlayer = amongUs.getSession().getPlayer(player);
                    if (amongUs.getSession().getConfig().isConfirmEjects()) {
                        var impostors = amongUs.getSession().getPlayers().stream().filter(p -> p.getRole() == PlayerRole.Impostor && p.alive).count();
                        var builder = new StringBuilder();
                        builder.append(ejectedPlayer.username);
                        builder.append(" was ");
                        if (ejectedPlayer.role != PlayerRole.Impostor)
                            builder.append("not ");
                        builder.append((impostors > 1) ? "An Impostor" : "The Impostor");

                        var remainMsg = impostors == 1 ? " Impostor remains" : " Impostors remain";
                        context.call("drawEjectMessage", builder.toString(), impostors + remainMsg);
                    } else {
                        context.call("drawEjectMessage", ejectedPlayer.getUsername() + " was ejected.", "");
                    }
                    break;
            }
        });
    }

    public void close() {
        if (amongUs.getStateController().getState() == GameState.End) {
            amongUs.getGuiManager().showPage(new GameEndPage());
        } else {
            amongUs.getGuiManager().showPage(new IngamePage());
        }
    }

}
