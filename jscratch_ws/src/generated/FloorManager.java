package generated;

import com.jscratch.Costume;
import com.jscratch.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
* 7-floor dungeon built from the existing 9 map tiles (480x360 each).
* Each floor re-tints the shared tile costumes in place (world + minimap
* update together), reconfigures EnemyManager (mix / limit / scaling),
* and moves the stairs. Step on the stairs pad to descend.
*
* <p>Call graph / interactions:
* <ul>
*   <li>Singleton created from {@code Main}; ticked per player frame from
*       {@code Sprite1} via {@code update} (chest check + stair proximity →
*       {@code descend/applyFloor}).</li>
*   <li>Calls into: {@code Scroller2d} (colliders, wall sprites, updates),
*       {@code ResourcePool} (shared tile/wall costumes),
*       {@code EnemyManager} (configure/clearAll/addEnemy for the boss),
*       {@code MusicDirector.onFloor} (per-floor tracks),
*       {@code Stage} broadcast {@code "floorChanged"} (Sprite1 resets to
*       spawn) and {@code PlayerStats.restoreMana}.</li>
*   <li>Reads {@code FloorManager.peekWalls} from {@code MinimapWalls.draw}
*       without forcing init.</li>
* </ul>
*
* Enemy type ids: 0 orc, 1 goblin, 2 zombie, 3 ghoul, 4 slime.
* Java 8 compatible.
*/
public class FloorManager {
    private static FloorManager instance;

    public static class FloorDef {
        /* block comment [block:"FloorManager_block_55" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_55" category:"Variables" label:"set num" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public final int num;
        /* block end [id:"FloorManager_block_55"] */
        /* block comment [block:"FloorManager_block_56" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_56" category:"Variables" label:"set name" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public final String name;
        /* block end [id:"FloorManager_block_56"] */
        /* block comment [block:"FloorManager_block_57" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_57" category:"Variables" label:"set tr" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public final float tr, tg, tb;
        /* block end [id:"FloorManager_block_57"] */
        /* block comment [block:"FloorManager_block_58" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_58" category:"Variables" label:"set mix" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public final int[] mix;
        /* block end [id:"FloorManager_block_58"] */
        /* block comment [block:"FloorManager_block_59" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_59" category:"Variables" label:"set limit" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public final int limit;
        /* block end [id:"FloorManager_block_59"] */
        /* block comment [block:"FloorManager_block_60" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_60" category:"Variables" label:"set hpMul" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public final double hpMul;
        /* block end [id:"FloorManager_block_60"] */
        /* block comment [block:"FloorManager_block_61" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_61" category:"Variables" label:"set dmgBonus" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public final int dmgBonus;
        /* block end [id:"FloorManager_block_61"] */
        /** Interior walls as {cx, cy, w, h} (borders are automatic). */
        /* block comment [block:"FloorManager_block_62" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_62" category:"Variables" label:"set walls" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public final int[][] walls;
        /* block end [id:"FloorManager_block_62"] */
        /** Music tracks (MusicManager catalog paths). */
        /* block comment [block:"FloorManager_block_63" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_63" category:"Variables" label:"set calmTrack" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public final String calmTrack;
        /* block end [id:"FloorManager_block_63"] */
        /* block comment [block:"FloorManager_block_64" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_64" category:"Variables" label:"set combatTrack" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public final String combatTrack;
        /* block end [id:"FloorManager_block_64"] */
        /** Stone texture asset for this floor's walls. */
        /* block comment [block:"FloorManager_block_65" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_65" category:"Variables" label:"set wallTex" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public final String wallTex;
        /* block end [id:"FloorManager_block_65"] */
        /* block comment [block:"FloorManager_block_66" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_66" category:"Variables" label:"set stairX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public final int stairX, stairY;
        /* block end [id:"FloorManager_block_66"] */
        /* block comment [block:"FloorManager_block_67" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_67" category:"Variables" label:"set chestX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public final int chestX, chestY;
        /* block end [id:"FloorManager_block_67"] */

        public FloorDef(int num, String name, float tr, float tg, float tb,
        int[] mix, int limit, double hpMul, int dmgBonus, int[][] walls,
        String calmTrack, String combatTrack, String wallTex,
        int stairX, int stairY, int chestX, int chestY) {
            /* block comment [block:"FloorManager_block_68" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"FloorManager_block_68" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.num = num;
            /* block end [id:"FloorManager_block_68"] */
            /* block comment [block:"FloorManager_block_69" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"FloorManager_block_69" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.name = name;
            /* block end [id:"FloorManager_block_69"] */
            /* block comment [block:"FloorManager_block_70" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"FloorManager_block_70" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.tr = tr;
            /* block end [id:"FloorManager_block_70"] */
            /* block comment [block:"FloorManager_block_71" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"FloorManager_block_71" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.tg = tg;
            /* block end [id:"FloorManager_block_71"] */
            /* block comment [block:"FloorManager_block_72" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"FloorManager_block_72" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.tb = tb;
            /* block end [id:"FloorManager_block_72"] */
            /* block comment [block:"FloorManager_block_73" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"FloorManager_block_73" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.mix = mix;
            /* block end [id:"FloorManager_block_73"] */
            /* block comment [block:"FloorManager_block_74" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"FloorManager_block_74" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.limit = limit;
            /* block end [id:"FloorManager_block_74"] */
            /* block comment [block:"FloorManager_block_75" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"FloorManager_block_75" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.hpMul = hpMul;
            /* block end [id:"FloorManager_block_75"] */
            /* block comment [block:"FloorManager_block_76" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"FloorManager_block_76" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.dmgBonus = dmgBonus;
            /* block end [id:"FloorManager_block_76"] */
            /* block comment [block:"FloorManager_block_77" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"FloorManager_block_77" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.walls = walls;
            /* block end [id:"FloorManager_block_77"] */
            /* block comment [block:"FloorManager_block_78" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"FloorManager_block_78" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.calmTrack = calmTrack;
            /* block end [id:"FloorManager_block_78"] */
            /* block comment [block:"FloorManager_block_79" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"FloorManager_block_79" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.combatTrack = combatTrack;
            /* block end [id:"FloorManager_block_79"] */
            /* block comment [block:"FloorManager_block_80" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"FloorManager_block_80" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.wallTex = wallTex;
            /* block end [id:"FloorManager_block_80"] */
            /* block comment [block:"FloorManager_block_81" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"FloorManager_block_81" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.stairX = stairX;
            /* block end [id:"FloorManager_block_81"] */
            /* block comment [block:"FloorManager_block_82" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"FloorManager_block_82" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.stairY = stairY;
            /* block end [id:"FloorManager_block_82"] */
            /* block comment [block:"FloorManager_block_83" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"FloorManager_block_83" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.chestX = chestX;
            /* block end [id:"FloorManager_block_83"] */
            /* block comment [block:"FloorManager_block_84" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"FloorManager_block_84" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.chestY = chestY;
            /* block end [id:"FloorManager_block_84"] */
        }
    }

    private final List<FloorDef> floors = new ArrayList<>();
    private int currentFloor = 1;
    private boolean victory = false;
    /** Spawn grace is started by character creation and restarted per floor entry. */
    private static final long SPAWN_GRACE_MS = 10_000L;
    private Stairs stairs;
    private Chest chest;
    private Minitaur bossRef;
    private final java.util.List<WallSprite> wallSprites = new java.util.ArrayList<>();
    /** Active wall rects as {cx, cy, w, h} (borders + interior), for the minimap. */
    private final java.util.List<int[]> wallRects = new java.util.ArrayList<>();

    private final List<String> tileIds = new ArrayList<>();
    private final List<BufferedImage> pristine = new ArrayList<>();

