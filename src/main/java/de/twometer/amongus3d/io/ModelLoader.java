package de.twometer.amongus3d.io;

import de.twometer.amongus3d.mesh.Mesh;
import de.twometer.amongus3d.mesh.Model;
import de.twometer.amongus3d.util.Log;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class ModelLoader {

    public static Model load(String path) {
        AIScene aiScene = aiImportFile("assets\\" + path, aiProcess_Triangulate);

        if (aiScene == null) {
            throw new IllegalStateException(aiGetErrorString());
        }

        PointerBuffer aiMeshes = aiScene.mMeshes();
        int numMeshes = aiScene.mNumMeshes();

        Mesh mesh = Mesh.create(700000, 3);

        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));

            AIVector3D.Buffer aiVertices = aiMesh.mVertices();
            while (aiVertices.remaining() > 0) {
                AIVector3D aiVertex = aiVertices.get();
                mesh.putVertex(aiVertex.x(), aiVertex.y(), aiVertex.z());
            }
        }

        Log.i("Loading " + mesh.getVertexCount() + " vertices.");

        return mesh.bake(GL_TRIANGLES);
    }

}
