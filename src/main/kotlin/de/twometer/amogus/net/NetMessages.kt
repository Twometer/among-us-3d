package de.twometer.amogus.net

import com.esotericsoftware.kryo.Kryo
import de.twometer.amogus.model.*
import org.joml.Vector3f

fun registerAllNetMessages(kryo: Kryo) {
    kryo.register(HandshakeRequest::class.java)
    kryo.register(HandshakeResponse::class.java)
    kryo.register(SessionCreateRequest::class.java)
    kryo.register(SessionCreateResponse::class.java)
    kryo.register(SessionJoinRequest::class.java)
    kryo.register(SessionJoinResponse::class.java)
    kryo.register(SessionConfigureRequest::class.java)
    kryo.register(SessionConfigureResponse::class.java)
    kryo.register(ColorChangeRequest::class.java)
    kryo.register(ColorChangeResponse::class.java)

    kryo.register(OnPlayerJoin::class.java)
    kryo.register(OnPlayerLeave::class.java)
    kryo.register(OnPlayerUpdate::class.java)
    kryo.register(OnPlayerKilled::class.java)
    kryo.register(OnSessionUpdate::class.java)
    kryo.register(OnEmergencyMeeting::class.java)
    kryo.register(OnPlayerVoted::class.java)
    kryo.register(OnGameStarted::class.java)
    kryo.register(OnGameEnded::class.java)
    kryo.register(OnTaskProgress::class.java)
    kryo.register(OnPlayerMove::class.java)
    kryo.register(OnHostChanged::class.java)
    kryo.register(OnSurveillanceChanged::class.java)
    kryo.register(OnEjectionResult::class.java)

    kryo.register(CastVote::class.java)
    kryo.register(KillPlayer::class.java)
    kryo.register(StartGame::class.java)
    kryo.register(SabotageStart::class.java)
    kryo.register(SabotageFix::class.java)
    kryo.register(CompleteTaskStage::class.java)
    kryo.register(CallMeeting::class.java)
    kryo.register(ChangePosition::class.java)
    kryo.register(ChangeCameraState::class.java)
    kryo.register(RequestEjectionResult::class.java)

    kryo.register(SessionConfig::class.java)
    kryo.register(Location::class.java)
    kryo.register(TaskType::class.java)
    kryo.register(TaskStage::class.java)
    kryo.register(PlayerTask::class.java)
    kryo.register(PlayerRole::class.java)
    kryo.register(PlayerColor::class.java)
    kryo.register(EjectResult::class.java)
    kryo.register(EjectResultType::class.java)
    kryo.register(Vector3f::class.java)
    kryo.register(ArrayList::class.java)
}

// Transactions
class HandshakeRequest(val version: Int = 0)
class HandshakeResponse(val accepted: Boolean = false, val playerId: Int = 0)
class SessionCreateRequest
class SessionCreateResponse(val accepted: Boolean = false, val code: String = "")
class SessionJoinRequest(val code: String = "", val username: String = "")
class SessionJoinResponse(val accepted: Boolean = false, val reason: String = "")
class SessionConfigureRequest(val config: SessionConfig = SessionConfig())
class SessionConfigureResponse(val accepted: Boolean = false)
class ColorChangeRequest(val newColor: PlayerColor = PlayerColor.Red)
class ColorChangeResponse(val accepted: Boolean = false)

// Events
class OnPlayerJoin(val id: Int = 0)
class OnPlayerLeave(val id: Int = 0)
class OnPlayerUpdate(
    val id: Int = 0,
    val username: String = "",
    val color: PlayerColor = PlayerColor.Red,
    val role: PlayerRole = PlayerRole.Crewmate
)

class OnPlayerKilled(val id: Int = 0)
class OnSessionUpdate(val code: String = "", val host: Int = IPlayer.INVALID_PLAYER_ID, val config: SessionConfig = SessionConfig())
class OnEmergencyMeeting(val caller: Int = 0, val byButton: Boolean = false)
class OnPlayerVoted(val src: Int = 0, val dst: Int = 0)
class OnGameStarted(val tasks: List<PlayerTask> = ArrayList())
class OnGameEnded(val winners: PlayerRole = PlayerRole.Crewmate)
class OnTaskProgress(val progress: Float = 0.0f)
class OnPlayerMove(val id: Int = 0, val pos: Vector3f = Vector3f(), val rot: Float = 0f)
class OnHostChanged(val id: Int = 0)
class OnSurveillanceChanged(val surveillance: Boolean = false)
class OnEjectionResult(val result: EjectResult = EjectResult(EjectResultType.Skipped))

// Actions
class CastVote(val playerId: Int = 0)
class KillPlayer(val playerId: Int = 0)
class StartGame
class SabotageStart(val sabotage: SabotageType = SabotageType.Comms)
class SabotageFix(val sabotage: SabotageType = SabotageType.Comms, val active: Boolean = false)
class CompleteTaskStage
class CallMeeting(val byButton: Boolean = false)
class ChangePosition(val pos: Vector3f = Vector3f(), val rot: Float = 0.0f)
class ChangeCameraState(val inCams: Boolean = false)
class RequestEjectionResult