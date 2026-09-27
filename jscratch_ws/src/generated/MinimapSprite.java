package generated;

import com.jscratch.*;

/**
* Icon on the minimap (player, enemies, stairs).
*
* <p>Call graph: one per {@code BSprite} (created in its ctor, skinned by
* subtype — e.g. enemy/player dots); repositioned from
* {@code BSprite.update} every frame via {@code update(worldX, worldY, dir,
* playerX, playerY)}; hidden when the owner is invisible or out of the
* minimap window. Never calls goToFront (border order is fixed at
* register time — per-icon fronting was a hotspot).
*/
public class MinimapSprite extends Sprite {

    public MinimapSprite(String name) {
        /* block comment [block:"MinimapSprite_block_2" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"MinimapSprite_block_2" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(name);
        /* block end [id:"MinimapSprite_block_2"] */
    }

    /**
    * Updates the icon's position on the minimap relative to the player.
    */
    public void update(int worldX, int worldY, double direction, int playerX, int playerY) {
        /* block comment [block:"MinimapSprite_block_3" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapSprite_block_3" category:"Variables" label:"set stage" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage stage = Stage.getInstance();
        /* block end [id:"MinimapSprite_block_3"] */
        /* block comment [block:"MinimapSprite_block_4" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapSprite_block_4" category:"Variables" label:"set displayScale" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double displayScale = stage.getScaleFactor();
        /* block end [id:"MinimapSprite_block_4"] */

        // Calculate world distance from player
        /* block comment [block:"MinimapSprite_block_5" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapSprite_block_5" category:"Variables" label:"set relX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int relX = worldX - playerX;
        /* block end [id:"MinimapSprite_block_5"] */
        /* block comment [block:"MinimapSprite_block_6" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapSprite_block_6" category:"Variables" label:"set relY" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int relY = worldY - playerY;
        /* block end [id:"MinimapSprite_block_6"] */

        // Convert world distance to minimap pixels
        /* block comment [block:"MinimapSprite_block_7" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapSprite_block_7" category:"Variables" label:"set miniRelX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int miniRelX = (int)(relX * MinimapConfig.SCALE);
        /* block end [id:"MinimapSprite_block_7"] */
        /* block comment [block:"MinimapSprite_block_8" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapSprite_block_8" category:"Variables" label:"set miniRelY" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int miniRelY = (int)(relY * MinimapConfig.SCALE);
        /* block end [id:"MinimapSprite_block_8"] */

        // Final screen position (Relative to Minimap Center * Stage Scale)
        /* block comment [block:"MinimapSprite_block_9" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapSprite_block_9" category:"Variables" label:"set screenX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int screenX = (int)((MinimapConfig.CENTER_X + miniRelX) * displayScale);
        /* block end [id:"MinimapSprite_block_9"] */
        /* block comment [block:"MinimapSprite_block_10" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapSprite_block_10" category:"Variables" label:"set screenY" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int screenY = (int)((MinimapConfig.CENTER_Y + miniRelY) * displayScale);
        /* block end [id:"MinimapSprite_block_10"] */

        // Only show if the icon is within the minimap's rectangular bounds
        /* block comment [block:"MinimapSprite_block_11" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapSprite_block_11" category:"Variables" label:"set inBoundsX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean inBoundsX = Math.abs(miniRelX) <= MinimapConfig.WIDTH / /* param start [id:"MinimapSprite_value_19" type:"number"] */2/* param end [id:"MinimapSprite_value_19"] */;
        /* block end [id:"MinimapSprite_block_11"] */
        /* block comment [block:"MinimapSprite_block_12" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapSprite_block_12" category:"Variables" label:"set inBoundsY" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean inBoundsY = Math.abs(miniRelY) <= MinimapConfig.HEIGHT / /* param start [id:"MinimapSprite_value_20" type:"number"] */2/* param end [id:"MinimapSprite_value_20"] */;
        /* block end [id:"MinimapSprite_block_12"] */

        /* block comment [block:"MinimapSprite_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MinimapSprite_block_1" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (inBoundsX && inBoundsY) {
            /* block comment [block:"MinimapSprite_block_13" text:"Runs the go to action. Values in this block are editable inputs."] */
            /* block start [id:"MinimapSprite_block_13" category:"Motion" label:"go to" shape:"command" docs:"Runs the go to action. Values in this block are editable inputs."] */
            super.goTo(screenX, screenY);
            /* block end [id:"MinimapSprite_block_13"] */
            /* block comment [block:"MinimapSprite_block_14" text:"Runs the point in direction action. Values in this block are editable inputs."] */
            /* block start [id:"MinimapSprite_block_14" category:"Motion" label:"point in direction" shape:"command" docs:"Runs the point in direction action. Values in this block are editable inputs."] */
            super.pointInDirection(direction);
            /* block end [id:"MinimapSprite_block_14"] */

            // Icons are scaled by the stage's display scale
            /* block comment [block:"MinimapSprite_block_15" text:"Runs the set scale x action. Values in this block are editable inputs."] */
            /* block start [id:"MinimapSprite_block_15" category:"Variables" label:"set scale x" shape:"command" docs:"Runs the set scale x action. Values in this block are editable inputs."] */
            super.setScaleX(/* param start [id:"MinimapSprite_value_21" type:"number"] */0.2/* param end [id:"MinimapSprite_value_21"] */ * displayScale);
            /* block end [id:"MinimapSprite_block_15"] */
            /* block comment [block:"MinimapSprite_block_16" text:"Runs the set scale y action. Values in this block are editable inputs."] */
            /* block start [id:"MinimapSprite_block_16" category:"Variables" label:"set scale y" shape:"command" docs:"Runs the set scale y action. Values in this block are editable inputs."] */
            super.setScaleY(/* param start [id:"MinimapSprite_value_22" type:"number"] */0.2/* param end [id:"MinimapSprite_value_22"] */ * displayScale);
            /* block end [id:"MinimapSprite_block_16"] */

            /* block comment [block:"MinimapSprite_block_17" text:"Runs the show action. Values in this block are editable inputs."] */
            /* block start [id:"MinimapSprite_block_17" category:"Looks" label:"show" shape:"command" docs:"Runs the show action. Values in this block are editable inputs."] */
            show();
            /* block end [id:"MinimapSprite_block_17"] */
            // NOTE: no goToFront here. Border order is fixed at register
            // time; per-icon-per-frame fronting was a major COW hotspot.
        } else {
            /* block comment [block:"MinimapSprite_block_18" text:"Runs the hide action. Values in this block are editable inputs."] */
            /* block start [id:"MinimapSprite_block_18" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
            hide();
            /* block end [id:"MinimapSprite_block_18"] */
        }
        /* block end [id:"MinimapSprite_block_1"] */
    }
}
