package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient
import de.twometer.amogus.client.PlayerGameObject
import de.twometer.amogus.client.TaskFormatter
import de.twometer.amogus.client.VentGameObject
import de.twometer.amogus.model.PlayerRole
import de.twometer.amogus.model.PlayerState
import de.twometer.amogus.net.OnTaskProgress
import de.twometer.neko.events.Events
import de.twometer.neko.events.TickEvent
import org.greenrobot.eventbus.Subscribe

class IngamePage : BasePage("Ingame.html") {

    override fun onLoaded() {
        val me = AmongUsClient.session!!.myself
        call("setImpostor", me.role == PlayerRole.Impostor)
        call("setGhost", me.state == PlayerState.Ghost)
        call("setTaskProgress", AmongUsClient.session!!.taskProgress)
        me.tasks.forEach {
            val state = when {
                it.progress == 0 -> 0
                it.isTimerRunning -> 1
                it.isCompleted -> 2
                else -> 1
            }
            call("addTask", TaskFormatter.formatTask(it), state)
        }
        Events.register(this)
    }

    override fun onUnloaded() {
        Events.unregister(this)
    }

    @Subscribe
    fun onProgressChanged(e: OnTaskProgress) {
        runOnUiThread {
            call("setTaskProgress", e.progress)
        }
    }

    @Subscribe
    fun onTick(e: TickEvent) {
        val me = AmongUsClient.session!!.myself
        val tasks = me.tasks
        for (i in tasks.indices) {
            val task = tasks[i]
            if (task.isTimerRunning)
                call("setTask", i, TaskFormatter.formatTask(task))
        }

        call("setKillCooldown", me.killCooldown)

        when (AmongUsClient.currentPickTarget) {
            is PlayerGameObject -> call("setMainAction", "Kill")
            is VentGameObject -> call("setMainAction", "Vent")
            else -> call("setMainAction", "Use")
        }
    }

    override fun blocksGameInput(): Boolean = false

    override fun close() {
        println("cannot close ma boi")
    }
}