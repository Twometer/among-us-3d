package de.twometer.amongus3d.io;

import de.twometer.amongus3d.phys.Collider;
import de.twometer.amongus3d.phys.Line;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;

import java.nio.IntBuffer;

import static org.lwjgl.assimp.Assimp.*;

public class ColliderLoader {

    public static Collider loadCollider(String path) {
        Collider collider = new Collider();
        AIScene aiScene = aiImportFile("assets\\" + path, aiProcess_Triangulate);

        if (aiScene == null) {
            throw new IllegalStateException(aiGetErrorString());
        }

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
                Line line = new Line(new Vector3f(vec0.x(), 0, vec0.z()), new Vector3f(vec1.x(), 0, vec1.z()));
                collider.addLine(line);
            }
        }
        return collider;
    }

}
