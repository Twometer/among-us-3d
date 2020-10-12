package de.twometer.amongus3d.ui.screen;

import de.twometer.amongus3d.audio.SoundFX;
import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.model.NetMessage;
import de.twometer.amongus3d.model.player.Player;
import de.twometer.amongus3d.ui.GuiRenderer;
import de.twometer.amongus3d.ui.component.ButtonComponent;
import de.twometer.amongus3d.ui.component.EmptyComponent;
import de.twometer.amongus3d.ui.component.LabelComponent;
import de.twometer.amongus3d.util.Constants;
import de.twometer.amongus3d.util.Log;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

public class EmergencyScreen extends GuiScreen {

    private Map<String, ButtonComponent> voteButtons = new HashMap<>();

    private ButtonComponent skipButton;

    private long voteEnd;

    public EmergencyScreen(boolean dead, String reporter, long votEnd) {
        this.voteEnd = System.currentTimeMillis() + votEnd;
        addComponent(new EmptyComponent(0, 50));
        addComponent(new LabelComponent(0, 70, dead ? "DEAD BODY REPORTED" : "EMERGENCY MEETING", 0.8f));
        addComponent(new EmptyComponent(0, 25));
        addComponent(new LabelComponent(0, 100, "Who is the impostor?", 0.6f));
        for (Player user : Game.instance().getClient().users.values()) {
            Runnable listener = () -> castVote(user.getUsername());
            user.resetVotes();
            ButtonComponent component = new ButtonComponent(500, 40, user.getUsername())
                    .setClickListener(listener);
            voteButtons.put(user.getUsername(), component);

            if (user.getUsername().equals(reporter))
                component.setColor(new Vector4f(1, 1, 0, 1));

            addComponent(component);
            addComponent(new EmptyComponent(0, 25));
        }
        addComponent(new EmptyComponent(0, 25));
        addComponent(skipButton = new ButtonComponent(500, 40, "SKIP VOTING").setClickListener(() -> castVote(Constants.SKIP_USER)));
    }

    private boolean hasVoted = false;
    private int skipVotes = 0;

    private void castVote(String user) {
        if (hasVoted)
            return;
        hasVoted = true;
        NetMessage.VoteCast cast = new NetMessage.VoteCast();
        cast.dstUsername = user;
        cast.srcUsername = Game.instance().getSelf().getUsername();
        Game.instance().getClient().sendMessage(cast);
    }

    @Subscribe
    public void onVoteCast(NetMessage.VoteCast cast) {
        SoundFX.playRandom("vote");

        voteButtons.get(cast.srcUsername).setColor(new Vector4f(0, 1, 0, 1));


        if (cast.dstUsername.equals(Constants.SKIP_USER)) {
            skipVotes++;
            Log.d("Skip vote by " + cast.srcUsername + " " + skipVotes);
            return;
        }
        Log.d(cast.srcUsername + " votes for " + cast.dstUsername);
        Player dstUser = Game.instance().getClient().getPlayer(cast.dstUsername);
        dstUser.vote();
    }

    @Subscribe
    public void onVotingEnd(NetMessage.VotingEnd end) {
        Log.d("sk " + skipVotes);
        voteEnd = System.currentTimeMillis() + 5000;
        endMessage = "Continuing";
        endMessage2 = "Continuing...";
        skipButton.setText("SKIP VOTING (" + skipVotes + ")");
        SoundFX.playRandom("vote");
        for (Player player : Game.instance().getClient().users.values())
            voteButtons.get(player.getUsername()).setText(player.getUsername() + " (" + player.getEjectionVotes() + ")");
    }

    String endMessage = "Voting closes";
    String endMessage2 = "Voting closed";

    @Override
    public void render(GuiRenderer renderer) {
        super.render(renderer);
        long remaining = (voteEnd - System.currentTimeMillis()) / 1000;

        renderer.getFontRenderer().draw(remaining > 0 ? endMessage + " in " + remaining + "s" : endMessage2, 20, Game.instance().getWindow().getHeight() - 50, 0.5f, new Vector4f(1, 1, 1, 1));
    }
}
