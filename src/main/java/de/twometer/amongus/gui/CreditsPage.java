package de.twometer.amongus.gui;

import de.twometer.amongus.AmongUs;
import de.twometer.neko.gui.Page;
import de.twometer.neko.sound.SoundSource;

public class CreditsPage extends BasePage {

    private SoundSource soundSource;

    public CreditsPage() {
        super("Credits.html");
    }

    public void back() {
        soundSource.stop();
        goBack();
    }

    @Override
    public void onDomReady() {
        soundSource = AmongUs.get().getSoundFX().play("CreditsRoll.ogg").setGain(0.85f);
    }

}
