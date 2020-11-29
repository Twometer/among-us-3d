package de.twometer.amongus.util;

import de.twometer.amongus.core.AmongUs;

public class UserSettings {

    public static final String FILE_NAME = "settings.json";

    private String username = "";
    private int volume = 75;
    private boolean useAO = true;
    private int aoSamples = 11;
    private boolean useBloom = true;
    private boolean useVignette = true;
    private boolean useFxaa = true;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isUseAO() {
        return useAO;
    }

    public void setUseAO(boolean useAO) {
        this.useAO = useAO;
    }

    public int getAoSamples() {
        return aoSamples;
    }

    public void setAoSamples(int aoSamples) {
        this.aoSamples = aoSamples;
    }

    public boolean isUseBloom() {
        return useBloom;
    }

    public void setUseBloom(boolean useBloom) {
        this.useBloom = useBloom;
    }

    public boolean isUseVignette() {
        return useVignette;
    }

    public void setUseVignette(boolean useVignette) {
        this.useVignette = useVignette;
    }

    public boolean isUseFxaa() {
        return useFxaa;
    }

    public void setUseFxaa(boolean useFxaa) {
        this.useFxaa = useFxaa;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void save() {
        AmongUs.get().getFileSystem().save(FILE_NAME, this);
    }

    public void copyFromJs(UserSettings other) {
        useAO = other.useAO;
        aoSamples = other.aoSamples;
        useBloom = other.useBloom;
        useVignette = other.useVignette;
        useFxaa = other.useFxaa;
        volume = other.volume;
    }
}
