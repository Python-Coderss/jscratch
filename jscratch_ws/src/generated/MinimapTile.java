package generated;

import com.jscratch.*;

/**
 * Represents a terrain tile rendered on the minimap.
 */
public class MinimapTile extends Sprite {
    public MinimapTile(String name) {
        super(name);
        // Base scale matches the minimap configuration
        this.setScaleX(MinimapConfig.SCALE);
        this.setScaleY(MinimapConfig.SCALE);
    }

    /**
     * Updates the tile's position on the minimap relative to the player.
     */
    public void update(int worldX, int worldY, int playerX, int playerY) {
        int relX = worldX - playerX;
        int relY = worldY - playerY;
        
        int miniRelX = (int)(relX * MinimapConfig.SCALE);
        int miniRelY = (int)(relY * MinimapConfig.SCALE);
        
        int screenX = MinimapConfig.CENTER_X + miniRelX;
        int screenY = MinimapConfig.CENTER_Y + miniRelY;
        
        // Culling: Slightly larger bounds for tiles to ensure they fill the minimap window completely
        int paddingX = (int)(MinimapConfig.WORLD_TILE_W * MinimapConfig.SCALE / 2);
        int paddingY = (int)(MinimapConfig.WORLD_TILE_H * MinimapConfig.SCALE / 2);

        if (Math.abs(miniRelX) <= MinimapConfig.WIDTH / 2 + paddingX && 
            Math.abs(miniRelY) <= MinimapConfig.HEIGHT / 2 + paddingY) {
            super.goTo(screenX, screenY);
            show();
        } else {
            hide();
        }
    }
}
