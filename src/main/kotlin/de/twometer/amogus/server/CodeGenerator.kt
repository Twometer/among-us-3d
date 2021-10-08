package de.twometer.amogus.server

object CodeGenerator {

    fun newSessionCode(): String {
        val characters = arrayListOf(
            'A',
            'B',
            'C',
            'D',
            'E',
            'F',
            'G',
            'H',
            'J',
            'K',
            'L',
            'O',
            'P',
            'Q',
            'R',
            'S',
            'T',
            'V',
            'W',
            'X',
            'Y',
            'Z'
        )
        characters.shuffle()
        return characters.subList(0, 5).joinToString()
    }

    fun newO2Code(): String {
        val characters = arrayListOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        characters.shuffle()
        return characters.subList(0, 5).joinToString()
    }

}