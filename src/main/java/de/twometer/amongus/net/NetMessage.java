package de.twometer.amongus.net;

import com.esotericsoftware.kryo.Kryo;
import de.twometer.amongus.model.*;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NetMessage {

    private NetMessage() {
    }

    private static final Class<?>[] classes = new Class<?>[]{
            // Messages
            Handshake.class,
            HandshakeReply.class,
            HandshakeReply.Result.class,
            SessionCreate.class,
            SessionCreated.class,
            SessionCreated.Result.class,
            SessionJoin.class,
            SessionJoined.class,
            SessionJoined.Result.class,
            OnPlayerJoin.class,
            OnPlayerLeave.class,
            ColorChange.class,
            ColorChanged.class,
            SessionConfigure.class,
            SessionConfigured.class,
            OnSessionUpdate.class,
            OnPlayerUpdate.class,
            PositionChange.class,
            StartGame.class,
            OnGameStart.class,
            OnGameEnd.class,
            StartSabotage.class,
            FixSabotage.class,
            OnSabotageStateChanged.class,
            CompleteTaskStage.class,
            OnTaskProgressChanged.class,
            EmergencyCause.class,
            CallEmergency.class,
            OnEmergencyMeeting.class,
            Vote.class,
            OnPlayerVoted.class,
            OnVoteResults.class,
            OnPlayerEjected.class,
            Vent.class,
            OnVent.class,
            Kill.class,

            // Game model
            Location.class,
            PlayerColor.class,
            PlayerRole.class,
            PlayerTask.class,
            TaskStage.class,
            TaskType.class,
            Sabotage.class,
            SessionConfig.class,

            // Basics
            ArrayList.class,
            HashMap.class,
            Vector3f.class
    };

    public static void registerAll(Kryo kryo) {
        for (Class<?> clazz : classes)
            kryo.register(clazz);
    }

    // Connection management
    public static class Handshake {
        public int protocolVer;
        public String appVer;
    }
    public static class HandshakeReply {
        public enum Result {
            OK,
            TooOld,
            TooNew
        }
        public Result result;
    }

    // Session creation
    public static class SessionCreate {
    }

    public static class SessionCreated {
        public enum Result {
            Ok,
            MatchmakerFull,
            Other
        }

        public Result result;
        public String code;
    }

    // Session joining
    public static class SessionJoin {
        public String username;
        public String code;
    }

    public static class SessionJoined {
        public enum Result {
            InvalidUsername,
            UsernameTaken,
            LobbyFull,
            Other,
            Success
        }

        public int playerId;
        public Result result;
    }

    // Player events
    public static class OnPlayerJoin {
        public int id;
        public String username;
        public PlayerColor color;
    }

    public static class OnPlayerLeave {
        public int id;
    }

    // Customization events
    public static class ColorChange {
        public PlayerColor newColor;
    }

    public static class ColorChanged {
        public boolean ok;
    }

    public static class SessionConfigure {
        public SessionConfig config;
    }

    public static class SessionConfigured {
        public boolean ok;
    }

    public static class OnSessionUpdate {
        public SessionConfig config;
    }

    public static class OnPlayerUpdate {
        public int id;
        public PlayerColor color;
        public PlayerRole role;
    }

    public static class PositionChange {
        public int playerId;
        public Vector3f position;
        public float rotation;
    }

    // Game start/end events
    public static class StartGame {
    }

    public static class OnGameStart {
        public List<PlayerTask> tasks;
    }

    public static class OnGameEnd {
        public PlayerRole winners;
    }

    // Sabotages
    public static class StartSabotage {
        public Sabotage sabotage;
    }

    public static class FixSabotage {
        public Sabotage sabotage;
        public boolean fixed;
    }

    public static class OnSabotageStateChanged {
        public Sabotage sabotage;
        public boolean active;
        public int duration;
        public int code;
    }

    // Tasks
    public static class CompleteTaskStage {
    }

    public static class OnTaskProgressChanged {
        public float progress;
    }

    // Emergency meetings
    public enum EmergencyCause {
        DeadBody,
        Button
    }

    public static class CallEmergency {
        EmergencyCause cause;
    }

    public static class OnEmergencyMeeting {
        public int reporterId;
        public EmergencyCause cause;
    }

    // Voting
    public static class Vote {
        public int playerId;
    }

    public static class OnPlayerVoted {
        public int srcPlayerId;
        public int dstPlayerId;
    }

    public static class OnVoteResults {
        public Map<Integer, Integer> votes;
    }

    public static class OnPlayerEjected {
        public int playerId;
    }

    // Venting
    public static class Vent {
        public VentAction action;
        public Location ventLocation;
        public int ventIdx;
    }

    public static class OnVent {
        public int playerId;
        public VentAction ventAction;
        public Location ventLocation;
        public int ventIdx;
    }

    // Killing
    public static class Kill {
        public int playerId;
    }

}
