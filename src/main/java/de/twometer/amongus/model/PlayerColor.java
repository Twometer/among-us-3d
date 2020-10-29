package de.twometer.amongus.model;

import de.twometer.neko.render.Color;

public enum PlayerColor {
    Red(fromHex("#FF0000")),
    DarkRed(fromHex("#540000")),
    Green(fromHex("#006836")),
    Lime(fromHex("#00FF00")),
    Blue(fromHex("#0000FF")),
    LightBlue(fromHex("#3287FF")),
    Cyan(fromHex("#00FFFA")),
    Yellow(fromHex("#FFB100")),
    Orange(fromHex("#FF6100")),
    Pink(fromHex("#FF00D1")),
    Purple(fromHex("#6400B7")),
    Brown(fromHex("#612500")),
    White(fromHex("#ffffff")),
    Black(fromHex("#000000")),
    Gray(fromHex("#494949"));


    private final Color value;

    PlayerColor(Color value) {
        this.value = value;
    }

    public Color getValue() {
        return value;
    }

    private static Color fromHex(String hex) {
        java.awt.Color c = java.awt.Color.decode(hex);
        return new Color(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, c.getAlpha() / 255.0f);
    }

}
