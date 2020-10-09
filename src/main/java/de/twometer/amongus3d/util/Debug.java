package de.twometer.amongus3d.util;

import com.sun.org.apache.xpath.internal.operations.Mod;
import de.twometer.amongus3d.mesh.Mesh;
import de.twometer.amongus3d.mesh.Model;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Debug {

    private boolean isActive;

    private Model crossModel;

    private final List<Vector3f> debugPositions = new ArrayList<>();

    private final List<Model> debugModels = new ArrayList<>();

    public void init() {
        float size = 0.1f;
        crossModel = Mesh.create(6, 3)
                .putVertex(0,0,size)
                .putVertex(0,0,-size)
                .putVertex(0,size,0)
                .putVertex(0,-size,0)
                .putVertex(size,0,0)
                .putVertex(-size,0,0)
                .bake(GL_LINES);
    }

    public void addDebugModel(Model model) {
        debugModels.add(model);
    }

    public void addDebugPos(Vector3f v) {
        debugPositions.add(v);
    }

    public void render() {
        if (!isActive)
            return;

        glDisable(GL_DEPTH_TEST);
        for (Model model : debugModels)
            model.render(new Matrix4f());
        for (Vector3f v : debugPositions)
            crossModel.render(new Matrix4f().translation(v));
        glEnable(GL_DEPTH_TEST);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
