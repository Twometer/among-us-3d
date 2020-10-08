package de.twometer.amongus3d.mesh.shading;

public final class ShadingStrategies {

    public static final ShadingStrategy DEFAULT = new DefaultShadingStrategy();

    public static final ShadingStrategy PICK = new PickShadingStrategy();

    public static final FlatShadingStrategy FLAT = new FlatShadingStrategy();

}
