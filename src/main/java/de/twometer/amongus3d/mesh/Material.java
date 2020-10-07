package de.twometer.amongus3d.mesh;

import org.joml.Vector3f;

public class Material {

    private String texture;

    private Vector3f diffuseColor;

    public Material(String texture, Vector3f diffuseColor) {
        this.texture = texture;
        this.diffuseColor = diffuseColor;
    }

    public String getTexture() {
        return texture;
    }

    public Vector3f getDiffuseColor() {
        return diffuseColor;
    }
}
