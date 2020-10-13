package de.twometer.amongus3d.io;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.mesh.*;
import de.twometer.amongus3d.model.world.AnimationType;
import de.twometer.amongus3d.model.world.Room;
import de.twometer.amongus3d.model.world.TaskType;
import de.twometer.amongus3d.model.world.ToolType;
import de.twometer.amongus3d.obj.*;
import de.twometer.amongus3d.util.Log;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class MapLoader {

    private static GameObject buildObject(String name, Renderable model, List<GameObject> registeredObjects) {
        if (!name.contains("_"))
            return new StaticGameObject(name, model);

        String[] args = name.split("_");

        switch (args[0]) {
            case "ANIM": {
                AnimationType type = AnimationType.parse(args[1]);
                return new StaticAnimGameObject(name, model, type);
            }
            case "TASK": {
                Room room = Room.parse(args[1]);
                TaskType taskType = TaskType.parse(args[2]);
                String ext = args[3];

                if (ext.equals("FX")) {
                    for (GameObject object : registeredObjects) {
                        if (object instanceof TaskGameObject) {
                            TaskGameObject task = (TaskGameObject) object;
                            if (task.getTaskType() == taskType && task.getRoom() == room) {
                                task.setFxModel(model);
                                return null;
                            }
                        }
                    }
                    Log.w("Dangling FX model: " + name);
                }

                return new TaskGameObject(name, model, Room.parse(args[1]), TaskType.parse(args[2]));
            }
            case "TOOL":
                return new ToolGameObject(name, model, Room.parse(args[1]), Enum.valueOf(ToolType.class, args[2]));
            case "VENT":
                try {
                    int id = Integer.parseInt(args[2]);
                    return new VentGameObject(name, model, Room.parse(args[1]), id);
                } catch (Exception e) {
                    return new VentGameObject(name, model, Room.parse(args[1]), 1);
                }
            case "DOOR":
                Room room = Room.parse(args[1]);
                int doorId = Integer.parseInt(args[2]);
                int otherDoorId = doorId % 2 == 0 ? doorId - 1 : doorId + 1;

                for (GameObject object : registeredObjects) {
                    if (object instanceof DoorGameObject) {
                        DoorGameObject door = (DoorGameObject) object;
                        if (door.getRoom() == room && door.getFirstHalfId() == otherDoorId) {
                            door.setSecondHalf(model);
                            return null;
                        }
                    }
                }

                return new DoorGameObject(name, room, model, doorId);
        }

        return new StaticGameObject(name, model);
    }


    public static List<GameObject> loadMap(String path) {
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

            AIString matname = AIString.calloc();
            aiGetMaterialString(aiMaterial, AI_MATKEY_NAME, 0, 0, matname);

            AIColor4D color = AIColor4D.create();
            aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color);

            Vector3f dcolor = new Vector3f(color.r(), color.g(), color.b());

            materials.add(new Material(matname.dataString(), jtp, dcolor));
            if (jtp.length() == 0)
                continue;
            Game.instance().getTextureProvider().getTexture(jtp);
            texpath.free();
            matname.free();
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
            Renderable model = models.size() == 1 ? models.get(0) : new CompositeModel(models);
            GameObject object = buildObject(name, model, gameObjects);
            if (object != null) {
                Log.d("Building game object " + object + " from " + models.size() + " chunks");
                gameObjects.add(object);
            }
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
            mesh.putTexCoord(aiTexCoord.x(), 1 - aiTexCoord.y());
        }

        Model model = mesh.bake(GL_TRIANGLES);
        model.setMaterial(mats.get(aiMesh.mMaterialIndex()));
        return model;
    }

}
