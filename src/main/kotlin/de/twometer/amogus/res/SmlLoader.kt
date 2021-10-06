package de.twometer.amogus.res

import de.twometer.neko.res.RawLoader

object SmlLoader {

    fun load(path: String): List<SmlRow> =
        RawLoader.loadLines(path)
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("#") }
            .map { SmlRow(it.split("|")) }

}