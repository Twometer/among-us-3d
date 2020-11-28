package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.event.SabotageEvent;
import de.twometer.amongus.model.Sabotage;
import de.twometer.amongus.net.NetMessage;
import org.greenrobot.eventbus.Subscribe;

public class FixSabotagePage extends BasePage {

    private final Sabotage sabotage;

    public FixSabotagePage(Sabotage sabotage) {
        super("Tasks/Fix" + sabotage.name() + ".html");
        this.sabotage = sabotage;
    }

    @Override
    public void onDomReady() {
        if (AmongUs.get().getSession().currentSabotage == Sabotage.O2) {
            context.call("setCode", AmongUs.get().getSession().currentSabotageCode);
        }
    }

    public void setFixing(boolean fixing) {
        AmongUs.get().getClient().sendMessage(new NetMessage.FixSabotage(AmongUs.get().getSession().currentSabotage, fixing));
    }

    @Subscribe
    public void onSabotageFixed(SabotageEvent event) {
        if (amongUs.getSession().currentSabotage == null) {
            AmongUs.get().getScheduler().runLater(800, () -> {
                if (AmongUs.get().getStateController().isRunning())
                    this.goBack();
            });
        }
    }

    @Override
    protected boolean escapeGoesBack() {
        return true;
    }

}
