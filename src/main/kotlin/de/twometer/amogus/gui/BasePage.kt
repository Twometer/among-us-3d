package de.twometer.amogus.gui

import de.twometer.neko.gui.Page

open class BasePage(path: String) : Page(path) {

    override fun blocksGameInput(): Boolean {
        return true
    }

}