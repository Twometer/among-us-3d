package de.twometer.amongus.model;

import de.twometer.neko.render.Color;

public enum PlayerColor {
    Red(fromHex("#FF0000")),
    DarkRed(fromHex("#660000")),
    Green(fromHex("#004200")),
    Lime(fromHex("#00FF00")),
    Blue(fromHex("#0000FF")),
    LightBlue(fromHex("#0071FF")),
    Cyan(fromHex("#00FAFF")),
    Yellow(fromHex("#FFB100")),
    Orange(fromHex("#FF6100")),
    Pink(fromHex("#FF00D1")),
    Purple(fromHex("#5700D5")),
    Brown(fromHex("#612500")),
    White(fromHex("#ffffff")),
    Black(fromHex("#000000")),
    Gray(fromHex("#3a3a3a"));


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
