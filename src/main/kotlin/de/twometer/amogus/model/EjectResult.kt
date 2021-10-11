package de.twometer.amogus.model

enum class EjectResultType {
    Ejected,
    Skipped,
    Tie
}

data class EjectResult(val type: EjectResultType = EjectResultType.Skipped, val player: Int = IPlayer.INVALID_PLAYER_ID)