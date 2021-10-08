package de.twometer.amogus.model

import de.twometer.neko.scene.Color

private fun fromHey(hex: String): Color {
    val c = java.awt.Color.decode(hex)
    return Color(c.red / 255.0f, c.green / 255.0f, c.blue / 255.0f, c.alpha / 255.0f)
}

enum class PlayerColor(val color: Color) {
    Red(fromHey("#FF0000")),
    DarkRed(fromHey("#540000")),
    Green(fromHey("#006836")),
    Lime(fromHey("#00FF00")),
    Blue(fromHey("#0000FF")),
    LightBlue(fromHey("#3287FF")),
    Cyan(fromHey("#00FFFA")),
    Yellow(fromHey("#FFB100")),
    Orange(fromHey("#FF6100")),
    Pink(fromHey("#FF00D1")),
    Purple(fromHey("#6400B7")),
    Brown(fromHey("#612500")),
    White(fromHey("#ffffff")),
    Black(fromHey("#000000")),
    Gray(fromHey("#494949"));
}