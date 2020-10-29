package de.twometer.amongus.util;

import com.google.gson.Gson;
import de.twometer.neko.util.CrashHandler;

import java.io.IOException;
import java.io.InputStreamReader;

public class Config {

    private static Config currentConfig = null;

    private String appVersion;
    private int protoVersion;
    private String serverIp;
    private int serverPort;

    public static Config get() {
        if (currentConfig == null) {
            var stream = Config.class.getClassLoader().getResourceAsStream("config.json");
            if (stream == null) {
                throw new RuntimeException("Cannot read the app config");
            }

            var gson = new Gson();
            currentConfig = gson.fromJson(new InputStreamReader(stream), Config.class);
        }
        return currentConfig;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public int getProtoVersion() {
        return protoVersion;
    }

    public String getServerIp() {
        return serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }
}
