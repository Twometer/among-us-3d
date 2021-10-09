package de.twometer.amogus.gui

import de.twometer.amogus.client.AmongUsClient
import de.twometer.neko.gui.Page

open class BasePage(path: String) : Page(path) {

    override fun blocksGameInput(): Boolean {
        return true
    }

    override fun close() {
        if (AmongUsClient.guiManager.page == this)
            PageManager.goBack()
    }

    protected fun runOnUiThread(runnable: Runnable) = AmongUsClient.mainScheduler.runNow(runnable)

    protected fun showLoading(msg: String) {
        call("showDialog", "loading")
        setElementText("loadingMessage", msg)
    }

    protected fun hideLoading() {
        call("hideDialog", "loading")
    }

    protected fun showError(msg: String) {
        hideLoading()
        call("showDialog", "error")
        setElementText("errorMessage", msg)
    }
}