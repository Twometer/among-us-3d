package de.twometer.amogus.server

import de.twometer.amogus.model.BaseSession

class ServerSession(code: String, host: Int) : BaseSession<PlayerClient>(code, host) {
}