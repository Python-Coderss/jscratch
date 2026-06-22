package generated;

import com.jscratch.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralized pool for sharing Costumes across multiple sprites.
 * This significantly reduces RAM by ensuring each image is only loaded ONCE.
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
        if (!costumeCache.containsKey(id)) {
            costumeCache.put(id, new Costume(id, path, cx, cy));
        }
        return costumeCache.get(id);
    }

    /**
     * Overload for when ID and Path are the same.
     */
    public static Costume getCostume(String path, int cx, int cy) {
        return getCostume(path, path, cx, cy);
    }
}
