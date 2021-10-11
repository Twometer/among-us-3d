package de.twometer.amogus.gui

import de.twometer.amogus.client.*
import de.twometer.amogus.model.PlayerRole
import de.twometer.amogus.model.PlayerState
import de.twometer.amogus.net.OnSurveillanceChanged
import de.twometer.amogus.net.OnTaskProgress
import de.twometer.neko.events.Events
import de.twometer.neko.events.KeyPressEvent
import de.twometer.neko.events.TickEvent
import org.greenrobot.eventbus.Subscribe
import org.lwjgl.glfw.GLFW

class IngamePage : BasePage("Ingame.html") {

    override fun onLoaded() {
        val me = AmongUsClient.session!!.myself
        call("setImpostor", me.role == PlayerRole.Impostor)
        call("setGhost", me.state == PlayerState.Ghost)
        call("setTaskProgress", AmongUsClient.session!!.taskProgress)
        call("setSurveillance", AmongUsClient.session!!.surveillanceActive)
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
    fun onKeyPress(e: KeyPressEvent) {
        if (e.key == GLFW.GLFW_KEY_ESCAPE) {
            runOnUiThread {
                PageManager.push(PausePage())
            }
        }
    }

    @Subscribe
    fun onProgressChanged(e: OnTaskProgress) {
        runOnUiThread {
            call("setTaskProgress", e.progress)
        }
    }

    @Subscribe
    fun onSurveillanceChange(e: OnSurveillanceChanged) {
        runOnUiThread {
            call("setSurveillance", e.surveillance)
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
            is CorpseGameObject -> call("setMainAction", "Report")
            else -> call("setMainAction", "Use")
        }
    }

    override fun blocksGameInput(): Boolean = false

    override fun close() {
        println("cannot close ma boi")
    }
}