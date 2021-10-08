package de.twometer.amogus.net

import com.esotericsoftware.kryo.Kryo


fun registerAllNetMessages(kryo: Kryo) {
    kryo.register(HandshakeRequest::class.java)
    kryo.register(HandshakeResponse::class.java)
}

class HandshakeRequest(var version: Int = 0)

class HandshakeResponse(var accepted: Boolean = false, var playerId: Int = 0)