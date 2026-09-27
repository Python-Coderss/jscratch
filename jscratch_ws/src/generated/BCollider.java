package generated;

import java.awt.Rectangle;

/**
* A simple world-space collider.
* Used to define areas where sprites cannot walk (walls, obstacles).
*
* <p>Call graph: created per wall by {@code FloorManager.addWall} into
* {@code Scroller2d} (borders + interior); tested by
* {@code Scroller2d.isColliding}, which serves {@code BSprite.willCollide /
* tryMove}, {@code EnemyManager} spawn checks, and spell wall stops.
*/
public class BCollider {
    public int x, y, width, height;

    public BCollider(int x, int y, int width, int height) {
        /* block comment [block:"BCollider_block_1" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"BCollider_block_1" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.x = x;
        /* block end [id:"BCollider_block_1"] */
        /* block comment [block:"BCollider_block_2" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"BCollider_block_2" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.y = y;
        /* block end [id:"BCollider_block_2"] */
        /* block comment [block:"BCollider_block_3" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"BCollider_block_3" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.width = width;
        /* block end [id:"BCollider_block_3"] */
        /* block comment [block:"BCollider_block_4" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"BCollider_block_4" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.height = height;
        /* block end [id:"BCollider_block_4"] */
    }

    /**
    * Checks if a rectangle overlaps with this collider.
    */
    public boolean intersects(int ox, int oy, int ow, int oh) {
        /* block comment [block:"BCollider_block_5" text:"Returns a value or ends the current method."] */
        /* block start [id:"BCollider_block_5" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return x < ox + ow && x + width > ox && y < oy + oh && y + height > oy;
        /* block end [id:"BCollider_block_5"] */
    }

    /**
    * Helper to get a Java Rectangle for visualization if needed.
    */
    public Rectangle getBounds() {
        /* block comment [block:"BCollider_block_6" text:"Returns a value or ends the current method."] */
        /* block start [id:"BCollider_block_6" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return new Rectangle(x, y, width, height);
        /* block end [id:"BCollider_block_6"] */
    }
}
