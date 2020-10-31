package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.model.Sabotage;
import de.twometer.amongus.net.NetMessage;

public class SabotagePage extends BasePage {

    private static long cooldown;

    public SabotagePage() {
        super("Sabotage.html");
        if (cooldown == 0)
            cooldown = System.currentTimeMillis() + 15000;
    }

    @Override
    public void onDomReady() {
        super.onDomReady();
        if (cooldown > System.currentTimeMillis())
            context.call("setCooldown");
    }

    public void sabotage(String sabotage) {
        var sab = Sabotage.valueOf(sabotage);
        if (cooldown < System.currentTimeMillis()) {
            cooldown = System.currentTimeMillis() + 30000;
            AmongUs.get().getClient().sendMessage(new NetMessage.StartSabotage(sab));
        }
    }

    @Override
    protected boolean escapeGoesBack() {
        return true;
    }
}
