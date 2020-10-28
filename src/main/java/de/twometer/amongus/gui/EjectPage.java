package de.twometer.amongus.gui;

import de.twometer.amongus.AmongUs;

public class EjectPage extends BasePage {

    public EjectPage() {
        super("Eject.html");
    }

    public void close() {
        goBack();
    }

}
