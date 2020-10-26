package de.twometer.amongus.gui;

import de.twometer.amongus.AmongUs;
import de.twometer.amongus.model.PlayerColor;
import de.twometer.neko.gui.Page;

public class CustomizePage extends Page {

    public CustomizePage() {
        super("Customize.html");
    }

    @Override
    public void onDomReady() {
        var i = 0;
        for (var color : PlayerColor.values()) {
            var col = color.getValue();
            var colStr = String.format("rgb(%d,%d,%d)", (int) (col.getR() * 255), (int) (col.getG() * 255), (int) (255 * col.getB()));
            context.call("setColorBox", i, colStr);
            i++;
        }
    }

    public void apply() {
        AmongUs.get().getGuiManager().showPage(new LobbyPage());
    }

    public void back() {
        AmongUs.get().getGuiManager().showPage(new MainMenuPage());
    }

}
