package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient
import de.twometer.amogus.client.StateManager
import de.twometer.amogus.model.*

class EjectPage(val result: EjectResult) : BasePage("Eject.html") {

    override fun onLoaded() {
        val session = AmongUsClient.session ?: return
        AmongUsClient.mainScheduler.runLater(1500) {
            val numImpostors = session.players.count { it.role == PlayerRole.Impostor && it.state == PlayerState.Alive }
            var impostorMessage = if (numImpostors == 1) "1 Impostor remains" else "$numImpostors Impostors remain"
            if (!session.config.confirmEjects) impostorMessage = ""

            when (result.type) {
                EjectResultType.Tie -> call("drawEjectMessage", "No one was ejected (Tie)", impostorMessage)
                EjectResultType.Skipped -> call("drawEjectMessage", "No one was ejected (Skipped)", impostorMessage)
                EjectResultType.Ejected -> {
                    val ejectedPlayer = session.findPlayer(result.player)!!
                    if (session.config.confirmEjects) {
                        val builder = StringBuilder()
                        builder.append(ejectedPlayer.username)
                        builder.append(" was ")
                        if (ejectedPlayer.role !== PlayerRole.Impostor) builder.append("not ")
                        builder.append(if (numImpostors > 1) "An Impostor" else "The Impostor")
                        call("drawEjectMessage", builder.toString(), impostorMessage)
                    } else {
                        call("drawEjectMessage", "${ejectedPlayer.username} was ejected.", impostorMessage)
                    }
                }
            }
        }
    }

    override fun close() {
        if (StateManager.gameState == GameState.GameOver)
        // TODO PageManager.overwrite()
        else
            PageManager.overwrite(IngamePage())
    }
}