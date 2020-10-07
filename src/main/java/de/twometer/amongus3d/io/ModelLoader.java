package de.twometer.amongus3d.io;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.mesh.*;
import de.twometer.amongus3d.model.Room;
import de.twometer.amongus3d.model.TaskType;
import de.twometer.amongus3d.obj.GameObject;
import de.twometer.amongus3d.obj.StaticGameObject;
import de.twometer.amongus3d.obj.TaskGameObject;
import de.twometer.amongus3d.obj.VentGameObject;
import de.twometer.amongus3d.util.Log;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class ModelLoader {

    private static GameObject buildObject(String name, IRenderable model) {
        if (!name.contains("_"))
            return new StaticGameObject(name, model);

        String[] args = name.split("_");

        switch (args[0]) {
            case "TASK":
                return new TaskGameObject(name, model, Room.parse(args[1]), TaskType.parse(args[2]), args[3]);
            case "VENT":
                try {
                    int id = Integer.parseInt(args[2]);
                    return new VentGameObject(name, model, Room.parse(args[1]), id);
                } catch (Exception e) {
                    return new VentGameObject(name, model, Room.parse(args[1]), 1);
                }
            case "DOOR":
        }

        return new StaticGameObject(name, model);
    }


    public static List<GameObject> loadShip(String path) {
        List<GameObject> gameObjects = new ArrayList<>();
        AIScene aiScene = aiImportFile("assets\\" + path, aiProcess_Triangulate);

        if (aiScene == null) {
            throw new IllegalStateException(aiGetErrorString());
        }

        int numMaterials = aiScene.mNumMaterials();
        PointerBuffer aiMaterials = aiScene.mMaterials();
        List<Material> materials = new ArrayList<>();
        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
            AIString texpath = AIString.calloc();
            aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, texpath, (IntBuffer) null, null, null, null, null, null);
            String jtp = texpath.dataString();

            AIColor4D color = AIColor4D.create();
            aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color);

            Vector3f dcolor = new Vector3f(color.r(), color.g(), color.b());

            materials.add(new Material(jtp, dcolor));

            if (jtp.length() == 0)
                continue;
            Game.instance().getTextureProvider().getTexture(jtp);
        }

        PointerBuffer aiMeshes = aiScene.mMeshes();
        int numMeshes = aiScene.mNumMeshes();

        String currentName = "";
        List<Model> currentModels = new ArrayList<>();

        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));

            String name = aiMesh.mName().dataString();

            if (name.startsWith("OBJ_")) { // actual original object
                convertModelList(currentName, currentModels, gameObjects);

                currentName = name.substring("OBJ_".length());
            }

            currentModels.add(convert(aiMesh, materials));
        }

        convertModelList(currentName, currentModels, gameObjects);

        Log.i("Loaded " + numMaterials + " materials");

        return gameObjects;
    }

    private static void convertModelList(String name, List<Model> models, List<GameObject> gameObjects) {
        if (models.size() != 0) {
            IRenderable model = models.size() == 1 ? models.get(0) : new CompositeModel(models);
            GameObject object = buildObject(name, model);
            Log.d("Building game object " + object + " from " + models.size() + " chunks");
            gameObjects.add(object);
            models.clear();
        }
    }

    private static Model convert(AIMesh aiMesh, List<Material> mats) {
        Mesh mesh = Mesh.create(aiMesh.mNumVertices() + 100, 3)
                .withTexCoords()
                .withNormals();
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

        AIVector3D.Buffer buffer = aiMesh.mTextureCoords(0);
        while (buffer.remaining() > 0) {
            AIVector3D aiTexCoord = buffer.get();
            mesh.putTexCoord(aiTexCoord.x(), 1-aiTexCoord.y());
        }

        Model model = mesh.bake(GL_TRIANGLES);
        model.setMaterial(mats.get(aiMesh.mMaterialIndex()));
        return model;
    }

}