    public static FloorManager getInstance() {
        /* block comment [block:"FloorManager_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_1" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (instance == /* param start [id:"FloorManager_value_223" type:"value"] */null/* param end [id:"FloorManager_value_223"] */) {
            /* block comment [block:"FloorManager_block_85" text:"Runs the floor manager action. Values in this block are editable inputs."] */
            /* block start [id:"FloorManager_block_85" category:"My Blocks" label:"floor manager" shape:"command" docs:"Runs the floor manager action. Values in this block are editable inputs."] */
            instance = new FloorManager();
            /* block end [id:"FloorManager_block_85"] */
        }
        /* block end [id:"FloorManager_block_1"] */
        /* block comment [block:"FloorManager_block_86" text:"Returns a value or ends the current method."] */
        /* block start [id:"FloorManager_block_86" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return instance;
        /* block end [id:"FloorManager_block_86"] */
    }

    private FloorManager() {
        // F1: tutorial glade — four standing stones + a ruined west wall with a gap.
        floors.add(new FloorDef(1, "Mossy Entry", 0.85f, 1.0f, 0.8f, new int[]{1, 1, 2}, 2, 1.0, 0,
        new int[][]{{300, 200, 80, 80}, {-300, 200, 80, 80}, {300, -200, 80, 80}, {-300, -200, 80, 80},
            {-450, 150, 60, 200}, {-450, -150, 60, 200}}/* block comment [block:"FloorManager_block_87" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"FloorManager_block_87" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        ,
        MusicManager.CALM_MOSS_GROTTO, MusicManager.EXPLORE_PART_FALLING_COMBAT,
        /* param start [id:"FloorManager_value_224" type:"text"] */"jscratch_ws/assets/map tile grey stone.png"/* param end [id:"FloorManager_value_224"] */,
        /* param start [id:"FloorManager_value_225" type:"number"] */520/* param end [id:"FloorManager_value_225"] */, /* param start [id:"FloorManager_value_226" type:"number"] */300/* param end [id:"FloorManager_value_226"] */, -/* param start [id:"FloorManager_value_227" type:"number"] */520/* param end [id:"FloorManager_value_227"] */, -/* param start [id:"FloorManager_value_228" type:"number"] */300/* param end [id:"FloorManager_value_228"] */));
        /* block end [id:"FloorManager_block_87"] */
        // F2: split great-hall — one long wall, cross through the middle gap.
        floors.add(new FloorDef(2, "Grey Halls", 0.8f, 0.8f, 0.85f, new int[]{0, 1, 2}, 3, 1.1, 0,
        new int[][]{{-200, 315, 60, 450}, {-200, -315, 60, 450}, {350, 300, 80, 80}, {350, -300, 80, 80}}/* block comment [block:"FloorManager_block_88" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"FloorManager_block_88" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        ,
        MusicManager.EXPLORE_IN_ABSENTIA_CALM, MusicManager.EXPLORE_IN_ABSENTIA_COMBAT,
        /* param start [id:"FloorManager_value_229" type:"text"] */"jscratch_ws/assets/map tile medium dark stone.png"/* param end [id:"FloorManager_value_229"] */,
        -/* param start [id:"FloorManager_value_230" type:"number"] */520/* param end [id:"FloorManager_value_230"] */, /* param start [id:"FloorManager_value_231" type:"number"] */380/* param end [id:"FloorManager_value_231"] */, /* param start [id:"FloorManager_value_232" type:"number"] */520/* param end [id:"FloorManager_value_232"] */, -/* param start [id:"FloorManager_value_233" type:"number"] */380/* param end [id:"FloorManager_value_233"] */));
        /* block end [id:"FloorManager_block_88"] */
        // F3: warren tunnels — staggered bars force an S-weave (gaps alternate sides).
        floors.add(new FloorDef(3, "Dark Warren", 0.55f, 0.55f, 0.65f, new int[]{1, 2, 3, 3}, 3, 1.25, 1,
        new int[][]{{-420, 180, 600, 60}, {420, -60, 600, 60}, {-420, -300, 600, 60},
            {0, 420, 200, 60}}/* block comment [block:"FloorManager_block_89" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"FloorManager_block_89" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        ,
        MusicManager.EXPLORE_CASTLE_VEIN_CALM, MusicManager.EXPLORE_CASTLE_VEIN_COMBAT,
        /* param start [id:"FloorManager_value_234" type:"text"] */"jscratch_ws/assets/map tile dark stone.png"/* param end [id:"FloorManager_value_234"] */,
        /* param start [id:"FloorManager_value_235" type:"number"] */600/* param end [id:"FloorManager_value_235"] */, /* param start [id:"FloorManager_value_236" type:"number"] */420/* param end [id:"FloorManager_value_236"] */, -/* param start [id:"FloorManager_value_237" type:"number"] */600/* param end [id:"FloorManager_value_237"] */, -/* param start [id:"FloorManager_value_238" type:"number"] */420/* param end [id:"FloorManager_value_238"] */));
        /* block end [id:"FloorManager_block_89"] */
        // F4: ember crossroads — four stubs leave a center plaza + corner pits.
        floors.add(new FloorDef(4, "Ember Deep", 1.0f, 0.7f, 0.6f, new int[]{0, 0, 3, 1}, 4, 1.4, 1,
        new int[][]{{-375, 150, 450, 60}, {375, 150, 450, 60}, {-375, -150, 450, 60}, {375, -150, 450, 60},
            {-550, -420, 100, 80}, {550, -420, 100, 80}}/* block comment [block:"FloorManager_block_90" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"FloorManager_block_90" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        ,
        MusicManager.EXPLORE_PART_FALLING_CALM, MusicManager.EXPLORE_PART_FALLING_COMBAT,
        /* param start [id:"FloorManager_value_239" type:"text"] */"jscratch_ws/assets/map tile bright stone.png"/* param end [id:"FloorManager_value_239"] */,
        /* param start [id:"FloorManager_value_240" type:"number"] */0/* param end [id:"FloorManager_value_240"] */, /* param start [id:"FloorManager_value_241" type:"number"] */420/* param end [id:"FloorManager_value_241"] */, /* param start [id:"FloorManager_value_242" type:"number"] */0/* param end [id:"FloorManager_value_242"] */, -/* param start [id:"FloorManager_value_243" type:"number"] */300/* param end [id:"FloorManager_value_243"] */));
        /* block end [id:"FloorManager_block_90"] */
        // F5: sunken causeway — one long wall, single center gap.
        floors.add(new FloorDef(5, "Sunken Slime", 0.6f, 0.9f, 0.85f, new int[]{2, 2, 4, 4, 3}, 4, 1.55, 2,
        new int[][]{{-350, 0, 500, 60}, {350, 0, 500, 60}, {0, 350, 120, 120}, {0, -350, 120, 120}}/* block comment [block:"FloorManager_block_91" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"FloorManager_block_91" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        ,
        MusicManager.EXTRA_RAIN_FALLS_CALM, MusicManager.EXTRA_RAIN_FALLS_COMBAT,
        /* param start [id:"FloorManager_value_244" type:"text"] */"jscratch_ws/assets/map tile grey stone.png"/* param end [id:"FloorManager_value_244"] */,
        /* param start [id:"FloorManager_value_245" type:"number"] */620/* param end [id:"FloorManager_value_245"] */, /* param start [id:"FloorManager_value_246" type:"number"] */420/* param end [id:"FloorManager_value_246"] */, -/* param start [id:"FloorManager_value_247" type:"number"] */620/* param end [id:"FloorManager_value_247"] */, -/* param start [id:"FloorManager_value_248" type:"number"] */420/* param end [id:"FloorManager_value_248"] */));
        /* block end [id:"FloorManager_block_91"] */
        // F6: warden's gatehouse — twin towers, pass north or south around the gate wall.
        floors.add(new FloorDef(6, "Warden's Gate", 0.75f, 0.65f, 0.9f, new int[]{0, 1, 3, 4, 2}, 5, 1.75, 2,
        new int[][]{{-250, 200, 60, 680}, {250, -200, 60, 680}, {0, -350, 500, 60},
            {-500, 350, 160, 60}, {500, 350, 160, 60}}/* block comment [block:"FloorManager_block_92" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"FloorManager_block_92" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        ,
        MusicManager.CALM_THOUSAND_GREETINGS, MusicManager.EXPLORE_CASTLE_VEIN_COMBAT,
        /* param start [id:"FloorManager_value_249" type:"text"] */"jscratch_ws/assets/map tile medium dark stone.png"/* param end [id:"FloorManager_value_249"] */,
        /* param start [id:"FloorManager_value_250" type:"number"] */0/* param end [id:"FloorManager_value_250"] */, /* param start [id:"FloorManager_value_251" type:"number"] */420/* param end [id:"FloorManager_value_251"] */, -/* param start [id:"FloorManager_value_252" type:"number"] */500/* param end [id:"FloorManager_value_252"] */, -/* param start [id:"FloorManager_value_253" type:"number"] */420/* param end [id:"FloorManager_value_253"] */));
        /* block end [id:"FloorManager_block_92"] */
        // F7: throne arena — pillars, throne block north, stairs tucked behind-right.
        floors.add(new FloorDef(7, "Minitaur's Throne", 0.9f, 0.55f, 0.55f, new int[]{0, 3, 4}, 4, 2.0, 3,
        new int[][]{{250, 250, 90, 90}, {-250, 250, 90, 90}, {250, -250, 90, 90}, {-250, -250, 90, 90},
            {0, 430, 240, 80}}/* block comment [block:"FloorManager_block_93" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"FloorManager_block_93" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        ,
        MusicManager.BOSS_GRIMM_TROUPE, MusicManager.BOSS_GRIMM_TROUPE,
        /* param start [id:"FloorManager_value_254" type:"text"] */"jscratch_ws/assets/map tile dark stone.png"/* param end [id:"FloorManager_value_254"] */,
        /* param start [id:"FloorManager_value_255" type:"number"] */350/* param end [id:"FloorManager_value_255"] */, /* param start [id:"FloorManager_value_256" type:"number"] */450/* param end [id:"FloorManager_value_256"] */, -/* param start [id:"FloorManager_value_257" type:"number"] */600/* param end [id:"FloorManager_value_257"] */, -/* param start [id:"FloorManager_value_258" type:"number"] */420/* param end [id:"FloorManager_value_258"] */));
        /* block end [id:"FloorManager_block_93"] */

        // Snapshot pristine tile pixels (must run after Scroller2d registered costumes)
        /* block comment [block:"FloorManager_block_2" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"FloorManager_block_2" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int tx = -/* param start [id:"FloorManager_value_259" type:"number"] */1/* param end [id:"FloorManager_value_259"] */; tx <= /* param start [id:"FloorManager_value_260" type:"number"] */1/* param end [id:"FloorManager_value_260"] */; tx++) {
            /* block comment [block:"FloorManager_block_3" text:"Repeats the nested operations for each loop turn."] */
            /* block start [id:"FloorManager_block_3" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
            for (int ty = -/* param start [id:"FloorManager_value_261" type:"number"] */1/* param end [id:"FloorManager_value_261"] */; ty <= /* param start [id:"FloorManager_value_262" type:"number"] */1/* param end [id:"FloorManager_value_262"] */; ty++) {
                /* block comment [block:"FloorManager_block_94" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"FloorManager_block_94" category:"Variables" label:"set id" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                String id = tx + /* param start [id:"FloorManager_value_263" type:"text"] */","/* param end [id:"FloorManager_value_263"] */ + ty;
                /* block end [id:"FloorManager_block_94"] */
                /* block comment [block:"FloorManager_block_95" text:"Calls the add method using its declared arguments."] */
                /* block start [id:"FloorManager_block_95" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
                tileIds.add(id);
                /* block end [id:"FloorManager_block_95"] */
                /* block comment [block:"FloorManager_block_96" text:"Calls the add method using its declared arguments."] */
                /* block start [id:"FloorManager_block_96" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
                pristine.add(loadTile(id));
                /* block end [id:"FloorManager_block_96"] */
            }
            /* block end [id:"FloorManager_block_3"] */
        }
        /* block end [id:"FloorManager_block_2"] */

        /* block comment [block:"FloorManager_block_97" text:"Runs the stairs action. Values in this block are editable inputs."] */
        /* block start [id:"FloorManager_block_97" category:"My Blocks" label:"stairs" shape:"command" docs:"Runs the stairs action. Values in this block are editable inputs."] */
        stairs = new Stairs();
        /* block end [id:"FloorManager_block_97"] */
        /* block comment [block:"FloorManager_block_98" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_98" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().addSprite(stairs);
        /* block end [id:"FloorManager_block_98"] */
        /* block comment [block:"FloorManager_block_99" text:"Runs the chest action. Values in this block are editable inputs."] */
        /* block start [id:"FloorManager_block_99" category:"My Blocks" label:"chest" shape:"command" docs:"Runs the chest action. Values in this block are editable inputs."] */
        chest = new Chest();
        /* block end [id:"FloorManager_block_99"] */
        /* block comment [block:"FloorManager_block_100" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_100" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().addSprite(chest);
        /* block end [id:"FloorManager_block_100"] */

        /* block comment [block:"FloorManager_block_101" text:"Calls the applyFloor method using its declared arguments."] */
        /* block start [id:"FloorManager_block_101" category:"My Blocks" label:"call apply floor" shape:"command" docs:"Calls the applyFloor method using its declared arguments."] */
        applyFloor();
        /* block end [id:"FloorManager_block_101"] */

        // Dungeon props (stairs/chest/walls) are gameplay-only. The
        // character menu owns the screen until GameState.toGameplay().
        Stage.getInstance().whenIReceive("enterGameplay", () -> {
            setDungeonVisible(true);
            publishFloorHud();
            /* block comment [block:"FloorManager_block_4" text:"Runs work safely and handles matching errors in a catch branch."] */
            /* block start [id:"FloorManager_block_4" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
            try { Scroller2d.getInstance().update(); }
            /* block end [id:"FloorManager_block_4"] */ catch (Exception ignored) {}
        }/* block comment [block:"FloorManager_block_102" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"FloorManager_block_102" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        );
        /* block end [id:"FloorManager_block_102"] */
        /* block comment [block:"FloorManager_block_103" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_103" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().whenIReceive(/* param start [id:"FloorManager_value_264" type:"text"] */"enterCharacterCreate"/* param end [id:"FloorManager_value_264"] */, () -> setDungeonVisible(/* param start [id:"FloorManager_value_265" type:"boolean"] */false/* param end [id:"FloorManager_value_265"] */));
        /* block end [id:"FloorManager_block_103"] */
        /* block comment [block:"FloorManager_block_5" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_5" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!GameState.isGameplay()) {
            /* block comment [block:"FloorManager_block_104" text:"Runs the set dungeon visible action. Values in this block are editable inputs."] */
            /* block start [id:"FloorManager_block_104" category:"Variables" label:"set dungeon visible" shape:"command" docs:"Runs the set dungeon visible action. Values in this block are editable inputs."] */
            setDungeonVisible(/* param start [id:"FloorManager_value_266" type:"boolean"] */false/* param end [id:"FloorManager_value_266"] */);
            /* block end [id:"FloorManager_block_104"] */
        }
        /* block end [id:"FloorManager_block_5"] */
    }

    /**
    * Shows/hides gameplay-only dungeon props: stairs, chest and all wall
    * sprites (plus their minimap dots). Enemies/loot never exist during
    * character creation (spawned only from the player loop), so they need
    * no handling here.
    */
    public void setDungeonVisible(boolean visible) {
        /* block comment [block:"FloorManager_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_6" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (stairs != /* param start [id:"FloorManager_value_267" type:"value"] */null/* param end [id:"FloorManager_value_267"] */) {
            /* block comment [block:"FloorManager_block_7" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"FloorManager_block_7" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (visible) {
                /* block comment [block:"FloorManager_block_105" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"FloorManager_block_105" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"FloorManager_block_8" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"FloorManager_block_8" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (!victory) stairs.show();
                /* block end [id:"FloorManager_block_105"] */
                /* block end [id:"FloorManager_block_8"] */
                /* block comment [block:"FloorManager_block_106" text:"Calls the update method using its declared arguments."] */
                /* block start [id:"FloorManager_block_106" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
                stairs.update();
                /* block end [id:"FloorManager_block_106"] */
            } else {
                /* block comment [block:"FloorManager_block_107" text:"Runs the hide action. Values in this block are editable inputs."] */
                /* block start [id:"FloorManager_block_107" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
                stairs.hide();
                /* block end [id:"FloorManager_block_107"] */
                /* block comment [block:"FloorManager_block_108" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"FloorManager_block_108" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"FloorManager_block_9" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"FloorManager_block_9" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (stairs.minimapSprite != /* param start [id:"FloorManager_value_268" type:"value"] */null/* param end [id:"FloorManager_value_268"] */) stairs.minimapSprite.hide();
                /* block end [id:"FloorManager_block_108"] */
                /* block end [id:"FloorManager_block_9"] */
            }
            /* block end [id:"FloorManager_block_7"] */
        }
        /* block end [id:"FloorManager_block_6"] */
        /* block comment [block:"FloorManager_block_10" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_10" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (chest != /* param start [id:"FloorManager_value_269" type:"value"] */null/* param end [id:"FloorManager_value_269"] */) {
            /* block comment [block:"FloorManager_block_11" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"FloorManager_block_11" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (visible) {
                // Only reveal an unopened chest; opened ones stay hidden
                // until resetForFloor on the next descend.
                /* block comment [block:"FloorManager_block_109" text:"Calls the update method using its declared arguments."] */
                /* block start [id:"FloorManager_block_109" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
                chest.update();
                /* block end [id:"FloorManager_block_109"] */
            } else {
                /* block comment [block:"FloorManager_block_110" text:"Runs the hide action. Values in this block are editable inputs."] */
                /* block start [id:"FloorManager_block_110" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
                chest.hide();
                /* block end [id:"FloorManager_block_110"] */
                /* block comment [block:"FloorManager_block_111" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"FloorManager_block_111" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"FloorManager_block_12" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"FloorManager_block_12" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (chest.minimapSprite != /* param start [id:"FloorManager_value_270" type:"value"] */null/* param end [id:"FloorManager_value_270"] */) chest.minimapSprite.hide();
                /* block end [id:"FloorManager_block_111"] */
                /* block end [id:"FloorManager_block_12"] */
            }
            /* block end [id:"FloorManager_block_11"] */
        }
        /* block end [id:"FloorManager_block_10"] */
        /* block comment [block:"FloorManager_block_13" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"FloorManager_block_13" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (WallSprite s : wallSprites) {
            /* block comment [block:"FloorManager_block_14" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"FloorManager_block_14" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (visible) {
                /* block comment [block:"FloorManager_block_112" text:"Runs the show action. Values in this block are editable inputs."] */
                /* block start [id:"FloorManager_block_112" category:"Looks" label:"show" shape:"command" docs:"Runs the show action. Values in this block are editable inputs."] */
                s.show();
                /* block end [id:"FloorManager_block_112"] */
                /* block comment [block:"FloorManager_block_113" text:"Calls the update method using its declared arguments."] */
                /* block start [id:"FloorManager_block_113" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
                s.update();
                /* block end [id:"FloorManager_block_113"] */
            } else {
                /* block comment [block:"FloorManager_block_114" text:"Runs the hide action. Values in this block are editable inputs."] */
                /* block start [id:"FloorManager_block_114" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
                s.hide();
                /* block end [id:"FloorManager_block_114"] */
                /* block comment [block:"FloorManager_block_115" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"FloorManager_block_115" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"FloorManager_block_15" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"FloorManager_block_15" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (s.minimapSprite != /* param start [id:"FloorManager_value_271" type:"value"] */null/* param end [id:"FloorManager_value_271"] */) s.minimapSprite.hide();
                /* block end [id:"FloorManager_block_115"] */
                /* block end [id:"FloorManager_block_15"] */
            }
            /* block end [id:"FloorManager_block_14"] */
        }
        /* block end [id:"FloorManager_block_13"] */
        /* block comment [block:"FloorManager_block_16" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_16" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (bossRef != /* param start [id:"FloorManager_value_272" type:"value"] */null/* param end [id:"FloorManager_value_272"] */ && !visible) {
            /* block comment [block:"FloorManager_block_116" text:"Runs the hide action. Values in this block are editable inputs."] */
            /* block start [id:"FloorManager_block_116" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
            bossRef.hide();
            /* block end [id:"FloorManager_block_116"] */
            /* block comment [block:"FloorManager_block_117" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"FloorManager_block_117" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"FloorManager_block_17" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"FloorManager_block_17" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (bossRef.minimapSprite != /* param start [id:"FloorManager_value_273" type:"value"] */null/* param end [id:"FloorManager_value_273"] */) bossRef.minimapSprite.hide();
            /* block end [id:"FloorManager_block_117"] */
            /* block end [id:"FloorManager_block_17"] */
            /* block comment [block:"FloorManager_block_118" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"FloorManager_block_118" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"FloorManager_block_18" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"FloorManager_block_18" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (bossRef.hpBar != /* param start [id:"FloorManager_value_274" type:"value"] */null/* param end [id:"FloorManager_value_274"] */) bossRef.hpBar.hide();
            /* block end [id:"FloorManager_block_118"] */
            /* block end [id:"FloorManager_block_18"] */
        } else /* block comment [block:"FloorManager_block_19" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_19" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (bossRef != /* param start [id:"FloorManager_value_275" type:"value"] */null/* param end [id:"FloorManager_value_275"] */ && visible && bossRef.isVisible()) {
            /* block comment [block:"FloorManager_block_119" text:"Calls the update method using its declared arguments."] */
            /* block start [id:"FloorManager_block_119" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
            bossRef.update();
            /* block end [id:"FloorManager_block_119"] */
        }
        /* block end [id:"FloorManager_block_19"] */
        /* block end [id:"FloorManager_block_16"] */
    }

    /** (Re)publish Floor/Msg watchers — gameplay only, never over the menu. */
    private void publishFloorHud() {
        /* block comment [block:"FloorManager_block_120" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"FloorManager_block_120" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"FloorManager_block_20" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_20" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!GameState.isGameplay() || victory) return;
        /* block end [id:"FloorManager_block_120"] */
        /* block end [id:"FloorManager_block_20"] */
        /* block comment [block:"FloorManager_block_121" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_121" category:"Variables" label:"set def" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        FloorDef def = getCurrentDef();
        /* block end [id:"FloorManager_block_121"] */
        /* block comment [block:"FloorManager_block_122" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"FloorManager_block_122" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"FloorManager_block_21" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_21" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (def == /* param start [id:"FloorManager_value_276" type:"value"] */null/* param end [id:"FloorManager_value_276"] */) return;
        /* block end [id:"FloorManager_block_122"] */
        /* block end [id:"FloorManager_block_21"] */
        /* block comment [block:"FloorManager_block_123" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_123" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().setVariable(/* param start [id:"FloorManager_value_277" type:"text"] */"Floor"/* param end [id:"FloorManager_value_277"] */, currentFloor + /* param start [id:"FloorManager_value_278" type:"text"] */"/7 "/* param end [id:"FloorManager_value_278"] */ + def.name);
        /* block end [id:"FloorManager_block_123"] */
        /* block comment [block:"FloorManager_block_124" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_124" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().setVariable(/* param start [id:"FloorManager_value_279" type:"text"] */"Msg"/* param end [id:"FloorManager_value_279"] */, /* param start [id:"FloorManager_value_280" type:"text"] */"Floor "/* param end [id:"FloorManager_value_280"] */ + currentFloor + /* param start [id:"FloorManager_value_281" type:"text"] */": "/* param end [id:"FloorManager_value_281"] */ + def.name);
        /* block end [id:"FloorManager_block_124"] */
    }

    public int getFloor() {
        /* block comment [block:"FloorManager_block_125" text:"Returns a value or ends the current method."] */
        /* block start [id:"FloorManager_block_125" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return currentFloor;
        /* block end [id:"FloorManager_block_125"] */
    }

    public boolean isVictory() {
        /* block comment [block:"FloorManager_block_126" text:"Returns a value or ends the current method."] */
        /* block start [id:"FloorManager_block_126" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return victory;
        /* block end [id:"FloorManager_block_126"] */
    }

    public FloorDef getCurrentDef() {
        /* block comment [block:"FloorManager_block_127" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_127" category:"Variables" label:"set i" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int i = Math.max(/* param start [id:"FloorManager_value_282" type:"number"] */1/* param end [id:"FloorManager_value_282"] */, Math.min(currentFloor, /* param start [id:"FloorManager_value_283" type:"number"] */7/* param end [id:"FloorManager_value_283"] */)) - /* param start [id:"FloorManager_value_284" type:"number"] */1/* param end [id:"FloorManager_value_284"] */;
        /* block end [id:"FloorManager_block_127"] */
        /* block comment [block:"FloorManager_block_128" text:"Returns a value or ends the current method."] */
        /* block start [id:"FloorManager_block_128" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return floors.get(i);
        /* block end [id:"FloorManager_block_128"] */
    }

    /** Wall rects without creating anything (safe before init). */
    public static java.util.List<int[]> peekWalls() {
        /* block comment [block:"FloorManager_block_129" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"FloorManager_block_129" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"FloorManager_block_22" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_22" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (instance == /* param start [id:"FloorManager_value_285" type:"value"] */null/* param end [id:"FloorManager_value_285"] */) return /* param start [id:"FloorManager_value_286" type:"value"] */null/* param end [id:"FloorManager_value_286"] */;
        /* block end [id:"FloorManager_block_129"] */
        /* block end [id:"FloorManager_block_22"] */
        /* block comment [block:"FloorManager_block_130" text:"Returns a value or ends the current method."] */
        /* block start [id:"FloorManager_block_130" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return instance.wallRects;
        /* block end [id:"FloorManager_block_130"] */
    }

    /** Called every player frame: checks stair descent. */
    public void update(int playerX, int playerY) {
        /* block comment [block:"FloorManager_block_131" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"FloorManager_block_131" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"FloorManager_block_23" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_23" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!GameState.isGameplay()) return;
        /* block end [id:"FloorManager_block_131"] */
        /* block end [id:"FloorManager_block_23"] */
        /* block comment [block:"FloorManager_block_132" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"FloorManager_block_132" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"FloorManager_block_24" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_24" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (victory || !PlayerStats.ready || stairs == /* param start [id:"FloorManager_value_287" type:"value"] */null/* param end [id:"FloorManager_value_287"] */ || !stairs.isVisible()) return;
        /* block end [id:"FloorManager_block_132"] */
        /* block end [id:"FloorManager_block_24"] */
        /* block comment [block:"FloorManager_block_133" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"FloorManager_block_133" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"FloorManager_block_25" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_25" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (chest != /* param start [id:"FloorManager_value_288" type:"value"] */null/* param end [id:"FloorManager_value_288"] */) chest.check(playerX, playerY);
        /* block end [id:"FloorManager_block_133"] */
        /* block end [id:"FloorManager_block_25"] */
        /* block comment [block:"FloorManager_block_134" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_134" category:"Variables" label:"set dx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double dx = stairs.xx - playerX;
        /* block end [id:"FloorManager_block_134"] */
        /* block comment [block:"FloorManager_block_135" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_135" category:"Variables" label:"set dy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double dy = stairs.yy - playerY;
        /* block end [id:"FloorManager_block_135"] */
        /* block comment [block:"FloorManager_block_26" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_26" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (dx * dx + dy * dy < /* param start [id:"FloorManager_value_289" type:"number"] */55/* param end [id:"FloorManager_value_289"] */ * /* param start [id:"FloorManager_value_290" type:"number"] */55/* param end [id:"FloorManager_value_290"] */) {
            // Floor 7: the stairs are sealed until the Minitaur falls
            /* block comment [block:"FloorManager_block_27" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"FloorManager_block_27" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (currentFloor == /* param start [id:"FloorManager_value_291" type:"number"] */7/* param end [id:"FloorManager_value_291"] */ && bossRef != /* param start [id:"FloorManager_value_292" type:"value"] */null/* param end [id:"FloorManager_value_292"] */ && bossRef.isVisible()) {
                /* block comment [block:"FloorManager_block_136" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"FloorManager_block_136" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                Stage.getInstance().setVariable(/* param start [id:"FloorManager_value_293" type:"text"] */"Msg"/* param end [id:"FloorManager_value_293"] */, /* param start [id:"FloorManager_value_294" type:"text"] */"Slay the Minitaur first!"/* param end [id:"FloorManager_value_294"] */);
                /* block end [id:"FloorManager_block_136"] */
                /* block comment [block:"FloorManager_block_137" text:"Returns a value or ends the current method."] */
                /* block start [id:"FloorManager_block_137" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
                return;
                /* block end [id:"FloorManager_block_137"] */
            }
            /* block end [id:"FloorManager_block_27"] */
            /* block comment [block:"FloorManager_block_138" text:"Calls the descend method using its declared arguments."] */
            /* block start [id:"FloorManager_block_138" category:"My Blocks" label:"call descend" shape:"command" docs:"Calls the descend method using its declared arguments."] */
            descend();
            /* block end [id:"FloorManager_block_138"] */
        }
        /* block end [id:"FloorManager_block_26"] */
    }

    /** Reset all floor/run state before creating a replacement character. */
    public void resetForNewRun() {
        /* block comment [block:"FloorManager_block_139" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"FloorManager_block_139" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        victory = /* param start [id:"FloorManager_value_295" type:"boolean"] */false/* param end [id:"FloorManager_value_295"] */;
        /* block end [id:"FloorManager_block_139"] */
        /* block comment [block:"FloorManager_block_140" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"FloorManager_block_140" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        currentFloor = /* param start [id:"FloorManager_value_296" type:"number"] */1/* param end [id:"FloorManager_value_296"] */;
        /* block end [id:"FloorManager_block_140"] */
        /* block comment [block:"FloorManager_block_141" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"FloorManager_block_141" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        bossRef = /* param start [id:"FloorManager_value_297" type:"value"] */null/* param end [id:"FloorManager_value_297"] */;
        /* block end [id:"FloorManager_block_141"] */
        /* block comment [block:"FloorManager_block_142" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_142" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        EnemyManager.getInstance().configure(/* param start [id:"FloorManager_value_298" type:"number"] */0/* param end [id:"FloorManager_value_298"] */, new int[/* param start [id:"FloorManager_value_299" type:"number"] */0/* param end [id:"FloorManager_value_299"] */], /* param start [id:"FloorManager_value_300" type:"number"] */1.0/* param end [id:"FloorManager_value_300"] */, /* param start [id:"FloorManager_value_301" type:"number"] */0/* param end [id:"FloorManager_value_301"] */);
        /* block end [id:"FloorManager_block_142"] */
        /* block comment [block:"FloorManager_block_143" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_143" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        EnemyManager.getInstance().clearAll();
        /* block end [id:"FloorManager_block_143"] */
        /* block comment [block:"FloorManager_block_144" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_144" category:"Variables" label:"set clearAll" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        LootDrop.clearAll();
        /* block end [id:"FloorManager_block_144"] */
        /* block comment [block:"FloorManager_block_145" text:"Calls the applyFloor method using its declared arguments."] */
        /* block start [id:"FloorManager_block_145" category:"My Blocks" label:"call apply floor" shape:"command" docs:"Calls the applyFloor method using its declared arguments."] */
        applyFloor();
        /* block end [id:"FloorManager_block_145"] */
    }

    private void descend() {
        /* block comment [block:"FloorManager_block_146" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"FloorManager_block_146" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        currentFloor++;
        /* block end [id:"FloorManager_block_146"] */
        /* block comment [block:"FloorManager_block_28" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_28" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (currentFloor > /* param start [id:"FloorManager_value_302" type:"number"] */7/* param end [id:"FloorManager_value_302"] */) {
            /* block comment [block:"FloorManager_block_147" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"FloorManager_block_147" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            victory = /* param start [id:"FloorManager_value_303" type:"boolean"] */true/* param end [id:"FloorManager_value_303"] */;
            /* block end [id:"FloorManager_block_147"] */
            /* block comment [block:"FloorManager_block_148" text:"Runs the hide action. Values in this block are editable inputs."] */
            /* block start [id:"FloorManager_block_148" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
            stairs.hide();
            /* block end [id:"FloorManager_block_148"] */
            /* block comment [block:"FloorManager_block_149" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"FloorManager_block_149" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().setVariable(/* param start [id:"FloorManager_value_304" type:"text"] */"Status"/* param end [id:"FloorManager_value_304"] */, /* param start [id:"FloorManager_value_305" type:"text"] */"VICTORY! All 7 floors cleared"/* param end [id:"FloorManager_value_305"] */);
            /* block end [id:"FloorManager_block_149"] */
            /* block comment [block:"FloorManager_block_150" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"FloorManager_block_150" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().broadcast(/* param start [id:"FloorManager_value_306" type:"text"] */"victory"/* param end [id:"FloorManager_value_306"] */);
            /* block end [id:"FloorManager_block_150"] */
            /* block comment [block:"FloorManager_block_151" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"FloorManager_block_151" category:"Variables" label:"set victory" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            MusicDirector.victory();
            /* block end [id:"FloorManager_block_151"] */
            /* block comment [block:"FloorManager_block_152" text:"Returns a value or ends the current method."] */
            /* block start [id:"FloorManager_block_152" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"FloorManager_block_152"] */
        }
        /* block end [id:"FloorManager_block_28"] */
        /* block comment [block:"FloorManager_block_153" text:"Calls the applyFloor method using its declared arguments."] */
        /* block start [id:"FloorManager_block_153" category:"My Blocks" label:"call apply floor" shape:"command" docs:"Calls the applyFloor method using its declared arguments."] */
        applyFloor();
        /* block end [id:"FloorManager_block_153"] */
    }

    private void applyFloor() {
        /* block comment [block:"FloorManager_block_154" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_154" category:"Variables" label:"set def" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        FloorDef def = floors.get(currentFloor - /* param start [id:"FloorManager_value_307" type:"number"] */1/* param end [id:"FloorManager_value_307"] */);
        /* block end [id:"FloorManager_block_154"] */
        /* block comment [block:"FloorManager_block_155" text:"Calls the applyTint method using its declared arguments."] */
        /* block start [id:"FloorManager_block_155" category:"My Blocks" label:"call apply tint" shape:"command" docs:"Calls the applyTint method using its declared arguments."] */
        applyTint(def.tr, def.tg, def.tb);
        /* block end [id:"FloorManager_block_155"] */
        /* block comment [block:"FloorManager_block_156" text:"Calls the buildWalls method using its declared arguments."] */
        /* block start [id:"FloorManager_block_156" category:"My Blocks" label:"call build walls" shape:"command" docs:"Calls the buildWalls method using its declared arguments."] */
        buildWalls(def);
        /* block end [id:"FloorManager_block_156"] */
        /* block comment [block:"FloorManager_block_157" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_157" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        EnemyManager.getInstance().configure(def.limit, def.mix, def.hpMul, def.dmgBonus);
        /* block end [id:"FloorManager_block_157"] */
        /* block comment [block:"FloorManager_block_158" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_158" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        EnemyManager.getInstance().clearAll();
        /* block end [id:"FloorManager_block_158"] */
        /* block comment [block:"FloorManager_block_159" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"FloorManager_block_159" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"FloorManager_block_29" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_29" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (GameState.isGameplay()) EnemyManager.getInstance().startSpawnGrace(SPAWN_GRACE_MS);
        /* block end [id:"FloorManager_block_159"] */
        /* block end [id:"FloorManager_block_29"] */

        /* block comment [block:"FloorManager_block_160" text:"Runs the go to action. Values in this block are editable inputs."] */
        /* block start [id:"FloorManager_block_160" category:"Motion" label:"go to" shape:"command" docs:"Runs the go to action. Values in this block are editable inputs."] */
        stairs.goTo(def.stairX, def.stairY);
        /* block end [id:"FloorManager_block_160"] */
        /* block comment [block:"FloorManager_block_161" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"FloorManager_block_161" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"FloorManager_block_30" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_30" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (chest != /* param start [id:"FloorManager_value_308" type:"value"] */null/* param end [id:"FloorManager_value_308"] */) chest.resetForFloor(currentFloor, def.chestX, def.chestY);
        /* block end [id:"FloorManager_block_161"] */
        /* block end [id:"FloorManager_block_30"] */
        // resetForFloor shows the chest; keep it hidden until gameplay.
        /* block comment [block:"FloorManager_block_31" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_31" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!GameState.isGameplay()) {
            /* block comment [block:"FloorManager_block_162" text:"Runs the hide action. Values in this block are editable inputs."] */
            /* block start [id:"FloorManager_block_162" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
            stairs.hide();
            /* block end [id:"FloorManager_block_162"] */
            /* block comment [block:"FloorManager_block_163" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"FloorManager_block_163" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"FloorManager_block_32" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"FloorManager_block_32" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (stairs.minimapSprite != /* param start [id:"FloorManager_value_309" type:"value"] */null/* param end [id:"FloorManager_value_309"] */) stairs.minimapSprite.hide();
            /* block end [id:"FloorManager_block_163"] */
            /* block end [id:"FloorManager_block_32"] */
            /* block comment [block:"FloorManager_block_33" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"FloorManager_block_33" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (chest != /* param start [id:"FloorManager_value_310" type:"value"] */null/* param end [id:"FloorManager_value_310"] */) {
                /* block comment [block:"FloorManager_block_164" text:"Runs the hide action. Values in this block are editable inputs."] */
                /* block start [id:"FloorManager_block_164" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
                chest.hide();
                /* block end [id:"FloorManager_block_164"] */
                /* block comment [block:"FloorManager_block_165" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"FloorManager_block_165" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"FloorManager_block_34" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"FloorManager_block_34" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (chest.minimapSprite != /* param start [id:"FloorManager_value_311" type:"value"] */null/* param end [id:"FloorManager_value_311"] */) chest.minimapSprite.hide();
                /* block end [id:"FloorManager_block_165"] */
                /* block end [id:"FloorManager_block_34"] */
            }
            /* block end [id:"FloorManager_block_33"] */
        } else {
            /* block comment [block:"FloorManager_block_166" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"FloorManager_block_166" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"FloorManager_block_35" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"FloorManager_block_35" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!victory) stairs.show();
            /* block end [id:"FloorManager_block_166"] */
            /* block end [id:"FloorManager_block_35"] */
            /* block comment [block:"FloorManager_block_167" text:"Calls the update method using its declared arguments."] */
            /* block start [id:"FloorManager_block_167" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
            stairs.update();
            /* block end [id:"FloorManager_block_167"] */
        }
        /* block end [id:"FloorManager_block_31"] */

        /* block comment [block:"FloorManager_block_36" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_36" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (currentFloor == /* param start [id:"FloorManager_value_312" type:"number"] */7/* param end [id:"FloorManager_value_312"] */) {
            // The Minitaur guards the sealed stairs. Kill it to win.
            /* block comment [block:"FloorManager_block_168" text:"Runs the minitaur action. Values in this block are editable inputs."] */
            /* block start [id:"FloorManager_block_168" category:"My Blocks" label:"minitaur" shape:"command" docs:"Runs the minitaur action. Values in this block are editable inputs."] */
            bossRef = new Minitaur(/* param start [id:"FloorManager_value_313" type:"text"] */"MinitaurBoss"/* param end [id:"FloorManager_value_313"] */);
            /* block end [id:"FloorManager_block_168"] */
            /* block comment [block:"FloorManager_block_169" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"FloorManager_block_169" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            EnemyManager.getInstance().addEnemy(bossRef, /* param start [id:"FloorManager_value_314" type:"number"] */350/* param end [id:"FloorManager_value_314"] */, /* param start [id:"FloorManager_value_315" type:"number"] */250/* param end [id:"FloorManager_value_315"] */);
            /* block end [id:"FloorManager_block_169"] */
            /* block comment [block:"FloorManager_block_37" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"FloorManager_block_37" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!GameState.isGameplay()) {
                /* block comment [block:"FloorManager_block_170" text:"Runs the hide action. Values in this block are editable inputs."] */
                /* block start [id:"FloorManager_block_170" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
                bossRef.hide();
                /* block end [id:"FloorManager_block_170"] */
                /* block comment [block:"FloorManager_block_171" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"FloorManager_block_171" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"FloorManager_block_38" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"FloorManager_block_38" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (bossRef.minimapSprite != /* param start [id:"FloorManager_value_316" type:"value"] */null/* param end [id:"FloorManager_value_316"] */) bossRef.minimapSprite.hide();
                /* block end [id:"FloorManager_block_171"] */
                /* block end [id:"FloorManager_block_38"] */
                /* block comment [block:"FloorManager_block_172" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"FloorManager_block_172" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"FloorManager_block_39" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"FloorManager_block_39" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (bossRef.hpBar != /* param start [id:"FloorManager_value_317" type:"value"] */null/* param end [id:"FloorManager_value_317"] */) bossRef.hpBar.hide();
                /* block end [id:"FloorManager_block_172"] */
                /* block end [id:"FloorManager_block_39"] */
            }
            /* block end [id:"FloorManager_block_37"] */
        } else {
            /* block comment [block:"FloorManager_block_173" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"FloorManager_block_173" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            bossRef = /* param start [id:"FloorManager_value_318" type:"value"] */null/* param end [id:"FloorManager_value_318"] */;
            /* block end [id:"FloorManager_block_173"] */
        }
        /* block end [id:"FloorManager_block_36"] */

        // During character creation the title theme owns the audio and the
        // menu owns the screen: skip floor music / HUD / reset broadcasts.
        /* block comment [block:"FloorManager_block_40" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_40" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!GameState.isGameplay()) {
            /* block comment [block:"FloorManager_block_174" text:"Returns a value or ends the current method."] */
            /* block start [id:"FloorManager_block_174" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"FloorManager_block_174"] */
        }
        /* block end [id:"FloorManager_block_40"] */

        // Restore the normal spawn mix after a new-run cleanup used a zero cap.
        /* block comment [block:"FloorManager_block_175" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_175" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        EnemyManager.getInstance().configure(def.limit, def.mix, def.hpMul, def.dmgBonus);
        /* block end [id:"FloorManager_block_175"] */
        /* block comment [block:"FloorManager_block_176" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_176" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        EnemyManager.getInstance().startSpawnGrace(SPAWN_GRACE_MS);
        /* block end [id:"FloorManager_block_176"] */
        /* block comment [block:"FloorManager_block_177" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_177" category:"Variables" label:"set onFloor" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        MusicDirector.onFloor(def);
        /* block end [id:"FloorManager_block_177"] */

        // Tell the player to reset to spawn, then report
        /* block comment [block:"FloorManager_block_178" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_178" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().broadcast(/* param start [id:"FloorManager_value_319" type:"text"] */"floorChanged"/* param end [id:"FloorManager_value_319"] */);
        /* block end [id:"FloorManager_block_178"] */
        /* block comment [block:"FloorManager_block_41" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_41" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (PlayerStats.ready && PlayerStats.maxMana > /* param start [id:"FloorManager_value_320" type:"number"] */0/* param end [id:"FloorManager_value_320"] */) {
            /* block comment [block:"FloorManager_block_179" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"FloorManager_block_179" category:"Variables" label:"set restoreMana" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            PlayerStats.restoreMana(Math.max(/* param start [id:"FloorManager_value_321" type:"number"] */1/* param end [id:"FloorManager_value_321"] */, PlayerStats.maxMana / /* param start [id:"FloorManager_value_322" type:"number"] */4/* param end [id:"FloorManager_value_322"] */));
            /* block end [id:"FloorManager_block_179"] */
        }
        /* block end [id:"FloorManager_block_41"] */
        /* block comment [block:"FloorManager_block_180" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_180" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().setVariable(/* param start [id:"FloorManager_value_323" type:"text"] */"Floor"/* param end [id:"FloorManager_value_323"] */, currentFloor + /* param start [id:"FloorManager_value_324" type:"text"] */"/7 "/* param end [id:"FloorManager_value_324"] */ + def.name);
        /* block end [id:"FloorManager_block_180"] */
        /* block comment [block:"FloorManager_block_181" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_181" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().setVariable(/* param start [id:"FloorManager_value_325" type:"text"] */"Msg"/* param end [id:"FloorManager_value_325"] */, /* param start [id:"FloorManager_value_326" type:"text"] */"Floor "/* param end [id:"FloorManager_value_326"] */ + currentFloor + /* param start [id:"FloorManager_value_327" type:"text"] */": "/* param end [id:"FloorManager_value_327"] */ + def.name);
        /* block end [id:"FloorManager_block_181"] */
    }

    // ---------- walls ----------

    /** Arena border (just outside the mapped +-720/+-540 region). */
    private static final int[][] BORDER = {
        {0, 600, 1560, 120}, {0, -600, 1560, 120},
        {-780, 0, 120, 1320}, {780, 0, 120, 1320}
    };

    private void addWall(int cx, int cy, int w, int h, String tex) {
        /* block comment [block:"FloorManager_block_182" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_182" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Scroller2d.getInstance().addCollider(cx - w / /* param start [id:"FloorManager_value_328" type:"number"] */2/* param end [id:"FloorManager_value_328"] */, cy - h / /* param start [id:"FloorManager_value_329" type:"number"] */2/* param end [id:"FloorManager_value_329"] */, w, h);
        /* block end [id:"FloorManager_block_182"] */
        /* block comment [block:"FloorManager_block_183" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_183" category:"Variables" label:"set sprite" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        WallSprite sprite = new WallSprite(cx, cy, w, h, tex);
        /* block end [id:"FloorManager_block_183"] */
        /* block comment [block:"FloorManager_block_184" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_184" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().addSprite(sprite);
        /* block end [id:"FloorManager_block_184"] */
        /* block comment [block:"FloorManager_block_42" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_42" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!GameState.isGameplay()) {
            /* block comment [block:"FloorManager_block_185" text:"Runs the hide action. Values in this block are editable inputs."] */
            /* block start [id:"FloorManager_block_185" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
            sprite.hide();
            /* block end [id:"FloorManager_block_185"] */
            /* block comment [block:"FloorManager_block_186" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"FloorManager_block_186" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"FloorManager_block_43" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"FloorManager_block_43" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (sprite.minimapSprite != /* param start [id:"FloorManager_value_330" type:"value"] */null/* param end [id:"FloorManager_value_330"] */) sprite.minimapSprite.hide();
            /* block end [id:"FloorManager_block_186"] */
            /* block end [id:"FloorManager_block_43"] */
        }
        /* block end [id:"FloorManager_block_42"] */
        /* block comment [block:"FloorManager_block_187" text:"Calls the add method using its declared arguments."] */
        /* block start [id:"FloorManager_block_187" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
        wallSprites.add(sprite);
        /* block end [id:"FloorManager_block_187"] */
        wallRects.add(new int[]{cx, cy, w, h}/* block comment [block:"FloorManager_block_188" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"FloorManager_block_188" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        );
        /* block end [id:"FloorManager_block_188"] */
    }

    private void buildWalls(FloorDef def) {
        /* block comment [block:"FloorManager_block_44" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"FloorManager_block_44" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (WallSprite s : wallSprites) {
            /* block comment [block:"FloorManager_block_189" text:"Runs the hide action. Values in this block are editable inputs."] */
            /* block start [id:"FloorManager_block_189" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
            s.hide();
            /* block end [id:"FloorManager_block_189"] */
            /* block comment [block:"FloorManager_block_45" text:"Runs work safely and handles matching errors in a catch branch."] */
            /* block start [id:"FloorManager_block_45" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
            try { /* block comment [block:"FloorManager_block_190" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"FloorManager_block_190" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                Scroller2d.getInstance().unregister(s);
                /* block end [id:"FloorManager_block_190"] */ }
            /* block end [id:"FloorManager_block_45"] */ catch (Exception ignored) {}
        }
        /* block end [id:"FloorManager_block_44"] */
        /* block comment [block:"FloorManager_block_191" text:"Calls the clear method using its declared arguments."] */
        /* block start [id:"FloorManager_block_191" category:"My Blocks" label:"call clear" shape:"command" docs:"Calls the clear method using its declared arguments."] */
        wallSprites.clear();
        /* block end [id:"FloorManager_block_191"] */
        /* block comment [block:"FloorManager_block_192" text:"Calls the clear method using its declared arguments."] */
        /* block start [id:"FloorManager_block_192" category:"My Blocks" label:"call clear" shape:"command" docs:"Calls the clear method using its declared arguments."] */
        wallRects.clear();
        /* block end [id:"FloorManager_block_192"] */
        /* block comment [block:"FloorManager_block_193" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_193" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Scroller2d.getInstance().clearColliders();
        /* block end [id:"FloorManager_block_193"] */
        /* block comment [block:"FloorManager_block_194" text:"Runs the for action. Values in this block are editable inputs."] */
        /* block start [id:"FloorManager_block_194" category:"My Blocks" label:"for" shape:"command" docs:"Runs the for action. Values in this block are editable inputs."] */
        /* block comment [block:"FloorManager_block_46" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"FloorManager_block_46" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int[] r : BORDER) addWall(r[/* param start [id:"FloorManager_value_331" type:"number"] */0/* param end [id:"FloorManager_value_331"] */], r[/* param start [id:"FloorManager_value_332" type:"number"] */1/* param end [id:"FloorManager_value_332"] */], r[/* param start [id:"FloorManager_value_333" type:"number"] */2/* param end [id:"FloorManager_value_333"] */], r[/* param start [id:"FloorManager_value_334" type:"number"] */3/* param end [id:"FloorManager_value_334"] */], def.wallTex);
        /* block end [id:"FloorManager_block_194"] */
        /* block end [id:"FloorManager_block_46"] */
        /* block comment [block:"FloorManager_block_47" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"FloorManager_block_47" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (def.walls != /* param start [id:"FloorManager_value_335" type:"value"] */null/* param end [id:"FloorManager_value_335"] */) {
            /* block comment [block:"FloorManager_block_195" text:"Runs the for action. Values in this block are editable inputs."] */
            /* block start [id:"FloorManager_block_195" category:"My Blocks" label:"for" shape:"command" docs:"Runs the for action. Values in this block are editable inputs."] */
            /* block comment [block:"FloorManager_block_48" text:"Repeats the nested operations for each loop turn."] */
            /* block start [id:"FloorManager_block_48" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
            for (int[] r : def.walls) addWall(r[/* param start [id:"FloorManager_value_336" type:"number"] */0/* param end [id:"FloorManager_value_336"] */], r[/* param start [id:"FloorManager_value_337" type:"number"] */1/* param end [id:"FloorManager_value_337"] */], r[/* param start [id:"FloorManager_value_338" type:"number"] */2/* param end [id:"FloorManager_value_338"] */], r[/* param start [id:"FloorManager_value_339" type:"number"] */3/* param end [id:"FloorManager_value_339"] */], def.wallTex);
            /* block end [id:"FloorManager_block_195"] */
            /* block end [id:"FloorManager_block_48"] */
        }
        /* block end [id:"FloorManager_block_47"] */
        /* block comment [block:"FloorManager_block_196" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_196" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Scroller2d.getInstance().update();
        /* block end [id:"FloorManager_block_196"] */
    }

    // ---------- tile tinting ----------

    private static BufferedImage loadTile(String id) {
        /* block comment [block:"FloorManager_block_197" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_197" category:"Variables" label:"set path" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        String path = /* param start [id:"FloorManager_value_340" type:"text"] */"jscratch_ws/assets/"/* param end [id:"FloorManager_value_340"] */ + id + /* param start [id:"FloorManager_value_341" type:"text"] */".png"/* param end [id:"FloorManager_value_341"] */;
        /* block end [id:"FloorManager_block_197"] */
        /* block comment [block:"FloorManager_block_49" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"FloorManager_block_49" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"FloorManager_block_198" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"FloorManager_block_198" category:"Variables" label:"set f" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            File f = new File(path);
            /* block end [id:"FloorManager_block_198"] */
            /* block comment [block:"FloorManager_block_50" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"FloorManager_block_50" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!f.exists()) {
                /* block comment [block:"FloorManager_block_199" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"FloorManager_block_199" category:"Variables" label:"set root" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                String root = System.getProperty(/* param start [id:"FloorManager_value_342" type:"text"] */"jscratch.project.root"/* param end [id:"FloorManager_value_342"] */);
                /* block end [id:"FloorManager_block_199"] */
                /* block comment [block:"FloorManager_block_200" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"FloorManager_block_200" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"FloorManager_block_51" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"FloorManager_block_51" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (root != /* param start [id:"FloorManager_value_343" type:"value"] */null/* param end [id:"FloorManager_value_343"] */) f = new File(root, path);
                /* block end [id:"FloorManager_block_200"] */
                /* block end [id:"FloorManager_block_51"] */
            }
            /* block end [id:"FloorManager_block_50"] */
            /* block comment [block:"FloorManager_block_201" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"FloorManager_block_201" category:"Variables" label:"set img" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            BufferedImage img = ImageIO.read(f);
            /* block end [id:"FloorManager_block_201"] */
            /* block comment [block:"FloorManager_block_202" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"FloorManager_block_202" category:"Variables" label:"set copy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            BufferedImage copy = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            /* block end [id:"FloorManager_block_202"] */
            /* block comment [block:"FloorManager_block_203" text:"Calls the drawImage method using its declared arguments."] */
            /* block start [id:"FloorManager_block_203" category:"My Blocks" label:"call draw image" shape:"command" docs:"Calls the drawImage method using its declared arguments."] */
            copy.getGraphics().drawImage(img, /* param start [id:"FloorManager_value_344" type:"number"] */0/* param end [id:"FloorManager_value_344"] */, /* param start [id:"FloorManager_value_345" type:"number"] */0/* param end [id:"FloorManager_value_345"] */, /* param start [id:"FloorManager_value_346" type:"value"] */null/* param end [id:"FloorManager_value_346"] */);
            /* block end [id:"FloorManager_block_203"] */
            /* block comment [block:"FloorManager_block_204" text:"Returns a value or ends the current method."] */
            /* block start [id:"FloorManager_block_204" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return copy;
            /* block end [id:"FloorManager_block_204"] */
        }
        /* block end [id:"FloorManager_block_49"] */ catch (Exception e) {
            /* block comment [block:"FloorManager_block_205" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"FloorManager_block_205" category:"Variables" label:"set fallback" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            BufferedImage fallback = new BufferedImage(/* param start [id:"FloorManager_value_347" type:"number"] */480/* param end [id:"FloorManager_value_347"] */, /* param start [id:"FloorManager_value_348" type:"number"] */360/* param end [id:"FloorManager_value_348"] */, BufferedImage.TYPE_INT_ARGB);
            /* block end [id:"FloorManager_block_205"] */
            /* block comment [block:"FloorManager_block_206" text:"Returns a value or ends the current method."] */
            /* block start [id:"FloorManager_block_206" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return fallback;
            /* block end [id:"FloorManager_block_206"] */
        }
    }

    /** Re-tints the 9 shared tile costumes in place (world + minimap follow). */
    private void applyTint(float tr, float tg, float tb) {
        /* block comment [block:"FloorManager_block_52" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"FloorManager_block_52" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int i = /* param start [id:"FloorManager_value_349" type:"number"] */0/* param end [id:"FloorManager_value_349"] */; i < tileIds.size(); i++) {
            /* block comment [block:"FloorManager_block_207" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"FloorManager_block_207" category:"Variables" label:"set id" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            String id = tileIds.get(i);
            /* block end [id:"FloorManager_block_207"] */
            /* block comment [block:"FloorManager_block_208" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"FloorManager_block_208" category:"Variables" label:"set src" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            BufferedImage src = pristine.get(i);
            /* block end [id:"FloorManager_block_208"] */
            /* block comment [block:"FloorManager_block_209" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"FloorManager_block_209" category:"Variables" label:"set live" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Costume live = ResourcePool.getCostume(id, /* param start [id:"FloorManager_value_350" type:"text"] */"jscratch_ws/assets/"/* param end [id:"FloorManager_value_350"] */ + id + /* param start [id:"FloorManager_value_351" type:"text"] */".png"/* param end [id:"FloorManager_value_351"] */, /* param start [id:"FloorManager_value_352" type:"number"] */240/* param end [id:"FloorManager_value_352"] */, /* param start [id:"FloorManager_value_353" type:"number"] */180/* param end [id:"FloorManager_value_353"] */);
            /* block end [id:"FloorManager_block_209"] */
            /* block comment [block:"FloorManager_block_210" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"FloorManager_block_210" category:"Variables" label:"set dst" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            BufferedImage dst = live.getImage();
            /* block end [id:"FloorManager_block_210"] */
            /* block comment [block:"FloorManager_block_211" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"FloorManager_block_211" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"FloorManager_block_53" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"FloorManager_block_53" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (dst == /* param start [id:"FloorManager_value_354" type:"value"] */null/* param end [id:"FloorManager_value_354"] */ || dst.getWidth() != src.getWidth() || dst.getHeight() != src.getHeight()) continue;
            /* block end [id:"FloorManager_block_211"] */
            /* block end [id:"FloorManager_block_53"] */
            /* block comment [block:"FloorManager_block_212" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"FloorManager_block_212" category:"Variables" label:"set w" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int w = src.getWidth();
            /* block end [id:"FloorManager_block_212"] */
            /* block comment [block:"FloorManager_block_213" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"FloorManager_block_213" category:"Variables" label:"set h" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int h = src.getHeight();
            /* block end [id:"FloorManager_block_213"] */
            // Bulk row ops: one getRGB/setRGB per tile instead of per pixel.
            /* block comment [block:"FloorManager_block_214" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"FloorManager_block_214" category:"Variables" label:"set pixels" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int[] pixels = src.getRGB(/* param start [id:"FloorManager_value_355" type:"number"] */0/* param end [id:"FloorManager_value_355"] */, /* param start [id:"FloorManager_value_356" type:"number"] */0/* param end [id:"FloorManager_value_356"] */, w, h, /* param start [id:"FloorManager_value_357" type:"value"] */null/* param end [id:"FloorManager_value_357"] */, /* param start [id:"FloorManager_value_358" type:"number"] */0/* param end [id:"FloorManager_value_358"] */, w);
            /* block end [id:"FloorManager_block_214"] */
            /* block comment [block:"FloorManager_block_54" text:"Repeats the nested operations for each loop turn."] */
            /* block start [id:"FloorManager_block_54" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
            for (int p = /* param start [id:"FloorManager_value_359" type:"number"] */0/* param end [id:"FloorManager_value_359"] */; p < pixels.length; p++) {
                /* block comment [block:"FloorManager_block_215" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"FloorManager_block_215" category:"Variables" label:"set argb" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int argb = pixels[p];
                /* block end [id:"FloorManager_block_215"] */
                /* block comment [block:"FloorManager_block_216" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"FloorManager_block_216" category:"Variables" label:"set a" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int a = (argb >>> /* param start [id:"FloorManager_value_360" type:"number"] */24/* param end [id:"FloorManager_value_360"] */) & /* param start [id:"FloorManager_value_361" type:"number"] */0xFF/* param end [id:"FloorManager_value_361"] */;
                /* block end [id:"FloorManager_block_216"] */
                /* block comment [block:"FloorManager_block_217" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"FloorManager_block_217" category:"Variables" label:"set r" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int r = Math.min(/* param start [id:"FloorManager_value_362" type:"number"] */255/* param end [id:"FloorManager_value_362"] */, (int) (((argb >> /* param start [id:"FloorManager_value_363" type:"number"] */16/* param end [id:"FloorManager_value_363"] */) & /* param start [id:"FloorManager_value_364" type:"number"] */0xFF/* param end [id:"FloorManager_value_364"] */) * tr));
                /* block end [id:"FloorManager_block_217"] */
                /* block comment [block:"FloorManager_block_218" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"FloorManager_block_218" category:"Variables" label:"set gr" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int gr = Math.min(/* param start [id:"FloorManager_value_365" type:"number"] */255/* param end [id:"FloorManager_value_365"] */, (int) (((argb >> /* param start [id:"FloorManager_value_366" type:"number"] */8/* param end [id:"FloorManager_value_366"] */) & /* param start [id:"FloorManager_value_367" type:"number"] */0xFF/* param end [id:"FloorManager_value_367"] */) * tg));
                /* block end [id:"FloorManager_block_218"] */
                /* block comment [block:"FloorManager_block_219" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"FloorManager_block_219" category:"Variables" label:"set b" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int b = Math.min(/* param start [id:"FloorManager_value_368" type:"number"] */255/* param end [id:"FloorManager_value_368"] */, (int) ((argb & /* param start [id:"FloorManager_value_369" type:"number"] */0xFF/* param end [id:"FloorManager_value_369"] */) * tb));
                /* block end [id:"FloorManager_block_219"] */
                /* block comment [block:"FloorManager_block_220" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"FloorManager_block_220" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                pixels[p] = (a << /* param start [id:"FloorManager_value_370" type:"number"] */24/* param end [id:"FloorManager_value_370"] */) | (r << /* param start [id:"FloorManager_value_371" type:"number"] */16/* param end [id:"FloorManager_value_371"] */) | (gr << /* param start [id:"FloorManager_value_372" type:"number"] */8/* param end [id:"FloorManager_value_372"] */) | b;
                /* block end [id:"FloorManager_block_220"] */
            }
            /* block end [id:"FloorManager_block_54"] */
            /* block comment [block:"FloorManager_block_221" text:"Runs the set rgb action. Values in this block are editable inputs."] */
            /* block start [id:"FloorManager_block_221" category:"Variables" label:"set rgb" shape:"command" docs:"Runs the set rgb action. Values in this block are editable inputs."] */
            dst.setRGB(/* param start [id:"FloorManager_value_373" type:"number"] */0/* param end [id:"FloorManager_value_373"] */, /* param start [id:"FloorManager_value_374" type:"number"] */0/* param end [id:"FloorManager_value_374"] */, w, h, pixels, /* param start [id:"FloorManager_value_375" type:"number"] */0/* param end [id:"FloorManager_value_375"] */, w);
            /* block end [id:"FloorManager_block_221"] */
        }
        /* block end [id:"FloorManager_block_52"] */
        /* block comment [block:"FloorManager_block_222" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorManager_block_222" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Scroller2d.getInstance().update();
        /* block end [id:"FloorManager_block_222"] */
    }
}
