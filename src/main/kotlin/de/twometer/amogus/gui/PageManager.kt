package de.twometer.amogus.gui

import de.twometer.neko.core.NekoApp
import de.twometer.neko.gui.Page
import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger {}

object PageManager {

    private val stack = Stack<Page>()

    private fun loadPage(page: Page) {
        logger.debug { "Loading page $page" }
        NekoApp.the.guiManager.page = page
    }

    fun push(page: Page) {
        NekoApp.the.guiManager.page?.let { stack.push(it) }
        loadPage(page)
    }

    fun goBack() {
        if (stack.empty())
            return
        loadPage(stack.pop())
    }

    fun clearHistory() {
        stack.clear()
    }

    fun overwrite(page: Page) {
        push(page)
        stack.clear()
    }

}