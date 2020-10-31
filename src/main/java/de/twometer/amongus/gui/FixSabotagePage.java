package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.model.Sabotage;
import de.twometer.amongus.net.NetMessage;

public class FixSabotagePage extends BasePage {

    public FixSabotagePage(Sabotage sabotage) {
        super("Tasks/Fix" + sabotage.name() + ".html");
    }

    @Override
    public void onDomReady() {
        if (AmongUs.get().getSession().currentSabotage == Sabotage.O2) {
            context.call("setCode", AmongUs.get().getSession().currentSabotageCode);
        }
    }

    public void setFixing(boolean fixing) {
        AmongUs.get().getClient().sendMessage(new NetMessage.FixSabotage(AmongUs.get().getSession().currentSabotage, fixing));
        amongUs.getScheduler().runLater(800, this::goBack);
    }

    @Override
    protected boolean escapeGoesBack() {
        return true;
    }

}
