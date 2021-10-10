package de.twometer.amogus.client

data class GameConfig(
    val version: Int = 2,
    var username: String = "",
    var volume: Int = 75,
    var maxFps: Int = 60,
    var aoActive: Boolean = true,
    var bloomActive: Boolean = true,
    var ssrActive: Boolean = true,
    var fxaaActive: Boolean = true,
    var vignetteActive: Boolean = true,
    var lightRenderDist: Int = 35
) {
    companion object {
        const val FILE_NAME = "settings.json"
    }

    fun set(other: GameConfig) {
        volume = other.volume
        maxFps = other.maxFps
        aoActive = other.aoActive
        bloomActive = other.bloomActive
        ssrActive = other.ssrActive
        fxaaActive = other.fxaaActive
        vignetteActive = other.vignetteActive
        lightRenderDist = other.lightRenderDist
    }
}