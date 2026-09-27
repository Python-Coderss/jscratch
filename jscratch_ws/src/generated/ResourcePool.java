package generated;

import com.jscratch.*;
import java.util.HashMap;
import java.util.Map;

/**
* Centralized pool for sharing Costumes across multiple sprites.
* This significantly reduces RAM by ensuring each image is only loaded ONCE.
*
* <p>Call graph: read by every sprite ctor (tiles, enemies, spells, loot,
* walls, minimap dots) via {@code getCostume}; written only on first use
* per id. {@code FloorManager.applyTint} mutates the shared tile images in
* place so world + minimap follow together.
*/
public class ResourcePool {
    private static final Map<String, Costume> costumeCache = new HashMap<>();

    /**
    * Gets a shared costume from the cache, or creates and caches it if it doesn't exist.
    * @param id A unique key for this costume (usually the image path)
    * @param path The filesystem path to the image
    * @param cx Rotation center X
    * @param cy Rotation center Y
    */
    public static Costume getCostume(String id, String path, int cx, int cy) {
        /* block comment [block:"ResourcePool_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"ResourcePool_block_1" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!costumeCache.containsKey(id)) {
            /* block comment [block:"ResourcePool_block_2" text:"Calls the put method using its declared arguments."] */
            /* block start [id:"ResourcePool_block_2" category:"My Blocks" label:"call put" shape:"command" docs:"Calls the put method using its declared arguments."] */
            costumeCache.put(id, new Costume(id, path, cx, cy));
            /* block end [id:"ResourcePool_block_2"] */
        }
        /* block end [id:"ResourcePool_block_1"] */
        /* block comment [block:"ResourcePool_block_3" text:"Returns a value or ends the current method."] */
        /* block start [id:"ResourcePool_block_3" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return costumeCache.get(id);
        /* block end [id:"ResourcePool_block_3"] */
    }

    /**
    * Overload for when ID and Path are the same.
    */
    public static Costume getCostume(String path, int cx, int cy) {
        /* block comment [block:"ResourcePool_block_4" text:"Returns a value or ends the current method."] */
        /* block start [id:"ResourcePool_block_4" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return getCostume(path, path, cx, cy);
        /* block end [id:"ResourcePool_block_4"] */
    }
}
