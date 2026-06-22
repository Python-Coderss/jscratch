package generated;

import com.jscratch.*;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.*;

/**
 * Manages the world scrolling, background tiles, and the minimap system.
 */
public class Scroller2d {
    private static Scroller2d instance;
    
    // World scrolling offset (typically -playerPos)
    public int x, y = 0;
    
    private List<BSprite> registeredSprites = new ArrayList<>();
    private List<BackgroundTile> tiles = new ArrayList<>();
    private List<MinimapTile> miniTiles = new ArrayList<>();
    private List<BCollider> colliders = new ArrayList<>();
    private List<Sprite> floors = new ArrayList<>();
    
    private Sprite minimapBg;
    private Sprite minimapBorder;

    public void addCollider(int x, int y, int w, int h) {
        colliders.add(new BCollider(x, y, w, h));
    }

    public boolean isColliding(int wx, int wy, int ww, int wh) {
        for (BCollider c : colliders) {
            if (c.intersects(wx - ww/2, wy - wh/2, ww, wh)) return true;
        }
        return false;
    }

    public void addFloor(Sprite f) {
        floors.add(f);
        Stage.getInstance().addSprite(f);
        Stage.getInstance().goToBack(f);
    }

    public List<BSprite> getRegisteredSprites() {
        return registeredSprites;
    }

    public static Scroller2d getInstance() {
        if (instance != null) {
            return instance;
        } else {
            instance = new Scroller2d();
            return instance;
        }
    }

    /**
     * Registers a sprite to be managed by the scroller and displayed on the minimap.
     */
    public void register(BSprite s, MinimapSprite m) {
        if (!registeredSprites.contains(s)) {
            registeredSprites.add(s);
        }
    }

    public Scroller2d() {
        Stage stage = Stage.getInstance();

        // Ensure the UI updates immediately when the stage is resized
        stage.whenScaleChanged((old, newScale) -> {
            update();
        });

        // 1. Initialize Main World Tiles (3x3 grid)
        for (int i = 0; i < 9; i++) {
            BackgroundTile tile = new BackgroundTile("Tile" + i);
            addTileCostumes(tile);
            tiles.add(tile);
            stage.addSprite(tile);
            stage.goToBack(tile);
        }

        // 2. Initialize Minimap Background
        minimapBg = new Sprite("MinimapBG");
        minimapBg.addCostume(new Costume("bg", "jscratch_ws/assets/minimap_bg.png", MinimapConfig.WIDTH/2, MinimapConfig.HEIGHT/2));
        minimapBg.goTo(MinimapConfig.CENTER_X, MinimapConfig.CENTER_Y);
        stage.addSprite(minimapBg);

        // 3. Initialize Minimap Terrain Tiles
        for (int i = 0; i < MinimapConfig.TOTAL_MINI_TILES; i++) {
            MinimapTile mTile = new MinimapTile("MTile" + i);
            addTileCostumes(mTile);
            miniTiles.add(mTile);
            stage.addSprite(mTile);
        }

        // 4. Initialize Minimap Border (Sits on top of everything)
        minimapBorder = new Sprite("MinimapBorder");
        // Center for 168x126 image is 84, 63
        minimapBorder.addCostume(new Costume("border", "jscratch_ws/assets/minimap_border.png", 84, 63));
        minimapBorder.goTo(MinimapConfig.CENTER_X, MinimapConfig.CENTER_Y);
        stage.addSprite(minimapBorder);
        
        update();
    }

    /**
     * Loads the set of possible costumes for a tile sprite.
     */
    private void addTileCostumes(Sprite s) {
        for (int tx = -1; tx <= 1; tx++) {
            for (int ty = -1; ty <= 1; ty++) {
                String id = tx + "," + ty;
                String path = "jscratch_ws/assets/" + id + ".png";
                s.addCostume(ResourcePool.getCostume(id, path, 240, 180));
            }
        }
        s.addCostume(ResourcePool.getCostume("err", "jscratch_ws/assets/error.png", 240, 180));
    }

    /**
     * Main update loop for positioning all world and UI elements.
     */
    public void update() {
        Stage stage = Stage.getInstance();
        double displayScale = stage.getScaleFactor();

        // Calculate which tile the camera (player) is currently in
        int tcx = (int) Math.round((double)-x / MinimapConfig.WORLD_TILE_W);
        int tcy = (int) Math.round((double)-y / MinimapConfig.WORLD_TILE_H);
        
        // --- Update Main World Tiles ---
        int tileIdx = 0;
        for (int dy = 1; dy >= -1; dy--) {
            for (int dx = -1; dx <= 1; dx++) {
                int worldX = tcx + dx;
                int worldY = tcy + dy;
                
                BackgroundTile tile = tiles.get(tileIdx++);
                
                int screenX = (int)(((worldX * MinimapConfig.WORLD_TILE_W) + x) * displayScale);
                int screenY = (int)(((worldY * MinimapConfig.WORLD_TILE_H) + y) * displayScale);
                
                tile.superGoTo(screenX, screenY);
                tile.setScaleX(displayScale);
                tile.setScaleY(displayScale);
                tile.switchCostume(worldX + "," + worldY);
            }
        }
        
        // --- Update Minimap Layers ---
        
        // 1. Background
        if (minimapBg != null) {
            minimapBg.goTo(MinimapConfig.CENTER_X * displayScale, MinimapConfig.CENTER_Y * displayScale);
            minimapBg.setScaleX(displayScale);
            minimapBg.setScaleY(displayScale);
            stage.goToFront(minimapBg);
        }

        // 2. Terrain Tiles
        int miniIdx = 0;
        int r = MinimapConfig.RENDER_RADIUS_TILES;
        for (int dy = r; dy >= -r; dy--) {
            for (int dx = -r; dx <= r; dx++) {
                int worldX = tcx + dx;
                int worldY = tcy + dy;
                
                MinimapTile mTile = miniTiles.get(miniIdx++);
                mTile.update(worldX * MinimapConfig.WORLD_TILE_W, worldY * MinimapConfig.WORLD_TILE_H, -x, -y);
                
                mTile.setScaleX(MinimapConfig.SCALE * displayScale);
                mTile.setScaleY(MinimapConfig.SCALE * displayScale);
                mTile.goTo(mTile.getX() * displayScale, mTile.getY() * displayScale);
                
                mTile.switchCostume(worldX + "," + worldY);
                stage.goToFront(mTile);
            }
        }
        
        // 3. Icons (Players, NPCs) - These must go ABOVE the terrain tiles
        for (BSprite s : registeredSprites) {
            s.update();
        }

        // 5. Floors (World Background objects)
        for (Sprite f : floors) {
            if (f instanceof FloorSprite) {
                ((FloorSprite)f).update();
            }
        }

        // 4. Border (Always on top of icons)
        if (minimapBorder != null) {
            minimapBorder.goTo(MinimapConfig.CENTER_X * displayScale, MinimapConfig.CENTER_Y * displayScale);
            minimapBorder.setScaleX(displayScale);
            minimapBorder.setScaleY(displayScale);
            stage.goToFront(minimapBorder);
        }
    }
}
