package de.twometer.amogus.server

import de.twometer.amogus.model.Location
import de.twometer.amogus.model.PlayerTask
import de.twometer.amogus.model.TaskStage
import de.twometer.amogus.model.TaskType
import de.twometer.amogus.model.TaskType.*
import kotlin.math.floor

object TaskGenerator {

    private val WIRING_LOCATIONS: List<Location> = listOf(
        Location.Cafeteria,
        Location.Electrical,
        Location.Navigation,
        Location.Security,
        Location.Storage
    )

    private val ACCEPT_POWER_LOCATIONS: List<Location> = listOf(
        Location.Navigation,
        Location.Security,
        Location.Reactor,
        Location.Weapons,
        Location.O2,
        Location.Comms
    )

    private val DOWNLOAD_LOCATIONS: List<Location> = listOf(
        Location.Comms,
        Location.Navigation,
        Location.Electrical,
        Location.Cafeteria,
        Location.Weapons
    )

    private val GARBAGE_LOCATIONS: List<Location> =
        listOf(Location.Storage, Location.O2, Location.Cafeteria)

    private val COMMON_TASKS: List<TaskType> = listOf(
        SwipeCard,
        FixWiring
    )

    private val SHORT_TASKS: List<TaskType> = listOf(
        EngineMgmt,
        ChartCourse,
        DivertPower,
        StabilizeSteering,
        PrimeShields,
        UnlockManifold,
        DataTransfer
    )

    private val LONG_TASKS: List<TaskType> = listOf(
        Scan,
        StartReactor,
        InspectSamples,
        ClearGarbage,
        Shoot
    )

    fun newCommonTask(): PlayerTask {
        return expand(COMMON_TASKS.getRandomItem())
    }

    fun newShortTask(): PlayerTask {
        return expand(SHORT_TASKS.getRandomItem())
    }

    fun newLongTask(): PlayerTask {
        return expand(LONG_TASKS.getRandomItem())
    }

    private fun expand(taskType: TaskType): PlayerTask {
        when (taskType) {
            SwipeCard -> return PlayerTask()
                .addStage(Location.Admin, SwipeCard)

            FixWiring -> {
                val builder = PlayerTask()
                val locations = getUniqueRandom(WIRING_LOCATIONS, 3)
                for (loc in locations) builder.addStage(loc, FixWiring)
                return builder
            }
            EngineMgmt -> return PlayerTask()
                .addStage(Location.UpperEngine, EngineMgmt)
                .addStage(Location.LowerEngine, EngineMgmt)

            ChartCourse -> return makeSingleStageTask(Location.Navigation, ChartCourse)
            DivertPower -> return PlayerTask()
                .addStage(Location.Electrical, DivertPower)
                .addStage(ACCEPT_POWER_LOCATIONS.getRandomItem(), AcceptPower)

            StabilizeSteering -> return makeSingleStageTask(
                Location.Navigation,
                StabilizeSteering
            )
            PrimeShields -> return makeSingleStageTask(Location.Shields, PrimeShields)
            UnlockManifold -> return makeSingleStageTask(Location.Reactor, UnlockManifold)
            DataTransfer -> return PlayerTask()
                .addStage(DOWNLOAD_LOCATIONS.getRandomItem(), DataTransfer)
                .addStage(Location.Admin, DataTransfer)

            Scan -> return makeSingleStageTask(Location.MedBay, Scan)
            StartReactor -> return makeSingleStageTask(Location.Reactor, StartReactor)
            InspectSamples -> return makeSingleStageTask(Location.MedBay, InspectSamples)
            ClearGarbage -> {
                val garbageLocations = getUniqueRandom(GARBAGE_LOCATIONS, 2).toTypedArray()
                return PlayerTask()
                    .addStage(garbageLocations[0], ClearGarbage)
                    .addStage(garbageLocations[1], ClearGarbage)

            }
            Shoot -> return makeSingleStageTask(Location.Weapons, Shoot)
            else -> throw IllegalArgumentException("Unknown task type $taskType")
        }
    }

    private fun makeSingleStageTask(loc: Location, type: TaskType): PlayerTask {
        return PlayerTask().addStage(loc, type)
    }

    private fun PlayerTask.addStage(loc: Location, type: TaskType): PlayerTask {
        this.stages.add(TaskStage(loc, type))
        return this
    }

    private fun <T> List<T>.getRandomItem(): T {
        return this[floor(Math.random() * size).toInt()]
    }

    private fun <T> getUniqueRandom(list: List<T>, num: Int): Set<T> {
        val set = HashSet<T>()
        do set.add(list.getRandomItem())
        while (set.size < num)
        return set
    }


}