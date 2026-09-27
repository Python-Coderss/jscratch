package generated;

/**
* Central configuration for the Minimap system.
* Change these values to adjust the minimap's appearance and behavior.
*
* <p>Used by: {@code Scroller2d} (tile counts/positions), {@code MinimapTile}
* and {@code MinimapSprite} (world→minimap math), {@code MinimapWalls}
* (rect projection). WORLD_TILE_W/H must match the 480x360 map tiles.
*/
public class MinimapConfig {
    // Position of the minimap center on the 480x360 stage
    // (Top-right rectangle of a 4x4 grid: X range 120 to 240, Y range 90 to 180)
    public static final int CENTER_X = 180;
    public static final int CENTER_Y = 135;

    // How much smaller the minimap is compared to the world (1/20 = 0.05)
    public static final double SCALE = 0.05;

    // The size of the minimap window on screen (1/16th of 480x360 = 120x90)
    public static final int WIDTH = 120;
    public static final int HEIGHT = 90;

    // How many world tiles to render in each direction around the player
    public static final int RENDER_RADIUS_TILES = 5;

    // Total number of tiles in the minimap grid (radius * 2 + 1)
    public static final int GRID_SIZE = (RENDER_RADIUS_TILES * 2) + 1;
    public static final int TOTAL_MINI_TILES = GRID_SIZE * GRID_SIZE;

    // World tile dimensions
    public static final int WORLD_TILE_W = 480;
    public static final int WORLD_TILE_H = 360;
}
