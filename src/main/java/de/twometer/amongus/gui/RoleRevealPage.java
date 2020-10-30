package de.twometer.amongus.gui;

import de.twometer.amongus.model.PlayerRole;

public class RoleRevealPage extends BasePage {

    public RoleRevealPage() {
        super("RoleReveal.html");
        amongUs.getScheduler().runLater(4700, () -> amongUs.getGuiManager().showPage(new IngamePage()));
    }

    @Override
    public void onDomReady() {
        var players = amongUs.getSession().getPlayers();
        var me = amongUs.getSession().getMyself();
        context.call("setRole", me.role.name());

        for (var player : players) {
            if (me.role == PlayerRole.Impostor && player.role != me.role) continue;
            context.call("addTeammate", player.username);
        }
    }

}
