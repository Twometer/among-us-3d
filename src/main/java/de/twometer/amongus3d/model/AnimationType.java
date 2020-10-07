package de.twometer.amongus3d.model;

public enum AnimationType {
    Spin,
    Rotate;

    public static AnimationType parse(String id) {
        for (AnimationType ani : AnimationType.values())
            if (ani.name().equals(id))
                return ani;
        throw new IllegalArgumentException("Unknown animation type " + id);
    }
}
