package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient
import de.twometer.amogus.client.StateManager
import de.twometer.amogus.model.GameState
import de.twometer.amogus.model.PlayerRole
import de.twometer.neko.audio.SoundEngine

class GameEndPage : BasePage("GameEnd.html") {

    override fun onLoaded() {
        val session = AmongUsClient.session ?: return
        val self = session.myself

        if (session.winners != self.role) {
            call("setDefeat")
            SoundEngine.play("Defeat.ogg")
        } else {
            SoundEngine.play("Victory.ogg")
        }

        session.players.forEach {
            call("addPlayer", it.username, it.role == PlayerRole.Impostor)
        }

        AmongUsClient.mainScheduler.runLater(10000) {
            session.taskProgress = 0f
            session.surveillanceActive = false
            PageManager.overwrite(LobbyPage())
            StateManager.changeGameState(GameState.Menus)
        }
    }

}