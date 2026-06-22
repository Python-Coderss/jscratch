package generated;

import com.jscratch.*;

/**
 * A floor sprite that stays fixed in world coordinates.
 * Useful for adding specific floor types (lava, water, stone) in particular world locations.
 */
public class FloorSprite extends Sprite {
    public int xx, yy;
    public double localScale = 1.0;

    public FloorSprite(String name, int x, int y) {
        super(name);
        this.xx = x;
        this.yy = y;
        
        // Listen for stage scaling
        this.whenScaleChanged((old, newScale) -> {
            update();
        });
        
        // Add to scroller's floor list for management
        Scroller2d.getInstance().addFloor(this);
        update();
    }

    public void update() {
        Stage stage = Stage.getInstance();
        double displayScale = stage.getScaleFactor();
        Scroller2d scroller = Scroller2d.getInstance();

        // Position = (World position + Scroll offset) * Stage scale
        double screenX = (xx + scroller.x) * localScale * displayScale;
        double screenY = (yy + scroller.y) * localScale * displayScale;
        
        super.goTo(screenX, screenY);
        super.setScaleX(localScale * displayScale);
        super.setScaleY(localScale * displayScale);
    }
}
