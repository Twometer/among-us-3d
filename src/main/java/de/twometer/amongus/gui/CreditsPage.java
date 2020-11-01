package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.model.GameState;
import de.twometer.neko.Neko;
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
        amongUs.getStateController().changeState(GameState.Menus);
    }

    @Override
    public void onDomReady() {
        soundSource = AmongUs.get().getSoundFX().play("CreditsRoll.ogg");
        context.setElementText("nekoVersion", Neko.VERSION);
        amongUs.getStateController().changeState(GameState.Credits);
    }

    public void back() {
        goBack();
    }

}
