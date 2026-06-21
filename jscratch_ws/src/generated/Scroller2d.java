package generated;

import com.jscratch.*;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.*;

/*
 * 
 * 
 * CREDIT TO GEMINI CLI
 * 
 * NOT TO GURTAJ
 * 
 * 
 * 
 * 
 */


public class Scroller2d {
    /* slot start [id:"Scroller2d_members" tags:"inClassNotMethod"] */
    private static Scroller2d instance;
    public int x, y = 0;
    private List<BSprite> registeredSprites = new ArrayList<>();
    private List<BackgroundTile> tiles = new ArrayList<>();
    
    public static final int TILE_WIDTH = 480;
    public static final int TILE_HEIGHT = 360;

    public static Scroller2d getInstance() {
        if (instance != null) {
            return instance;
        } else {
            instance = new Scroller2d();
            return instance;
        }
    }

    public void register(BSprite s) {
        if (!registeredSprites.contains(s)) {
            registeredSprites.add(s);
        }
    }

    public Scroller2d() {
        /* slot start [id:"Scroller2d_init" tags:"inInstanceMethod"] */
        Stage stage = Stage.getInstance();

        // Initialize 9 tiles (3x3 grid)
        for (int i = 0; i < 9; i++) {
            BackgroundTile tile = new BackgroundTile("Tile" + i);
            
            // Add costumes for nearby tiles
            // IMPORTANT: Use (240, 180) as the center for a 480x360 tile
            for (int tx = -1; tx <= 1; tx++) {
                for (int ty = -1; ty <= 1; ty++) {
                    tile.addCostume(new Costume(tx + "," + ty, "jscratch_ws/assets/" + tx + "," + ty + ".png", 240, 180));
                }
            }
            tile.addCostume(new Costume("err", "jscratch_ws/assets/error.png", 240, 180));
            
            tiles.add(tile);
            stage.addSprite(tile);
            // Tiles should be at the back
            stage.goToBack(tile);
        }
        
        update();
        /* slot end [id:"Scroller2d_init"] */
    }

    public void update() {
        // Calculate the world coordinates of the tile the camera is currently over.
        // Rounding to nearest tile ensures the 3x3 grid stays roughly centered.
        int tcx = (int) Math.round((double)-x / TILE_WIDTH);
        int tcy = (int) Math.round((double)-y / TILE_HEIGHT);
        
        int tileIdx = 0;
        // Arrange 9 tiles in a 3x3 grid centered around (tcx, tcy)
        for (int dy = 1; dy >= -1; dy--) {
            for (int dx = -1; dx <= 1; dx++) {
                int worldX = tcx + dx;
                int worldY = tcy + dy;
                
                BackgroundTile tile = tiles.get(tileIdx++);
                
                // Screen Position = (World Position * Tile Size) + Camera Offset
                // Since we used (240, 180) as the costume center, goTo(0,0) centers it on stage.
                int screenX = (worldX * TILE_WIDTH) + x;
                int screenY = (worldY * TILE_HEIGHT) + y;
                
                tile.superGoTo(screenX, screenY);
                tile.switchCostume(worldX + "," + worldY);
            }
        } 
        
        for (BSprite s : registeredSprites) {
            s.update();
        }
    }
    /* slot end [id:"Scroller2d_members"] */
}
