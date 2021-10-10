package de.twometer.amogus.client

import com.google.gson.Gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object WorkingDirectory {

    private val gson = Gson()
    private val basedir = File(System.getProperty("user.home"), "Among Us 3D")

    init {
        if (!basedir.exists())
            basedir.mkdirs()
    }

    fun store(filename: String, data: Any) {
        val file = getFile(filename)
        FileWriter(file).use {
            gson.toJson(data, it)
        }
    }

    fun <T> load(filename: String, clazz: Class<T>, defaultValue: T? = null): T? {
        val file = getFile(filename)
        FileReader(file).use {
            return gson.fromJson(it, clazz) ?: defaultValue
        }
    }

    private fun getFile(name: String): File = File(basedir, name)

}