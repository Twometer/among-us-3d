package de.twometer.amongus3d.server;

import java.util.Random;

public class SessionCodeGenerator {

    private static final char START = 'A';
    private static final char END = 'Z';

    private static final Random r = new Random();

    public static String generate() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            builder.append((char) ((int) START + r.nextInt((int) (END - START))));
        }
        builder.setCharAt(builder.length() - 1, 'Q');
        return builder.toString();
    }

}
