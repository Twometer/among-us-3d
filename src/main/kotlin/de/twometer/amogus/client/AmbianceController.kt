package de.twometer.amogus.client

import de.twometer.neko.audio.SoundEngine
import de.twometer.neko.audio.SoundSource
import org.joml.Vector3f


object AmbianceController {

    private val sources = ArrayList<SoundSource>()

    fun play() {
        stop()
        addAmbiance("Engines.ogg", 9f, 0.25f, -8.15f).setRollOffFactor(7f).setGain(0.75f)
        addAmbiance("Reactor.ogg", 3.58f, 0.75f, -15.25f)
        addAmbiance("Engines.ogg", 9f, 0.25f, -22.55f).setRollOffFactor(7f).setGain(0.75f)
        addAmbiance("Security.ogg", 14.4f, 0.25f, -18.11f).setRollOffFactor(3f).setGain(0.8f)
        addAmbiance("Medbay.ogg", 20.36f, 0.0f, -19.07f).setRollOffFactor(3.5f)
        addAmbiance("Medbay.ogg", 16.86f, 0.0f, -19.07f).setRollOffFactor(3.5f)
        addAmbiance("Electrical.ogg", 19.0f, 0.25f, -9.87f)
        addAmbiance("Storage.ogg", 26.91f, 0.25f, -6.72f).setRollOffFactor(0.5f)
        addAmbiance("Comms.ogg", 33.50f, 0.625f, -1.61f)
        addAmbiance("Shields.ogg", 38.4f, 0.15f, -8.72f)
        addAmbiance("Shields.ogg", 42.0f, 0.15f, -8.20f)
        addAmbiance("Admin.ogg", 34.25f, 0.5f, -11.86f)
        addAmbiance("Weapons.ogg", 39.3f, 0.625f, -22.85f)
        addAmbiance("Cafeteria.ogg", 28.18f, 5.0f, -23.57f)
        addAmbiance("O2.ogg", 36.63f, 0.0f, -18.6f)
        addAmbiance("Hallways.ogg", 40.18f, 0.125f, -15.86f)
        addAmbiance("Hallways.ogg", 31.7f, 0.3f, -7.6f)
    }

    fun stop() {
        sources.forEach { it.stop() }
        sources.clear()
    }

    private fun addAmbiance(name: String, x: Float, y: Float, z: Float): SoundSource {
        return SoundEngine.newSource("Ambiance/$name")
            .setAbsolute(true)
            .setPosition(Vector3f(x, y, z))
            .setLooping(true)
            .play()
            .also { sources.add(it) }
    }

}