package de.twometer.amogus.gui

import de.twometer.neko.audio.SoundEngine
import de.twometer.neko.audio.SoundSource
import de.twometer.neko.util.MathF
import java.util.concurrent.atomic.AtomicInteger

class GuiApi {

    private val counter = AtomicInteger(0)
    private val sources = HashMap<Int, SoundSource>()

    fun playSound(sound: String): Int {
        return storeSource(SoundEngine.play(sound))
    }

    fun playSoundRandom(sound: String, n: Int): Int {
        val rand = ((MathF.rand() * n) + 1).toInt()
        return storeSource(SoundEngine.play("$sound$rand.ogg"))
    }

    fun playSoundPitched(sound: String, pitch: Float): Int {
        return storeSource(SoundEngine.play(sound).setPitch(pitch))
    }

    fun playSoundLooping(sound: String): Int {
        // currently kind of glitchy so don't
        return -1; // storeSource(SoundEngine.play(sound).setLooping(true))
    }

    fun stopSource(sourceId: Int) {
        sources[sourceId]?.stop()
    }

    private fun storeSource(source: SoundSource): Int =
        counter.incrementAndGet().also { id ->
            sources[id] = source
        }

}