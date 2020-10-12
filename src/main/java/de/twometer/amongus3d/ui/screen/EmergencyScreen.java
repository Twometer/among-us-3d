package de.twometer.amongus3d.ui.screen;

import de.twometer.amongus3d.audio.SoundFX;
import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.model.NetMessage;
import de.twometer.amongus3d.ui.component.ButtonComponent;
import de.twometer.amongus3d.ui.component.EmptyComponent;
import de.twometer.amongus3d.ui.component.LabelComponent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joml.Vector4f;

public class EmergencyScreen extends GuiScreen {

    public EmergencyScreen(boolean dead, String reporter) {
        addComponent(new LabelComponent(0, 70, dead ? "DEAD BODY REPORTED" : "EMERGENCY MEETING", 0.8f));
        addComponent(new LabelComponent(0, 100, "Who is the impostor?", 0.6f));
        EventBus.getDefault().register(this);
        for (String user : Game.instance().getClient().users) {
            Runnable listener = () -> {
                NetMessage.VoteCast cast = new NetMessage.VoteCast();
                cast.dstUsername = user;
                cast.srcUsername = Game.instance().getSelf().getUsername();
                Game.instance().getClient().sendMessage(cast);
            };
            if (user.equals(reporter))
                addComponent(new ButtonComponent(500, 40, user).setColor(new Vector4f(1, 1, 0, 1)).setClickListener(listener));
            else
                addComponent(new ButtonComponent(500, 40, user).setClickListener(listener));
            addComponent(new EmptyComponent(0, 25));
        }
    }

    @Subscribe
    public void onVoteCast(NetMessage.VoteCast cast) {
        SoundFX.playRandom("vote");
    }

}
