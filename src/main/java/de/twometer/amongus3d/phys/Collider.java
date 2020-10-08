package de.twometer.amongus3d.phys;

import de.twometer.amongus3d.mesh.Mesh;
import de.twometer.amongus3d.mesh.Model;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_LINES;

public class Collider {

    private static final float PLAYER_RADIUS = 0.2f;
    private static final float PLAYER_RADIUS_SQ = PLAYER_RADIUS * PLAYER_RADIUS;

    private List<Line> lines = new ArrayList<>();

    private Model model;

    public void prepareDebugRender() {
        Mesh mesh = Mesh.create(lines.size() * 2, 3);
        for (Line line : lines) {
            mesh.putVertex(line.getA().x, 0.35f, line.getA().z);
            mesh.putVertex(line.getB().x, 0.35f, line.getB().z);
        }
        model = mesh.bake(GL_LINES);
    }

    public void renderDebug() {
        model.render();
    }

    public void addLine(Line line) {
        lines.add(line);
    }

    public void updatePlayerLocation(Vector3f player) {
        Vector3f player2d = new Vector3f(player.x, 0, player.z);
        for (Line line : lines) {
            Vector3f closestPoint = closestPoint(line, player2d);
            float distSq = closestPoint.distanceSquared(player2d);
            if (distSq < PLAYER_RADIUS_SQ) {
                float dist = (float) Math.sqrt(distSq);

                Vector3f pushVector = new Vector3f(player2d.x - closestPoint.x, 0, player2d.z - closestPoint.z);
                pushVector = pushVector.normalize(PLAYER_RADIUS - dist);

                player.x += pushVector.x;
                player.z += pushVector.z;
            }
        }
    }

    private Vector3f closestPoint(Line line, Vector3f player) {
        return getClosestPointOnSegment(line.getA().x, line.getA().z, line.getB().x, line.getB().z, player.x, player.z);
    }

    /**
     * Returns closest point on segment to point
     *
     * @param sx1 - segment x coord 1
     * @param sy1 - segment y coord 1
     * @param sx2 - segment x coord 2
     * @param sy2 - segment y coord 2
     * @param px  - point x coord
     * @param py  - point y coord
     * @return closets point on segment to point
     */
    private static Vector3f getClosestPointOnSegment(float sx1, float sy1, float sx2, float sy2, float px, float py) {
        double xDelta = sx2 - sx1;
        double yDelta = sy2 - sy1;

        if ((xDelta == 0) && (yDelta == 0)) {
            throw new IllegalArgumentException("Segment start equals segment end");
        }

        double u = ((px - sx1) * xDelta + (py - sy1) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

        final Vector3f closestPoint;
        if (u < 0) {
            closestPoint = new Vector3f(sx1, 0, sy1);
        } else if (u > 1) {
            closestPoint = new Vector3f(sx2, 0, sy2);
        } else {
            closestPoint = new Vector3f((float) (sx1 + u * xDelta), 0, (float) (sy1 + u * yDelta));
        }
        return closestPoint;
    }

}
