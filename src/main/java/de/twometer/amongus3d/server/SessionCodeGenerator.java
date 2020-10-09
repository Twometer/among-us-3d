package de.twometer.amongus3d.server;

import java.util.Random;

public class SessionCodeGenerator {

    private static final char START = 'A';
    private static final char END = 'Z';

    private static final Random r = new Random();

    public static String generate() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            if (Math.random() > 0.8) {
                builder.append('Q');
            } else {

                builder.append((char) ((int) START + r.nextInt((int)(END-START))));
            }
        }
        return builder.toString();
    }

}
