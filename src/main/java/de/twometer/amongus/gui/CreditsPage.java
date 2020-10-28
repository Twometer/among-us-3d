package de.twometer.amongus.gui;

import de.twometer.amongus.game.AmongUs;
import de.twometer.neko.sound.SoundSource;

public class CreditsPage extends BasePage {

    private SoundSource soundSource;

    public CreditsPage() {
        super("Credits.html");
    }

    @Override
    public void onUnload() {
        super.onUnload();
        soundSource.stop();
    }

    @Override
    public void onDomReady() {
        soundSource = AmongUs.get().getSoundFX().play("CreditsRoll.ogg").setGain(0.85f);
    }

    public void back() {
        goBack();
    }

}
