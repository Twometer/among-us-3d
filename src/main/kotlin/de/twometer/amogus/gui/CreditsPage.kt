package de.twometer.amogus.gui

import de.twometer.amogus.client.StateManager
import de.twometer.amogus.model.GameState
import de.twometer.neko.Neko
import de.twometer.neko.audio.SoundEngine
import de.twometer.neko.audio.SoundSource

class CreditsPage : BasePage("Credits.html") {

    private lateinit var soundSource: SoundSource

    override fun onLoaded() {
        setElementText("nekoVersion", Neko.VERSION)
        StateManager.changeGameState(GameState.Credits)
        soundSource = SoundEngine.play("CreditsRoll.ogg")
    }

    override fun onUnloaded() {
        soundSource.stop()
        StateManager.changeGameState(GameState.Menus)
    }
}