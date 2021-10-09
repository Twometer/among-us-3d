package de.twometer.amogus.gui

import de.twometer.neko.core.NekoApp
import de.twometer.neko.gui.Page
import java.util.*

object PageManager {

    private val stack = Stack<Page>()

    fun push(page: Page) {
        NekoApp.the.guiManager.page?.let { stack.push(it) }
        NekoApp.the.guiManager.page = page
    }

    fun goBack() {
        if (stack.empty())
            return
        NekoApp.the.guiManager.page = stack.pop()
    }

    fun clearHistory() {
        stack.clear()
    }

    fun overwrite(page: Page) {
        push(page)
        stack.clear()
    }

}