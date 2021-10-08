package de.twometer.amogus.server

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.Server
import de.twometer.amogus.net.HandshakeRequest
import de.twometer.amogus.net.HandshakeResponse
import de.twometer.amogus.net.registerAllNetMessages
import mu.KotlinLogging
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

private val logger = KotlinLogging.logger {}

object AmongUsServer : Server() {

    private val sessions = ConcurrentHashMap<String, ServerSession>()
    private val playerIdCounter = AtomicInteger()

    fun main() {
        registerAllNetMessages(kryo)
        start()
        bind(32783)

        addListener(object : Listener() {
            override fun received(p0: Connection?, p1: Any?) {
                handle(p0 as PlayerClient, p1!!)
            }

            override fun connected(p0: Connection?) {
                logger.info { "Client connected from ${p0!!.remoteAddressTCP}" }
            }
        })
    }

    private fun handle(client: PlayerClient, obj: Any) {
        when (obj) {
            is HandshakeRequest -> {
                client.player.id = playerIdCounter.incrementAndGet()
                client.sendTCP(HandshakeResponse(obj.version == 2, client.player.id))
                logger.info { "Player #${client.player.id} connected" }
            }
        }
    }


    override fun newConnection(): Connection {
        return PlayerClient()
    }
}