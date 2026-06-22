package generated;

import java.awt.Rectangle;

/**
 * A simple world-space collider.
 * Used to define areas where sprites cannot walk (walls, obstacles).
 */
public class BCollider {
    public int x, y, width, height;

    public BCollider(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Checks if a rectangle overlaps with this collider.
     */
    public boolean intersects(int ox, int oy, int ow, int oh) {
        return x < ox + ow && x + width > ox && y < oy + oh && y + height > oy;
    }

    /**
     * Helper to get a Java Rectangle for visualization if needed.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
