package de.twometer.amogus.player

import de.twometer.amogus.res.SmlLoader
import org.joml.Vector3f
import kotlin.math.sqrt

data class LineSegment(val a: Vector3f, val b: Vector3f)

object LineColliderLoader {

    fun load(path: String): LineCollider {
        val sml = SmlLoader.load(path)
        val lines = ArrayList<LineSegment>()
        sml.forEach {
            val a = it.readVec2()
            val b = it.readVec2()
            lines.add(LineSegment(Vector3f(a.x, 0f, a.y), Vector3f(b.x, 0f, b.y)))
        }
        return LineCollider(lines)
    }

}

class LineCollider(private val lineSegments: List<LineSegment>) {

    private val playerRadius = 0.28f
    private val playerRadiusSq = playerRadius * playerRadius

    fun processPosition(pos: Vector3f) {
        val player2d = Vector3f(pos.x, 0f, pos.z)
        for (line in lineSegments) {
            val closestPoint = getClosestPointOnSegment(line, player2d)
            val distSq = closestPoint.distanceSquared(player2d)

            if (distSq < playerRadiusSq) {
                val dist = sqrt(distSq.toDouble()).toFloat()
                var pushVector = Vector3f(player2d.x - closestPoint.x, 0f, player2d.z - closestPoint.z)
                pushVector = pushVector.normalize(playerRadius - dist)
                pos.x += pushVector.x
                pos.z += pushVector.z
            }
        }
    }

    private fun getClosestPointOnSegment(line: LineSegment, player: Vector3f): Vector3f {
        return getClosestPointOnSegment(line.a.x, line.a.z, line.b.x, line.b.z, player.x, player.z)
    }

    private fun getClosestPointOnSegment(
        sx1: Float,
        sy1: Float,
        sx2: Float,
        sy2: Float,
        px: Float,
        py: Float
    ): Vector3f {
        val xDelta = (sx2 - sx1).toDouble()
        val yDelta = (sy2 - sy1).toDouble()
        require(!(xDelta == 0.0 && yDelta == 0.0)) { "Segment start equals segment end" }
        val u = ((px - sx1) * xDelta + (py - sy1) * yDelta) / (xDelta * xDelta + yDelta * yDelta)
        val closestPoint: Vector3f = when {
            u < 0 -> {
                Vector3f(sx1, 0f, sy1)
            }
            u > 1 -> {
                Vector3f(sx2, 0f, sy2)
            }
            else -> {
                Vector3f((sx1 + u * xDelta).toFloat(), 0f, (sy1 + u * yDelta).toFloat())
            }
        }
        return closestPoint
    }

}