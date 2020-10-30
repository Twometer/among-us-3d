package de.twometer.amongus.physics;

import org.joml.Vector3f;

import java.util.List;

public class Collider {

    private static final float PLAYER_RADIUS = 0.2f;
    private static final float PLAYER_RADIUS_SQ = PLAYER_RADIUS * PLAYER_RADIUS;

    private final List<LineSegment> lineSegments;

    public Collider(List<LineSegment> lineSegments) {
        this.lineSegments = lineSegments;
    }

    public void updatePosition(Vector3f pos) {
        Vector3f player2d = new Vector3f(pos.x, 0, pos.z);
        for (LineSegment line : lineSegments) {
            Vector3f closestPoint = getClosestPointOnSegment(line, player2d);
            float distSq = closestPoint.distanceSquared(player2d);
            if (distSq < PLAYER_RADIUS_SQ) {
                float dist = (float) Math.sqrt(distSq);

                Vector3f pushVector = new Vector3f(player2d.x - closestPoint.x, 0, player2d.z - closestPoint.z);
                pushVector = pushVector.normalize(PLAYER_RADIUS - dist);

                pos.x += pushVector.x;
                pos.z += pushVector.z;
            }
        }
    }

    private static Vector3f getClosestPointOnSegment(LineSegment line, Vector3f player) {
        return getClosestPointOnSegment(line.getA().x, line.getA().z, line.getB().x, line.getB().z, player.x, player.z);
    }

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
