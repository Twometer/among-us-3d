package de.twometer.amongus3d.render;

import de.twometer.amongus3d.core.Game;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class FrustumCulling {

    private final Matrix4f projViewMatrix;
    private final Vector4f[] frustumPlanes;

    public FrustumCulling() {
        this.projViewMatrix = new Matrix4f();
        this.frustumPlanes = new Vector4f[6];
        for (int i = 0; i < 6; i++) {
            frustumPlanes[i] = new Vector4f();
        }
    }

    public void update() {
        if (Game.instance().getViewMatrix() == null)
            return;

        projViewMatrix.set(Game.instance().getProjMatrix());
        projViewMatrix.mul(Game.instance().getViewMatrix());

        for (int i = 0; i < 6; i++)
            projViewMatrix.frustumPlane(i, frustumPlanes[i]);
    }

    public boolean insideFrustum(Vector3f vec, float radius) {
        float x0 = vec.x;
        float y0 = vec.y;
        float z0 = vec.z;
        boolean result = true;
        for (int i = 0; i < 6; i++) {
            Vector4f plane = frustumPlanes[i];
            if (plane.x * x0 + plane.y * y0 + plane.z * z0 + plane.w <= -radius) {
                result = false;
                return result;
            }
        }
        return result;
    }
}
