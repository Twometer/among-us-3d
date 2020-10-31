package de.twometer.amongus.gui;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.event.UpdateEvent;
import de.twometer.amongus.net.NetMessage;
import de.twometer.neko.util.Timer;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

public class EmergencyPage extends BasePage {

    private final Timer timer = new Timer(1);
    private final int caller;
    private int votingTime;
    private int continueTime = 10;
    private boolean resultsIn;

    public EmergencyPage(int caller) {
        super("Emergency.html");
        this.caller = caller;
        votingTime = AmongUs.get().getSession().getConfig().getVotingTime();
    }

    @Override
    public void onDomReady() {
        super.onDomReady();
        for (var player : amongUs.getSession().getPlayers()) {
            context.call("addPlayer", player.id, player.username, player.color, !player.alive, player.id == caller);
        }
        context.setElementText("votingTimeout", "Voting ends in " + votingTime + "s");
        timer.reset();
    }

    public void vote(int id) {
        amongUs.getClient().sendMessage(new NetMessage.Vote(id));
    }

    private float pitch = 1.0f;

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (timer.elapsed()) {
            timer.reset();

            if (!resultsIn) {
                votingTime--;
                if (votingTime >= 0)
                    context.setElementText("votingTimeout", "Voting ends in " + votingTime + "s");
                if (votingTime < 4) {
                    AmongUs.get().getSoundFX().sourceBuilder("VoteTimer.ogg")
                            .setAbsolute(false)
                            .setPitch(pitch)
                            .play();
                    pitch += 0.1f;
                }
            } else {
                continueTime--;
                if (continueTime >= 0)
                    context.setElementText("votingTimeout", "Continuing in " + continueTime + "s");
            }
        }
    }

    @Subscribe
    public void onVote(NetMessage.OnPlayerVoted vote) {
        runOnUiThread(() -> context.call("onPlayerVote", vote.srcPlayerId));
        amongUs.getSoundFX().play("Voted.ogg");
    }

    @Subscribe
    public void onEject(NetMessage.OnPlayerEjected eject) {
        runOnUiThread(() -> amongUs.getGuiManager().showPage(new EjectPage(eject.playerId, eject.result)));
    }

    @Subscribe
    public void onResults(NetMessage.OnVoteResults results) {
        resultsIn = true;
        runOnUiThread(() -> {
            var idcolormap = new HashMap<Integer, String>();
            for (var player : amongUs.getSession().getPlayers())
                idcolormap.put(player.id, player.color.name());
            context.call("onResults", idcolormap, results.votes);
            amongUs.getSoundFX().play("VoteLockin.ogg");
        });
    }
}
