package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient
import de.twometer.amogus.net.CallMeeting

class CallMeetingPage : BasePage("CallMeeting.html") {

    override fun onLoaded() {
        call("setCallsLeft", AmongUsClient.session!!.myself.emergencyMeetings)
    }

    fun callEmergency() {
        AmongUsClient.send(CallMeeting(true))
        AmongUsClient.session!!.myself.emergencyMeetings--
    }

}