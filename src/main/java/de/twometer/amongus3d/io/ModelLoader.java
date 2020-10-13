package de.twometer.amongus3d.io;

import de.twometer.amongus3d.mesh.Material;
import de.twometer.amongus3d.mesh.Mesh;
import de.twometer.amongus3d.mesh.Model;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;

import java.util.List;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class ModelLoader {

    public static Model load(String path) {
        AIScene aiScene = aiImportFile("assets\\" + path, aiProcess_Triangulate);

        if (aiScene == null) {
            throw new IllegalStateException(aiGetErrorString());
        }

        Mesh mesh = Mesh.create(60000, 3)
                .withNormals();

        PointerBuffer aiMeshes = aiScene.mMeshes();
        int numMeshes = aiScene.mNumMeshes();

        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            addTo(aiMesh, mesh);
        }


        return mesh.bake(GL_TRIANGLES);
    }

    private static void addTo(AIMesh aiMesh, Mesh mesh) {

        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            mesh.putVertex(aiVertex.x(), aiVertex.y(), aiVertex.z());
        }

        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        while (aiNormals.remaining() > 0) {
            AIVector3D aiNormal = aiNormals.get();
            mesh.putNormal(aiNormal.x(), aiNormal.y(), aiNormal.z());
        }
    }

}
