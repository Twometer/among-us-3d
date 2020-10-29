package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;

public class GameEndPage extends BasePage {
    public GameEndPage() {
        super("GameEnd.html");
        AmongUs.get().getScheduler().runLater(7500, () -> AmongUs.get().getGuiManager().showPage(new LobbyPage("unknown")));
    }
}
