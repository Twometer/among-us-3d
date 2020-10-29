package de.twometer.amongus.io;

import com.google.gson.Gson;
import de.twometer.neko.util.Log;

import java.io.*;

public class FileSystem {

    private final Gson gson = new Gson();

    private File baseDirectory;

    public void initialize() {
        baseDirectory = new File(System.getProperty("user.home"), "Among Us 3D");
        if (!baseDirectory.exists()) {
            var created = baseDirectory.mkdirs();
            if (!created)
                Log.e("Failed to create config directory");
        }
    }

    public void save(String name, Object o) {
        try {
            var file = getFile(name);
            var writer = new FileWriter(file);
            gson.toJson(o, writer);
            writer.close();
        } catch (IOException e) {
            Log.e("Saving " + name + " failed", e);
        }
    }

    public <T> T load(String name, Class<T> clazz, T defaultValue) {
        var result = load(name, clazz);
        if (result == null) return defaultValue;
        else return result;
    }

    public <T> T load(String name, Class<T> clazz) {
        try {
            var file = getFile(name);
            return gson.fromJson(new FileReader(file), clazz);
        } catch (FileNotFoundException e) {
            Log.e("Reading " + name + " failed", e);
            return null;
        }
    }

    private File getFile(String name) {
        return new File(baseDirectory, name);
    }

}
