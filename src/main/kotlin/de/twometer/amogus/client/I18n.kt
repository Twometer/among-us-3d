package de.twometer.amogus.client

import de.twometer.neko.res.RawLoader
import java.util.*

object I18n {

    private val props = Properties().also {
        it.load(RawLoader.openFile("strings.properties"))
    }

    operator fun get(key: String, vararg format: Any): String {
        if (!props.containsKey(key))
            return key

        return String.format(props.getProperty(key), *format)
    }

}