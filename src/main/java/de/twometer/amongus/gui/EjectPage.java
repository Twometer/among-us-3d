package de.twometer.amongus.gui;

public class EjectPage extends BasePage {

    public EjectPage() {
        super("Eject.html");
    }

    public void close() {
        goBack();
    }

}
