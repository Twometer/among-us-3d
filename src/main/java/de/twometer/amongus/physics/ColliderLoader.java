package de.twometer.amongus.physics;

import de.twometer.neko.res.ModelLoader;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;

import java.nio.IntBuffer;
import java.util.ArrayList;

public class ColliderLoader {

    private static final String COLLIDER_NAME = "Collider.obj";

    public static Collider load() {
        var segments = new ArrayList<LineSegment>();
        var aiScene = ModelLoader.loadRaw(COLLIDER_NAME);

        PointerBuffer aiMeshes = aiScene.mMeshes();
        int numMeshes = aiScene.mNumMeshes();

        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            AIVector3D.Buffer aiVertices = aiMesh.mVertices();

            AIFace.Buffer aiFaces = aiMesh.mFaces();
            for (AIFace face : aiFaces) {
                IntBuffer buffer = face.mIndices();

                AIVector3D vec0 = aiVertices.get(buffer.get(0));
                AIVector3D vec1 = aiVertices.get(buffer.get(1));
                LineSegment line = new LineSegment(new Vector3f(vec0.x(), 0, vec0.z()), new Vector3f(vec1.x(), 0, vec1.z()));
                segments.add(line);
            }
        }

        return new Collider(segments);
    }

}
