package de.twometer.amongus3d.model;

import com.esotericsoftware.kryo.Kryo;
import de.twometer.amongus3d.model.player.PlayerColor;
import de.twometer.amongus3d.model.player.PlayerTask;
import de.twometer.amongus3d.model.player.Role;
import de.twometer.amongus3d.model.world.Room;
import de.twometer.amongus3d.model.world.TaskDef;
import de.twometer.amongus3d.model.world.TaskType;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public final class NetMessage {

    private NetMessage() {

    }

    private static final Class<?>[] classes = new Class<?>[]
            {
                    CreateGame.class,
                    GameCreated.class,
                    JoinGame.class,
                    GameJoined.class,
                    EmergencyReport.class,
                    VoteCast.class,
                    PlayerJoined.class,
                    PlayerLeft.class,
                    PlayerEjected.class,
                    GameStarted.class,
                    GameEnded.class,
                    EnterVent.class,
                    LeaveVent.class,
                    CompleteTask.class,
                    TaskProgress.class,
                    PlayerKill.class,
                    PlayerMove.class,
                    Sabotage.class,
                    StartGame.class,
                    VotingEnd.class,

                    Role.class,
                    Vector3f.class,
                    TaskDef.class,
                    PlayerColor.class,
                    PlayerTask.class,
                    TaskDef.class,
                    Room.class,
                    TaskType.class,
                    ArrayList.class,
                    Sabotage.class
            };

    public static void registerAll(Kryo kryo) {
        for (Class<?> clazz : classes)
            kryo.register(clazz);
    }

    public static class CreateGame {
    }

    public static class GameCreated {
        public String gameId;

        public GameCreated() {
        }

        public GameCreated(String gameId) {
            this.gameId = gameId;
        }
    }

    public static class JoinGame {
        public String username;
        public String gameId;

        public JoinGame() {
        }

        public JoinGame(String username, String gameId) {
            this.username = username;
            this.gameId = gameId;
        }
    }

    public static class GameJoined {
        public boolean ok;

        public boolean host;

        public GameJoined() {
        }

        public GameJoined(boolean ok, boolean host) {
            this.ok = ok;
            this.host = host;
        }
    }

    public static class EmergencyReport {
        public String reporter;
        public boolean deathReport;
        public long voteDuration;
    }

    public static class VoteCast {
        public String srcUsername;
        public String dstUsername;
    }

    public static class PlayerJoined {
        public String username;

        public PlayerJoined() {
        }

        public PlayerJoined(String username) {
            this.username = username;
        }
    }

    public static class PlayerLeft {
        public String username;
    }

    public static class VotingEnd {
    }

    public static class PlayerEjected {
        public String username;
        public boolean confirm;
        public boolean impostor;
    }

    public static class StartGame {

    }

    public static class GameStarted {
        public long startTime;
        public Role role;
        public List<PlayerTask> tasks;
        public Vector3f position;
        public PlayerColor color;
        public List<String> impostors;
    }

    public static class GameEnded {
        public Role winner;
    }

    public static class EnterVent {
        public String username;
        public Room ventRoom;
        public int ventIdx;
    }

    public static class LeaveVent {
        public String username;
        public Room ventRoom;
        public int ventIdx;
    }

    public static class CompleteTask {
        public TaskDef task;
    }

    public static class TaskProgress {
        public float totalProgress;
    }

    public static class PlayerKill {
        public String attacker;
        public String victim;
    }

    public static class PlayerMove {
        public float x;
        public float y;
        public float z;
        public float angle;
    }

    public static class Sabotage {
        public Sabotage sabotage;
    }

}
