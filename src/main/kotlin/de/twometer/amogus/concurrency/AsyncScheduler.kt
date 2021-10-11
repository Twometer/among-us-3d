package de.twometer.amogus.concurrency

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class AsyncScheduler : Scheduler() {

    fun start() =
        Thread {
            logger.info { "Starting asynchronous scheduler" }
            while (true) {
                update()
                Thread.sleep(20)
            }
        }.also {
            it.name = "AsyncSchedulerThread-${it.id}"
            it.isDaemon = true
        }.start()

}