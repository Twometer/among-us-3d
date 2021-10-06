package de.twometer.amogus.res

import org.joml.Vector3f

class SmlRow(private val data: List<String>) {

    private var index = 0

    val hasRemaining get() = index < data.size - 1

    fun readVec3(): Vector3f {
        val parts = readString().split(",")
        return Vector3f(parts[0].toFloat(), parts[1].toFloat(), parts[2].toFloat())
    }

    fun readFloat(): Float {
        return readString().toFloat()
    }

    fun readString(): String {
        return data[index].also { index++ }
    }

}