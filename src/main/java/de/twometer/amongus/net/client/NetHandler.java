package de.twometer.amongus.net.client;

import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.event.SabotageEvent;
import de.twometer.amongus.game.DeadBodyGameObject;
import de.twometer.amongus.game.PlayerGameObject;
import de.twometer.amongus.gui.EmergencyPage;
import de.twometer.amongus.gui.GameEndPage;
import de.twometer.amongus.gui.SabotagePage;
import de.twometer.amongus.model.ClientSession;
import de.twometer.amongus.model.GameState;
import de.twometer.amongus.model.Player;
import de.twometer.amongus.model.Sabotage;
import de.twometer.amongus.net.NetMessage;
import de.twometer.amongus.physics.CollidingPlayerController;
import de.twometer.amongus.physics.GhostPlayerController;
import de.twometer.amongus.util.Scheduler;
import de.twometer.neko.event.Events;
import de.twometer.neko.util.Log;

public class NetHandler {

    private final NetClient client;
    private final AmongUs amongUs;

    public NetHandler(NetClient client) {
        this.client = client;
        this.amongUs = AmongUs.get();
    }

    public void handle(Object o) {
        // Log.d("Received " + o.toString());
        if (o instanceof NetMessage.OnPlayerUpdate) {
            var update = (NetMessage.OnPlayerUpdate) o;
            var player = amongUs.getSession().getPlayer(update.id);
            player.role = update.role;
            player.color = update.color;
        } else if (o instanceof NetMessage.OnPlayerLeave) {
            var leave = (NetMessage.OnPlayerLeave) o;
            amongUs.getSession().removePlayer(leave.id);
            amongUs.removeGameObjects(gameObject -> gameObject instanceof PlayerGameObject && ((PlayerGameObject) gameObject).getTrackedPlayer().id == leave.id);
        } else if (o instanceof NetMessage.OnPlayerJoin) {
            var join = (NetMessage.OnPlayerJoin) o;
            var player = new Player();
            player.id = join.id;
            player.color = join.color;
            player.username = join.username;
            amongUs.getSession().addPlayer(player);
            if (player.id != amongUs.getSession().getMyPlayerId())
                amongUs.getScheduler().run(() -> amongUs.addGameObject(new PlayerGameObject(player)));
        } else if (o instanceof NetMessage.PositionChange) {
            var change = (NetMessage.PositionChange) o;
            var player = amongUs.getSession().getPlayer(change.playerId);
            player.position = change.position;
            player.rotation = change.rotation;

            if (player.id == amongUs.getSession().getMyPlayerId()) {
                amongUs.getCamera().getPosition().set(player.position);
                amongUs.getCamera().getAngle().x = player.rotation;
                Log.d("Server teleported me to " + player.position.x + "|" + player.position.y + "|" + player.position.z);
            }
        } else if (o instanceof NetMessage.OnSessionUpdate) {
            amongUs.getSession().setConfig(((NetMessage.OnSessionUpdate) o).config);
            Log.d("Loaded session config");
        } else if (o instanceof NetMessage.SessionJoined) {
            var joined = (NetMessage.SessionJoined) o;
            if (joined.result == NetMessage.SessionJoined.Result.Success) {
                amongUs.setSession(new ClientSession(joined.gameCode, joined.host));
                amongUs.getSession().setMyPlayerId(joined.playerId);
            }
        } else if (o instanceof NetMessage.OnHostChanged) {
            var changed = (NetMessage.OnHostChanged) o;
            amongUs.getSession().setHost(changed.id);
        } else if (o instanceof NetMessage.OnGameStart) {
            var myTasks = ((NetMessage.OnGameStart) o).tasks;
            amongUs.getSession().getMyself().tasks = myTasks;
            amongUs.getSession().getMyself().emergencyMeetings = amongUs.getSession().getConfig().getEmergencyMeetings();
            amongUs.getSession().lastEmergency = System.currentTimeMillis();
            amongUs.getSession().winners = null;
            Log.d("Server assigned " + myTasks.size() + " tasks.");
        } else if (o instanceof NetMessage.OnTaskProgressChanged) {
            amongUs.getSession().taskProgress = ((NetMessage.OnTaskProgressChanged) o).progress;
        } else if (o instanceof NetMessage.OnEmergencyMeeting) {
            var meeting = (NetMessage.OnEmergencyMeeting) o;
            amongUs.getScheduler().run(() -> {
                var sound = meeting.cause == NetMessage.EmergencyCause.Button
                        ? "EmergencyMeeting.ogg"
                        : "EmergencyBody.ogg";
                amongUs.getSoundFX().play(sound);
                amongUs.getStateController().changeState(GameState.Emergency);
                amongUs.getGuiManager().showPage(new EmergencyPage(meeting.reporterId));
            });
            clearDed();
        } else if (o instanceof NetMessage.OnGameEnd) {
            var end = (NetMessage.OnGameEnd) o;
            var sess = amongUs.getSession();
            sess.winners = end.winners;
            if (amongUs.getStateController().getState() != GameState.Emergency) {
                amongUs.getScheduler().run(() -> amongUs.getGuiManager().showPage(new GameEndPage()));
            }
            amongUs.getStateController().changeState(GameState.End);
            clearDed();
            for (var player : amongUs.getSession().getPlayers()) {
                if (!player.alive) {
                    player.alive = true;
                    amongUs.getScheduler().run(() -> {
                        amongUs.addGameObject(new PlayerGameObject(player));
                    });
                }
            }
            amongUs.getCamera().getPosition().y = 0;
            amongUs.setPlayerController(new CollidingPlayerController());
        } else if (o instanceof NetMessage.Kill) {
            var kill = (NetMessage.Kill) o;
            var victim = amongUs.getSession().getPlayer(kill.playerId);
            victim.alive = false;
            if (!kill.system) {
                amongUs.getScheduler().run(() -> {
                    // Create body
                    var body = new DeadBodyGameObject(victim, victim.position);
                    amongUs.addGameObject(body);

                    // Remove player
                    amongUs.removeGameObjects(gameObject -> {
                        if (gameObject instanceof PlayerGameObject) {
                            var obj = (PlayerGameObject) gameObject;
                            return obj.getTrackedPlayer().id == victim.id;
                        }
                        return false;
                    });

                    // Was it me?
                    if (victim.id == amongUs.getSession().getMyPlayerId()) {
                        // Put player in ghost mode
                        amongUs.getCamera().getPosition().y += 1;
                        amongUs.setPlayerController(new GhostPlayerController());

                        // Play DEATH SOUND
                        amongUs.getSoundFX().play("KillMusic.ogg");
                    }
                });
            }
        } else if (o instanceof NetMessage.OnSabotageStateChanged) {
            var sab = (NetMessage.OnSabotageStateChanged) o;
            if (sab.active) {
                amongUs.getSession().currentSabotage = sab.sabotage;
                amongUs.getSession().currentSabotageCode = sab.code;
                amongUs.getSession().currentSabotageDuration = sab.duration / 1000;
                if (sab.sabotage == Sabotage.Lights)
                    amongUs.getScheduler().run(() -> {
                        for (var light : amongUs.getScene().getLights())
                            light.setOn(false);
                        amongUs.getScene().reloadLights();
                    });
            } else {
                amongUs.getScheduler().run(() -> {
                    for (var light : amongUs.getScene().getLights())
                        light.setOn(true);
                    amongUs.getScene().reloadLights();
                });
                amongUs.getSession().currentSabotage = null;
            }
            Events.post(new SabotageEvent());
        }
    }

    private void clearDed() {
        amongUs.removeGameObjects(o -> o instanceof DeadBodyGameObject);
    }

}
