package de.twometer.amogus.concurrency

import java.util.concurrent.atomic.AtomicInteger

class ScheduledTask(val time: Long, val runnable: Runnable) {

    companion object {
        private val counter = AtomicInteger(0)
    }

    val id: Int = counter.incrementAndGet()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScheduledTask

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

}

open class Scheduler {
    private val tasks = ArrayList<ScheduledTask>()

    fun runNow(runnable: Runnable): ScheduledTask = runLater(0, runnable)

    fun runLater(delay: Long, runnable: Runnable): ScheduledTask {
        val task = ScheduledTask(System.currentTimeMillis() + delay, runnable)
        synchronized(tasks) {
            tasks.add(task)
        }
        return task
    }

    fun cancel(task: ScheduledTask) {
        synchronized(tasks) {
            tasks.remove(task)
        }
    }

    fun update() {
        val executableTasks = ArrayList<ScheduledTask>()
        synchronized(tasks) {
            val iterator = tasks.iterator()
            while (iterator.hasNext()) {
                val task = iterator.next()
                if (task.time < System.currentTimeMillis()) {
                    executableTasks.add(task)
                    iterator.remove()
                }
            }
        }
        executableTasks.forEach { it.runnable.run() }
    }

}