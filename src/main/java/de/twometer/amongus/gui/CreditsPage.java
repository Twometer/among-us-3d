package de.twometer.amongus.gui;

import de.twometer.amongus.AmongUs;
import de.twometer.neko.gui.Page;
import de.twometer.neko.sound.SoundSource;

public class CreditsPage extends Page {

    private SoundSource soundSource;

    public CreditsPage() {
        super("Credits.html");
    }

    public void back() {
        AmongUs.get().getGuiManager().showPage(new MainMenuPage());
        soundSource.stop();
    }

    @Override
    public void onDomReady() {
        soundSource = AmongUs.get().getSoundFX().play("CreditsRoll.ogg").setGain(0.85f);
    }

}
