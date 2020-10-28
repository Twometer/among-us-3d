package de.twometer.amongus.server;

import java.util.Random;

public class CodeGenerator {

    private static final int LENGTH = 5;

    private static final char START = 'A';
    private static final char END = 'Z';

    private static final Random rand = new Random();

    public static String generate() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) {
            builder.append((char) ((int) START + rand.nextInt(END - START)));
        }
        return builder.toString();
    }

}
