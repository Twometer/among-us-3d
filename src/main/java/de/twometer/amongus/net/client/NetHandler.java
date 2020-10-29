package de.twometer.amongus.net.client;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.model.ClientSession;
import de.twometer.amongus.model.Player;
import de.twometer.amongus.net.NetMessage;
import de.twometer.neko.util.Log;

public class NetHandler {

    private final NetClient client;
    private final AmongUs amongUs;

    public NetHandler(NetClient client) {
        this.client = client;
        this.amongUs = AmongUs.get();
    }

    public void handle(Object o) {
        Log.d("Received " + o.toString());
        if (o instanceof NetMessage.OnPlayerUpdate) {
            var update = (NetMessage.OnPlayerUpdate) o;
            var player = amongUs.getSession().getPlayer(update.id);
            player.role = update.role;
            player.color = update.color;
        } else if (o instanceof NetMessage.OnPlayerLeave) {
            var leave = (NetMessage.OnPlayerLeave)o;
            amongUs.getSession().removePlayer(leave.id);
        } else if (o instanceof NetMessage.OnPlayerJoin) {
            var join = (NetMessage.OnPlayerJoin)o;
            var player = new Player();
            player.id = join.id;
            player.color = join.color;
            player.username = join.username;
            amongUs.getSession().addPlayer(player);
        } else if (o instanceof NetMessage.PositionChange) {
            var change = (NetMessage.PositionChange)o;
            var player = amongUs.getSession().getPlayer(change.playerId);
            player.position = change.position;
            player.rotation = change.rotation;
        } else if (o instanceof NetMessage.OnSessionUpdate) {
            amongUs.getSession().setConfig(((NetMessage.OnSessionUpdate) o).config);
        } else if (o instanceof NetMessage.SessionJoined) {
            var joined = (NetMessage.SessionJoined)o;
            if (joined.result == NetMessage.SessionJoined.Result.Success) {
                amongUs.setSession(new ClientSession(joined.gameCode, joined.host));
                amongUs.getSession().setMyPlayerId(joined.playerId);
            }
        } else if (o instanceof NetMessage.OnHostChanged) {
            var changed = (NetMessage.OnHostChanged)o;
            amongUs.getSession().setHost(changed.id);
        }
    }

}
