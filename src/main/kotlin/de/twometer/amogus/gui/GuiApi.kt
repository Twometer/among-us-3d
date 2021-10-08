package de.twometer.amogus.gui

import de.twometer.neko.audio.SoundEngine
import de.twometer.neko.util.MathF

class GuiApi {

    fun playSound(sound: String) {
        SoundEngine.play(sound)
    }

    fun playSoundRandom(sound: String, n: Int) {
        val rand = ((MathF.rand() * n) + 1).toInt()
        SoundEngine.play("$sound$rand.ogg")
    }

    fun playSoundPitched(sound: String, pitch: Float) {
        SoundEngine.play(sound).setPitch(pitch)
    }

}