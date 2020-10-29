package de.twometer.amongus.net.server;

import java.util.Random;

public class CodeGenerator {

    private static final int LENGTH = 5;

    private static final char START = 'A';
    private static final char END = 'Z';

    private static final Random rand = new Random();

    public static String newGameCode() {
        var builder = new StringBuilder();
        for (int i = 0; i < LENGTH; i++)
            builder.append((char) ((int) START + rand.nextInt(END - START)));
        return builder.toString();
    }

    public static String newO2Code() {
        var builder = new StringBuilder();
        for (int i = 0; i < LENGTH; i++)
            builder.append(rand.nextInt(10));
        return builder.toString();
    }

}
