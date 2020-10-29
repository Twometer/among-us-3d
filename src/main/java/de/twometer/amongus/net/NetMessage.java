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

        public Handshake() {
        }

        public Handshake(int protocolVer, String appVer) {
            this.protocolVer = protocolVer;
            this.appVer = appVer;
        }
    }

    public static class HandshakeReply {
        public enum Result {
            OK,
            TooOld,
            TooNew
        }

        public Result result;

        public HandshakeReply(Result result) {
            this.result = result;
        }

        public HandshakeReply() {
        }
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

        public SessionCreated() {
        }

        public SessionCreated(Result result, String code) {
            this.result = result;
            this.code = code;
        }
    }

    // Session joining
    public static class SessionJoin {
        public String username;
        public String code;

        public SessionJoin(String username, String code) {
            this.username = username;
            this.code = code;
        }

        public SessionJoin() {
        }
    }

    public static class SessionJoined {
        public enum Result {
            InvalidGameCode,
            InvalidUsername,
            UsernameTaken,
            LobbyFull,
            Other,
            Success
        }

        public int playerId;
        public Result result;

        public SessionJoined() {
        }

        public SessionJoined(Result result) {
            this.result = result;
        }

        public SessionJoined(int playerId, Result result) {
            this.playerId = playerId;
            this.result = result;
        }
    }

    // Player events
    public static class OnPlayerJoin {
        public int id;
        public String username;
        public PlayerColor color;

        public OnPlayerJoin() {
        }

        public OnPlayerJoin(int id, String username, PlayerColor color) {
            this.id = id;
            this.username = username;
            this.color = color;
        }
    }

    public static class OnPlayerLeave {
        public int id;

        public OnPlayerLeave() {
        }

        public OnPlayerLeave(int id) {
            this.id = id;
        }
    }

    // Customization events
    public static class ColorChange {
        public PlayerColor newColor;

        public ColorChange() {
        }

        public ColorChange(PlayerColor newColor) {
            this.newColor = newColor;
        }
    }

    public static class ColorChanged {
        public boolean ok;

        public ColorChanged() {
        }

        public ColorChanged(boolean ok) {
            this.ok = ok;
        }
    }

    public static class SessionConfigure {
        public SessionConfig config;

        public SessionConfigure() {
        }

        public SessionConfigure(SessionConfig config) {
            this.config = config;
        }
    }

    public static class SessionConfigured {
        public boolean ok;

        public SessionConfigured() {
        }

        public SessionConfigured(boolean ok) {
            this.ok = ok;
        }
    }

    public static class OnSessionUpdate {
        public SessionConfig config;

        public OnSessionUpdate() {
        }

        public OnSessionUpdate(SessionConfig config) {
            this.config = config;
        }
    }

    public static class OnPlayerUpdate {
        public int id;
        public PlayerColor color;
        public PlayerRole role;

        public OnPlayerUpdate() {
        }

        public OnPlayerUpdate(int id, PlayerColor color, PlayerRole role) {
            this.id = id;
            this.color = color;
            this.role = role;
        }
    }

    public static class PositionChange {
        public int playerId;
        public Vector3f position;
        public float rotation;

        public PositionChange() {
        }

        public PositionChange(int playerId, Vector3f position, float rotation) {
            this.playerId = playerId;
            this.position = position;
            this.rotation = rotation;
        }
    }

    // Game start/end events
    public static class StartGame {
    }

    public static class OnGameStart {
        public List<PlayerTask> tasks;

        public OnGameStart() {
        }

        public OnGameStart(List<PlayerTask> tasks) {
            this.tasks = tasks;
        }
    }

    public static class OnGameEnd {
        public PlayerRole winners;

        public OnGameEnd() {
        }

        public OnGameEnd(PlayerRole winners) {
            this.winners = winners;
        }
    }

    // Sabotages
    public static class StartSabotage {
        public Sabotage sabotage;

        public StartSabotage() {
        }

        public StartSabotage(Sabotage sabotage) {
            this.sabotage = sabotage;
        }
    }

    public static class FixSabotage {
        public Sabotage sabotage;
        public boolean fixed;

        public FixSabotage() {
        }

        public FixSabotage(Sabotage sabotage, boolean fixed) {
            this.sabotage = sabotage;
            this.fixed = fixed;
        }
    }

    public static class OnSabotageStateChanged {
        public Sabotage sabotage;
        public boolean active;
        public int duration;
        public int code;

        public OnSabotageStateChanged(Sabotage sabotage, boolean active, int duration, int code) {
            this.sabotage = sabotage;
            this.active = active;
            this.duration = duration;
            this.code = code;
        }

        public OnSabotageStateChanged() {
        }
    }

    // Tasks
    public static class CompleteTaskStage {
    }

    public static class OnTaskProgressChanged {
        public float progress;

        public OnTaskProgressChanged() {
        }

        public OnTaskProgressChanged(float progress) {
            this.progress = progress;
        }
    }

    // Emergency meetings
    public enum EmergencyCause {
        DeadBody,
        Button
    }

    public static class CallEmergency {
        EmergencyCause cause;

        public CallEmergency(EmergencyCause cause) {
            this.cause = cause;
        }

        public CallEmergency() {
        }
    }

    public static class OnEmergencyMeeting {
        public int reporterId;
        public EmergencyCause cause;

        public OnEmergencyMeeting() {
        }

        public OnEmergencyMeeting(int reporterId, EmergencyCause cause) {
            this.reporterId = reporterId;
            this.cause = cause;
        }
    }

    // Voting
    public static class Vote {
        public int playerId;

        public Vote() {
        }

        public Vote(int playerId) {
            this.playerId = playerId;
        }
    }

    public static class OnPlayerVoted {
        public int srcPlayerId;

        public OnPlayerVoted() {
        }

        public int dstPlayerId;

        public OnPlayerVoted(int srcPlayerId, int dstPlayerId) {
            this.srcPlayerId = srcPlayerId;
            this.dstPlayerId = dstPlayerId;
        }
    }

    public static class OnVoteResults {
        public Map<Integer, Integer> votes;

        public OnVoteResults(Map<Integer, Integer> votes) {
            this.votes = votes;
        }

        public OnVoteResults() {
        }
    }

    public static class OnPlayerEjected {
        public int playerId;

        public OnPlayerEjected(int playerId) {
            this.playerId = playerId;
        }

        public OnPlayerEjected() {
        }
    }

    // Venting
    public static class Vent {
        public VentAction action;
        public Location ventLocation;
        public int ventIdx;

        public Vent() {
        }

        public Vent(VentAction action, Location ventLocation, int ventIdx) {
            this.action = action;
            this.ventLocation = ventLocation;
            this.ventIdx = ventIdx;
        }
    }

    public static class OnVent {
        public int playerId;
        public VentAction ventAction;
        public Location ventLocation;
        public int ventIdx;

        public OnVent() {
        }

        public OnVent(int playerId, VentAction ventAction, Location ventLocation, int ventIdx) {
            this.playerId = playerId;
            this.ventAction = ventAction;
            this.ventLocation = ventLocation;
            this.ventIdx = ventIdx;
        }
    }

    // Killing
    public static class Kill {
        public int playerId;

        public Kill() {
        }

        public Kill(int playerId) {
            this.playerId = playerId;
        }
    }

}
