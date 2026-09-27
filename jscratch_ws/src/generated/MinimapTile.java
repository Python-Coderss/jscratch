package generated;

import com.jscratch.*;

/**
* One terrain cell of the minimap (11x11 grid around the player).
*
* <p>Call graph: owned array in {@code Scroller2d} (created once at boot);
* repositioned + re-costumed every {@code Scroller2d.update} from world
* coords; culled (hidden) outside the window + tile padding. Shares tile
* costumes with the world via {@code ResourcePool}, so floor tints apply
* to both.
*/
public class MinimapTile extends Sprite {
    public MinimapTile(String name) {
        /* block comment [block:"MinimapTile_block_2" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"MinimapTile_block_2" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(name);
        /* block end [id:"MinimapTile_block_2"] */
        // Base scale matches the minimap configuration
        /* block comment [block:"MinimapTile_block_3" text:"Runs the set scale x action. Values in this block are editable inputs."] */
        /* block start [id:"MinimapTile_block_3" category:"Variables" label:"set scale x" shape:"command" docs:"Runs the set scale x action. Values in this block are editable inputs."] */
        this.setScaleX(MinimapConfig.SCALE);
        /* block end [id:"MinimapTile_block_3"] */
        /* block comment [block:"MinimapTile_block_4" text:"Runs the set scale y action. Values in this block are editable inputs."] */
        /* block start [id:"MinimapTile_block_4" category:"Variables" label:"set scale y" shape:"command" docs:"Runs the set scale y action. Values in this block are editable inputs."] */
        this.setScaleY(MinimapConfig.SCALE);
        /* block end [id:"MinimapTile_block_4"] */
    }

    /**
    * Updates the tile's position on the minimap relative to the player.
    */
    public void update(int worldX, int worldY, int playerX, int playerY) {
        /* block comment [block:"MinimapTile_block_5" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapTile_block_5" category:"Variables" label:"set relX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int relX = worldX - playerX;
        /* block end [id:"MinimapTile_block_5"] */
        /* block comment [block:"MinimapTile_block_6" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapTile_block_6" category:"Variables" label:"set relY" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int relY = worldY - playerY;
        /* block end [id:"MinimapTile_block_6"] */

        /* block comment [block:"MinimapTile_block_7" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapTile_block_7" category:"Variables" label:"set miniRelX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int miniRelX = (int)(relX * MinimapConfig.SCALE);
        /* block end [id:"MinimapTile_block_7"] */
        /* block comment [block:"MinimapTile_block_8" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapTile_block_8" category:"Variables" label:"set miniRelY" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int miniRelY = (int)(relY * MinimapConfig.SCALE);
        /* block end [id:"MinimapTile_block_8"] */

        /* block comment [block:"MinimapTile_block_9" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapTile_block_9" category:"Variables" label:"set screenX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int screenX = MinimapConfig.CENTER_X + miniRelX;
        /* block end [id:"MinimapTile_block_9"] */
        /* block comment [block:"MinimapTile_block_10" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapTile_block_10" category:"Variables" label:"set screenY" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int screenY = MinimapConfig.CENTER_Y + miniRelY;
        /* block end [id:"MinimapTile_block_10"] */

        // Culling: Slightly larger bounds for tiles to ensure they fill the minimap window completely
        /* block comment [block:"MinimapTile_block_11" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapTile_block_11" category:"Variables" label:"set paddingX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int paddingX = (int)(MinimapConfig.WORLD_TILE_W * MinimapConfig.SCALE / /* param start [id:"MinimapTile_value_16" type:"number"] */2/* param end [id:"MinimapTile_value_16"] */);
        /* block end [id:"MinimapTile_block_11"] */
        /* block comment [block:"MinimapTile_block_12" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapTile_block_12" category:"Variables" label:"set paddingY" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int paddingY = (int)(MinimapConfig.WORLD_TILE_H * MinimapConfig.SCALE / /* param start [id:"MinimapTile_value_17" type:"number"] */2/* param end [id:"MinimapTile_value_17"] */);
        /* block end [id:"MinimapTile_block_12"] */

        /* block comment [block:"MinimapTile_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MinimapTile_block_1" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (Math.abs(miniRelX) <= MinimapConfig.WIDTH / /* param start [id:"MinimapTile_value_18" type:"number"] */2/* param end [id:"MinimapTile_value_18"] */ + paddingX &&
        Math.abs(miniRelY) <= MinimapConfig.HEIGHT / /* param start [id:"MinimapTile_value_19" type:"number"] */2/* param end [id:"MinimapTile_value_19"] */ + paddingY) {
            /* block comment [block:"MinimapTile_block_13" text:"Runs the go to action. Values in this block are editable inputs."] */
            /* block start [id:"MinimapTile_block_13" category:"Motion" label:"go to" shape:"command" docs:"Runs the go to action. Values in this block are editable inputs."] */
            super.goTo(screenX, screenY);
            /* block end [id:"MinimapTile_block_13"] */
            /* block comment [block:"MinimapTile_block_14" text:"Runs the show action. Values in this block are editable inputs."] */
            /* block start [id:"MinimapTile_block_14" category:"Looks" label:"show" shape:"command" docs:"Runs the show action. Values in this block are editable inputs."] */
            show();
            /* block end [id:"MinimapTile_block_14"] */
        } else {
            /* block comment [block:"MinimapTile_block_15" text:"Runs the hide action. Values in this block are editable inputs."] */
            /* block start [id:"MinimapTile_block_15" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
            hide();
            /* block end [id:"MinimapTile_block_15"] */
        }
        /* block end [id:"MinimapTile_block_1"] */
    }
}
