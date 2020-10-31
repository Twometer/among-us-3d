package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.model.PlayerRole;

public class GameEndPage extends BasePage {

    public GameEndPage() {
        super("GameEnd.html");
        AmongUs.get().getScheduler().runLater(10000, () -> AmongUs.get().getGuiManager().showPage(new LobbyPage()));
    }

    @Override
    public void onDomReady() {
        super.onDomReady();
        var s = amongUs.getSession();
        var me = amongUs.getSession().getMyself();
        if (s.winners != me.role) {
            context.call("setDefeat");
            amongUs.getSoundFX().play("Defeat.ogg");
        } else {
            amongUs.getSoundFX().play("Victory.ogg");
        }

        for (var player : s.getPlayers()) {
            context.call("addPlayer", player.username, player.role == PlayerRole.Impostor);
        }
    }
}
