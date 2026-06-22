package generated;

import com.jscratch.*;

/**
 * Represents an icon on the minimap (Player, NPC, etc.)
 */
public class MinimapSprite extends Sprite {
    
    public MinimapSprite(String name) {
        super(name);
    }

    /**
     * Updates the icon's position on the minimap relative to the player.
     */
    public void update(int worldX, int worldY, double direction, int playerX, int playerY) {
        Stage stage = Stage.getInstance();
        double displayScale = stage.getScaleFactor();

        // Calculate world distance from player
        int relX = worldX - playerX;
        int relY = worldY - playerY;
        
        // Convert world distance to minimap pixels
        int miniRelX = (int)(relX * MinimapConfig.SCALE);
        int miniRelY = (int)(relY * MinimapConfig.SCALE);
        
        // Final screen position (Relative to Minimap Center * Stage Scale)
        int screenX = (int)((MinimapConfig.CENTER_X + miniRelX) * displayScale);
        int screenY = (int)((MinimapConfig.CENTER_Y + miniRelY) * displayScale);
        
        // Only show if the icon is within the minimap's rectangular bounds
        boolean inBoundsX = Math.abs(miniRelX) <= MinimapConfig.WIDTH / 2;
        boolean inBoundsY = Math.abs(miniRelY) <= MinimapConfig.HEIGHT / 2;

        if (inBoundsX && inBoundsY) {
            super.goTo(screenX, screenY);
            super.pointInDirection(direction);
            
            // Icons are scaled by the stage's display scale
            super.setScaleX(0.2 * displayScale);
            super.setScaleY(0.2 * displayScale);
            
            show();
            // Ensure icons stay on top of the minimap background/tiles
            stage.goToFront(this);
        } else {
            hide();
        }
    }
}
