package de.twometer.amogus.player

import de.twometer.amogus.res.SmlLoader
import de.twometer.neko.util.MathExtensions.clone
import org.joml.Vector3f
import kotlin.math.abs

object BoundsTesterLoader {

    fun load(path: String): BoundsTester {
        val sml = SmlLoader.load(path)
        val bounds = HashMap<String, List<LineSegment>>()
        sml.forEach {
            val name = it.readString()
            val segments = ArrayList<LineSegment>()
            while (it.hasRemaining) {
                val a = it.readVec2()
                val b = it.readVec2()
                segments.add(LineSegment(Vector3f(a.x, 0f, a.y), Vector3f(b.x, 0f, b.y)))
            }
            bounds[name] = segments
        }

        return BoundsTester(bounds)
    }

}

class BoundsTester(private val bounds: Map<String, List<LineSegment>>) {

    fun findContainingBounds(position: Vector3f): String? {
        bounds.forEach {
            var intersections = 0
            it.value.forEach { line ->
                if (rayHit(position, line))
                    intersections++
            }
            if (intersections % 2 == 1)
                return it.key
        }
        return null
    }

    private fun rayHit(ray: Vector3f, line: LineSegment): Boolean {
        ray.y = 0f
        line.a.y = 0f
        line.b.y = 0f

        val v1 = ray.clone().sub(line.a)
        val v2 = line.b.clone().sub(line.a)
        val v3 = Vector3f(-ray.z, 0f, ray.x)
        val dot = v2.dot(v3)
        if (abs(dot) < 0.00001f)
            return false

        val t1 = v2.cross(v1).div(dot)
        val t2 = (v1.dot(v3)) / dot

        return t1.x >= 0.0 && t1.y >= 0.0 && (t2 in 0.0..1.0)
    }

}