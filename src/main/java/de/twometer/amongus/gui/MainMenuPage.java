package de.twometer.amongus.gui;

import de.twometer.amongus.AmongUs;
import de.twometer.neko.gui.Page;

public class MainMenuPage extends Page {

    public MainMenuPage() {
        super("MainMenu.html");
    }

    public void createGame() {

    }

    public void joinGame() {

    }

    public void settings() {

    }

    public void quit() {
        AmongUs.get().getWindow().close();
    }

    public void credits() {

    }

}
