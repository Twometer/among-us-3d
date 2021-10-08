package de.twometer.amogus.model

object Vents {

    private val connections = arrayOf(
        arrayOf(VentOpening(Location.LowerEngine), VentOpening(Location.Reactor, 1)),
        arrayOf(VentOpening(Location.UpperEngine), VentOpening(Location.Reactor, 2)),
        arrayOf(VentOpening(Location.Security), VentOpening(Location.MedBay), VentOpening(Location.Electrical)),
        arrayOf(VentOpening(Location.Shields), VentOpening(Location.Navigation, 1)),
        arrayOf(VentOpening(Location.Weapons), VentOpening(Location.Navigation, 2)),
        arrayOf(VentOpening(Location.Cafeteria), VentOpening(Location.Hallways), VentOpening(Location.Admin))
    )

    fun findVentOpenings(location: Location, num: Int): Array<VentOpening> {
        connections.forEach {
            if (it.any { o -> o.location == location && o.num == num })
                return it
        }
        throw IllegalStateException("No vents connected to $location#$num")
    }

}

data class VentOpening(val location: Location, val num: Int = 1)