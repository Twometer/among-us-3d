package de.twometer.amongus3d.ui;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.mesh.Mesh;
import de.twometer.amongus3d.mesh.Model;
import de.twometer.amongus3d.render.shaders.ShaderGuiFlat;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;

public class Starfield {

    private final List<Star> stars = new ArrayList<>();

    private static final float VEL = 1f;

    private ShaderGuiFlat shader;

    private class Star {
        float x;
        float y;
        float z;
    }

    private Model circleModel;

    public void init() {
        shader = Game.instance().getShaderProvider().getShader(ShaderGuiFlat.class);
        Mesh mesh = Mesh.create(40 * 2 + 2, 2)
                .withColors();

        float angInc = (float) (Math.PI / 40.0f);
        float cosInc = (float) Math.cos(angInc);
        float sinInc = (float) Math.sin(angInc);
        mesh.putVertex(1, 0);
        mesh.putColor(1, 1, 1);

        double xc = 1.0f;
        double yc = 0.0f;
        for (float iAng = 1; iAng < 40; iAng++) {
            float xcNew = (float) (cosInc * xc - sinInc * yc);
            yc = sinInc * xc + cosInc * yc;
            xc = xcNew;

            mesh.putVertex((float) xc, (float) yc);
            mesh.putColor(1, 1, 1);

            mesh.putVertex((float) xc, (float) -yc);
            mesh.putColor(1, 1, 1);
        }
        mesh.putVertex(-1, 0);
        mesh.putColor(1, 1, 1);
        circleModel = mesh.bake(GL_TRIANGLE_STRIP);

        regenerate();
    }

    public void regenerate() {
        stars.clear();
        int numStars = (int) (Game.instance().getWindow().getWidth() / 70.0f);
        for (int i = 0; i < numStars; i++) {
            Star star = new Star();
            star.x = (float) (Math.random() * Game.instance().getWindow().getWidth());
            star.y = (float) (Math.random() * Game.instance().getWindow().getHeight());
            star.z = (float) (Math.random());
            stars.add(star);
        }
    }

    public void render() {
        for (Star star : stars) {
            star.x += VEL * star.z;
            float scale = (star.z) * 8;
            shader.bind();
            shader.setProjectionMatrix(Game.instance().getGuiMatrix());
            shader.setTransformationMatrix(new Matrix4f().translate(star.x, star.y, 0).scale(scale, scale, 1));
            shader.setColor(new Vector4f(1, 1, 1, star.z));
            circleModel.render();

            if (star.x > Game.instance().getWindow().getWidth()) {
                star.x = 0;
                star.y = (float) (Math.random() * Game.instance().getWindow().getHeight());
                //star.z = (float) (Math.random());
            }
        }
    }

}
