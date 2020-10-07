package de.twometer.amongus3d.io;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class ResourceLoader {

    /**
     * Loads a string from a file embedded in the jar resources
     *
     * @param path The path to the file
     * @return The contents of the file
     * @throws IOException Will be thrown if the file does not exist, or the reading fails for any other reason.
     */
    public static String loadString(String path) throws IOException {
        BufferedReader reader = new BufferedReader(openReader(path));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
            builder.append(line).append("\n");
        return builder.toString();
    }

    public static byte[] readData(String path) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream reader = openStream(path);
        byte[] buf = new byte[8192];
        int read;
        while ((read = reader.read(buf)) > 0)
            outputStream.write(buf, 0, read);
        return outputStream.toByteArray();
    }

    /**
     * Opens a stream reader to a file embedded in the jar resources
     *
     * @param path The path to the file
     * @return A reader on the content stream of the file
     * @throws IOException Will be thrown if the file does not exist
     */
    public static InputStreamReader openReader(String path) throws IOException {
        return new InputStreamReader(openStream(path), StandardCharsets.UTF_8);
    }

    /**
     * Loads an image from a file embedded in the jar resources
     *
     * @param path The path to the image
     * @return The image
     * @throws IOException Will be thrown if the file does not exist, or the image decoding fails
     */
    public static BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(openStream(path));
    }

    private static InputStream openStream(String path) throws IOException {
        return new FileInputStream("assets\\" + path);
    }

}
