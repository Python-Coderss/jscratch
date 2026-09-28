package generated;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
* Bitmap text renderer for the generated game. Every glyph comes from a PNG in
* jscratch_ws/assets: lowercase a-z use a.png..z.png, uppercase A-Z use
* captial_A.png..captial_Z.png (separate files, never case folded), digits use
* 0.png..9.png and the punctuation files use their own names. No java.awt.Font
* and no java.awt.FontMetrics are used for game text.
*
* Metrics: glyph art is ink-cropped and measured in units where the cap height
* is 153 units above the baseline, the x-height is 110 units, descenders hang 21
* units below it. A Style carries the requested size (java.awt-like px) plus the
* bold flag; the cap height is derived so line spacing keeps matching the
* previous Font based layout.
*/
public final class BitmapFont {

    private static final String ASSET_DIR = "jscratch_ws/assets";
    private static final double CAP_UNITS = 153.0;
    private static final double DESCENT_UNITS = 21.0;
    private static final double SIZE_TO_CAP = 0.72;

    private static final Map<Character, String> PUNCT = new HashMap<Character, String>();
    private static final Map<Character, Glyph> GLYPHS = new HashMap<Character, Glyph>();
    private static final Map<String, BufferedImage> TINTED = new HashMap<String, BufferedImage>();
    private static final Map<String, BufferedImage> SCALED = new HashMap<String, BufferedImage>();

    static {
        /* block comment [block:"BitmapFont_block_47" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_47" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_193" type:"character"] */'&'/* param end [id:"BitmapFont_value_193"] */, /* param start [id:"BitmapFont_value_194" type:"text"] */"andpersayand.png"/* param end [id:"BitmapFont_value_194"] */);
        /* block end [id:"BitmapFont_block_47"] */
        /* block comment [block:"BitmapFont_block_48" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_48" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_195" type:"character"] */'*'/* param end [id:"BitmapFont_value_195"] */, /* param start [id:"BitmapFont_value_196" type:"text"] */"asterisk.png"/* param end [id:"BitmapFont_value_196"] */);
        /* block end [id:"BitmapFont_block_48"] */
        /* block comment [block:"BitmapFont_block_49" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_49" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_197" type:"character"] */'@'/* param end [id:"BitmapFont_value_197"] */, /* param start [id:"BitmapFont_value_198" type:"text"] */"at.png"/* param end [id:"BitmapFont_value_198"] */);
        /* block end [id:"BitmapFont_block_49"] */
        /* block comment [block:"BitmapFont_block_50" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_50" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_199" type:"character"] */'`'/* param end [id:"BitmapFont_value_199"] */, /* param start [id:"BitmapFont_value_200" type:"text"] */"backtick.png"/* param end [id:"BitmapFont_value_200"] */);
        /* block end [id:"BitmapFont_block_50"] */
        /* block comment [block:"BitmapFont_block_51" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_51" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_201" type:"character"] */'\\'/* param end [id:"BitmapFont_value_201"] */, /* param start [id:"BitmapFont_value_202" type:"text"] */"back_slash.png"/* param end [id:"BitmapFont_value_202"] */);
        /* block end [id:"BitmapFont_block_51"] */
        /* block comment [block:"BitmapFont_block_52" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_52" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_203" type:"character"] */'^'/* param end [id:"BitmapFont_value_203"] */, /* param start [id:"BitmapFont_value_204" type:"text"] */"caret.png"/* param end [id:"BitmapFont_value_204"] */);
        /* block end [id:"BitmapFont_block_52"] */
        /* block comment [block:"BitmapFont_block_53" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_53" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_205" type:"character"] */'>'/* param end [id:"BitmapFont_value_205"] */, /* param start [id:"BitmapFont_value_206" type:"text"] */"close_angle_bracket.png"/* param end [id:"BitmapFont_value_206"] */);
        /* block end [id:"BitmapFont_block_53"] */
        /* block comment [block:"BitmapFont_block_54" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_54" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_207" type:"character"] */')'/* param end [id:"BitmapFont_value_207"] */, /* param start [id:"BitmapFont_value_208" type:"text"] */"close_bracket.png"/* param end [id:"BitmapFont_value_208"] */);
        /* block end [id:"BitmapFont_block_54"] */
        /* block comment [block:"BitmapFont_block_55" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_55" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_209" type:"character"] */'}'/* param end [id:"BitmapFont_value_209"] */, /* param start [id:"BitmapFont_value_210" type:"text"] */"close_curly_bracket.png"/* param end [id:"BitmapFont_value_210"] */);
        /* block end [id:"BitmapFont_block_55"] */
        /* block comment [block:"BitmapFont_block_56" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_56" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_211" type:"character"] */']'/* param end [id:"BitmapFont_value_211"] */, /* param start [id:"BitmapFont_value_212" type:"text"] */"close_square_bracket.png"/* param end [id:"BitmapFont_value_212"] */);
        /* block end [id:"BitmapFont_block_56"] */
        /* block comment [block:"BitmapFont_block_57" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_57" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_213" type:"character"] */':'/* param end [id:"BitmapFont_value_213"] */, /* param start [id:"BitmapFont_value_214" type:"text"] */"colon.png"/* param end [id:"BitmapFont_value_214"] */);
        /* block end [id:"BitmapFont_block_57"] */
        /* block comment [block:"BitmapFont_block_58" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_58" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_215" type:"character"] */','/* param end [id:"BitmapFont_value_215"] */, /* param start [id:"BitmapFont_value_216" type:"text"] */"comma.png"/* param end [id:"BitmapFont_value_216"] */);
        /* block end [id:"BitmapFont_block_58"] */
        /* block comment [block:"BitmapFont_block_59" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_59" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_217" type:"character"] */'-'/* param end [id:"BitmapFont_value_217"] */, /* param start [id:"BitmapFont_value_218" type:"text"] */"dash.png"/* param end [id:"BitmapFont_value_218"] */);
        /* block end [id:"BitmapFont_block_59"] */
        /* block comment [block:"BitmapFont_block_60" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_60" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_219" type:"character"] */'$'/* param end [id:"BitmapFont_value_219"] */, /* param start [id:"BitmapFont_value_220" type:"text"] */"dollar_sign.png"/* param end [id:"BitmapFont_value_220"] */);
        /* block end [id:"BitmapFont_block_60"] */
        /* block comment [block:"BitmapFont_block_61" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_61" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_221" type:"character"] */'"'/* param end [id:"BitmapFont_value_221"] */, /* param start [id:"BitmapFont_value_222" type:"text"] */"double_quote.png"/* param end [id:"BitmapFont_value_222"] */);
        /* block end [id:"BitmapFont_block_61"] */
        /* block comment [block:"BitmapFont_block_62" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_62" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_223" type:"character"] */'='/* param end [id:"BitmapFont_value_223"] */, /* param start [id:"BitmapFont_value_224" type:"text"] */"equals.png"/* param end [id:"BitmapFont_value_224"] */);
        /* block end [id:"BitmapFont_block_62"] */
        /* block comment [block:"BitmapFont_block_63" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_63" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_225" type:"character"] */'!'/* param end [id:"BitmapFont_value_225"] */, /* param start [id:"BitmapFont_value_226" type:"text"] */"exclamation_mark.png"/* param end [id:"BitmapFont_value_226"] */);
        /* block end [id:"BitmapFont_block_63"] */
        /* block comment [block:"BitmapFont_block_64" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_64" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_227" type:"character"] */'/'/* param end [id:"BitmapFont_value_227"] */, /* param start [id:"BitmapFont_value_228" type:"text"] */"forward_slash.png"/* param end [id:"BitmapFont_value_228"] */);
        /* block end [id:"BitmapFont_block_64"] */
        /* block comment [block:"BitmapFont_block_65" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_65" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_229" type:"character"] */'#'/* param end [id:"BitmapFont_value_229"] */, /* param start [id:"BitmapFont_value_230" type:"text"] */"hashtag.png"/* param end [id:"BitmapFont_value_230"] */);
        /* block end [id:"BitmapFont_block_65"] */
        /* block comment [block:"BitmapFont_block_66" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_66" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_231" type:"character"] */'<'/* param end [id:"BitmapFont_value_231"] */, /* param start [id:"BitmapFont_value_232" type:"text"] */"open_angle_bracket.png"/* param end [id:"BitmapFont_value_232"] */);
        /* block end [id:"BitmapFont_block_66"] */
        /* block comment [block:"BitmapFont_block_67" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_67" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_233" type:"character"] */'('/* param end [id:"BitmapFont_value_233"] */, /* param start [id:"BitmapFont_value_234" type:"text"] */"open_bracket.png"/* param end [id:"BitmapFont_value_234"] */);
        /* block end [id:"BitmapFont_block_67"] */
        /* block comment [block:"BitmapFont_block_68" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_68" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_235" type:"character"] */'{'/* param end [id:"BitmapFont_value_235"] */, /* param start [id:"BitmapFont_value_236" type:"text"] */"open_curly_bracket.png"/* param end [id:"BitmapFont_value_236"] */);
        /* block end [id:"BitmapFont_block_68"] */
        /* block comment [block:"BitmapFont_block_69" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_69" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_237" type:"character"] */'['/* param end [id:"BitmapFont_value_237"] */, /* param start [id:"BitmapFont_value_238" type:"text"] */"open_square_bracket.png"/* param end [id:"BitmapFont_value_238"] */);
        /* block end [id:"BitmapFont_block_69"] */
        /* block comment [block:"BitmapFont_block_70" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_70" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_239" type:"character"] */'%'/* param end [id:"BitmapFont_value_239"] */, /* param start [id:"BitmapFont_value_240" type:"text"] */"percent.png"/* param end [id:"BitmapFont_value_240"] */);
        /* block end [id:"BitmapFont_block_70"] */
        /* block comment [block:"BitmapFont_block_71" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_71" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_241" type:"character"] */'.'/* param end [id:"BitmapFont_value_241"] */, /* param start [id:"BitmapFont_value_242" type:"text"] */"period.png"/* param end [id:"BitmapFont_value_242"] */);
        /* block end [id:"BitmapFont_block_71"] */
        /* block comment [block:"BitmapFont_block_72" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_72" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_243" type:"character"] */'|'/* param end [id:"BitmapFont_value_243"] */, /* param start [id:"BitmapFont_value_244" type:"text"] */"pipe.png"/* param end [id:"BitmapFont_value_244"] */);
        /* block end [id:"BitmapFont_block_72"] */
        /* block comment [block:"BitmapFont_block_73" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_73" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_245" type:"character"] */'+'/* param end [id:"BitmapFont_value_245"] */, /* param start [id:"BitmapFont_value_246" type:"text"] */"plus.png"/* param end [id:"BitmapFont_value_246"] */);
        /* block end [id:"BitmapFont_block_73"] */
        /* block comment [block:"BitmapFont_block_74" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_74" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_247" type:"character"] */'?'/* param end [id:"BitmapFont_value_247"] */, /* param start [id:"BitmapFont_value_248" type:"text"] */"question_mark.png"/* param end [id:"BitmapFont_value_248"] */);
        /* block end [id:"BitmapFont_block_74"] */
        /* block comment [block:"BitmapFont_block_75" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_75" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_249" type:"character"] */'\''/* param end [id:"BitmapFont_value_249"] */, /* param start [id:"BitmapFont_value_250" type:"text"] */"quote.png"/* param end [id:"BitmapFont_value_250"] */);
        /* block end [id:"BitmapFont_block_75"] */
        /* block comment [block:"BitmapFont_block_76" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_76" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_251" type:"character"] */';'/* param end [id:"BitmapFont_value_251"] */, /* param start [id:"BitmapFont_value_252" type:"text"] */"semicolon.png"/* param end [id:"BitmapFont_value_252"] */);
        /* block end [id:"BitmapFont_block_76"] */
        /* block comment [block:"BitmapFont_block_77" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_77" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_253" type:"character"] */'~'/* param end [id:"BitmapFont_value_253"] */, /* param start [id:"BitmapFont_value_254" type:"text"] */"tilde.png"/* param end [id:"BitmapFont_value_254"] */);
        /* block end [id:"BitmapFont_block_77"] */
        /* block comment [block:"BitmapFont_block_78" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_78" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PUNCT.put(/* param start [id:"BitmapFont_value_255" type:"character"] */'_'/* param end [id:"BitmapFont_value_255"] */, /* param start [id:"BitmapFont_value_256" type:"text"] */"underscore.png"/* param end [id:"BitmapFont_value_256"] */);
        /* block end [id:"BitmapFont_block_78"] */
    }

    private BitmapFont() {
    }

    /** Requested text size plus bold flag; cap height is derived from size. */
    public static final class Style {
        /* block comment [block:"BitmapFont_block_79" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_79" category:"Variables" label:"set size" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public final int size;
        /* block end [id:"BitmapFont_block_79"] */
        /* block comment [block:"BitmapFont_block_80" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_80" category:"Variables" label:"set bold" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public final boolean bold;
        /* block end [id:"BitmapFont_block_80"] */
        /* block comment [block:"BitmapFont_block_81" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_81" category:"Variables" label:"set cap" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public final int cap;
        /* block end [id:"BitmapFont_block_81"] */

        Style(int size, boolean bold) {
            /* block comment [block:"BitmapFont_block_82" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"BitmapFont_block_82" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.size = size;
            /* block end [id:"BitmapFont_block_82"] */
            /* block comment [block:"BitmapFont_block_83" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"BitmapFont_block_83" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.bold = bold;
            /* block end [id:"BitmapFont_block_83"] */
            /* block comment [block:"BitmapFont_block_84" text:"Calls the max method using its declared arguments."] */
            /* block start [id:"BitmapFont_block_84" category:"My Blocks" label:"call max" shape:"command" docs:"Calls the max method using its declared arguments."] */
            this.cap = Math.max(/* param start [id:"BitmapFont_value_257" type:"number"] */4/* param end [id:"BitmapFont_value_257"] */, (int) Math.round(size * SIZE_TO_CAP));
            /* block end [id:"BitmapFont_block_84"] */
        }
    }

    public static Style style(int size, boolean bold) {
        /* block comment [block:"BitmapFont_block_85" text:"Returns a value or ends the current method."] */
        /* block start [id:"BitmapFont_block_85" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return new Style(Math.max(/* param start [id:"BitmapFont_value_258" type:"number"] */6/* param end [id:"BitmapFont_value_258"] */, size), bold);
        /* block end [id:"BitmapFont_block_85"] */
    }

    public static int capHeight(Style st) {
        /* block comment [block:"BitmapFont_block_86" text:"Returns a value or ends the current method."] */
        /* block start [id:"BitmapFont_block_86" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return st.cap;
        /* block end [id:"BitmapFont_block_86"] */
    }

    private static final class Glyph {
        /* block comment [block:"BitmapFont_block_87" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_87" category:"Variables" label:"set file" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        String file;
        /* block end [id:"BitmapFont_block_87"] */
        /* block comment [block:"BitmapFont_block_88" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_88" category:"Variables" label:"set img" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        BufferedImage img;
        /* block end [id:"BitmapFont_block_88"] */
        /* block comment [block:"BitmapFont_block_89" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_89" category:"Variables" label:"set w" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int w;
        /* block end [id:"BitmapFont_block_89"] */
        /* block comment [block:"BitmapFont_block_90" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_90" category:"Variables" label:"set h" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int h;
        /* block end [id:"BitmapFont_block_90"] */
        /* block comment [block:"BitmapFont_block_91" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_91" category:"Variables" label:"set units" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double units;
        /* block end [id:"BitmapFont_block_91"] */
    }

    private static String fileFor(char c) {
        /* block comment [block:"BitmapFont_block_92" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"BitmapFont_block_92" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        /* block comment [block:"BitmapFont_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BitmapFont_block_1" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (c >= /* param start [id:"BitmapFont_value_259" type:"character"] */'a'/* param end [id:"BitmapFont_value_259"] */ && c <= /* param start [id:"BitmapFont_value_260" type:"character"] */'z'/* param end [id:"BitmapFont_value_260"] */) return c + /* param start [id:"BitmapFont_value_261" type:"text"] */".png"/* param end [id:"BitmapFont_value_261"] */;
        /* block end [id:"BitmapFont_block_92"] */
        /* block end [id:"BitmapFont_block_1"] */
        /* block comment [block:"BitmapFont_block_93" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"BitmapFont_block_93" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        /* block comment [block:"BitmapFont_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BitmapFont_block_2" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (c >= /* param start [id:"BitmapFont_value_262" type:"character"] */'A'/* param end [id:"BitmapFont_value_262"] */ && c <= /* param start [id:"BitmapFont_value_263" type:"character"] */'Z'/* param end [id:"BitmapFont_value_263"] */) return /* param start [id:"BitmapFont_value_264" type:"text"] */"captial_"/* param end [id:"BitmapFont_value_264"] */ + c + /* param start [id:"BitmapFont_value_265" type:"text"] */".png"/* param end [id:"BitmapFont_value_265"] */;
        /* block end [id:"BitmapFont_block_93"] */
        /* block end [id:"BitmapFont_block_2"] */
        /* block comment [block:"BitmapFont_block_94" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"BitmapFont_block_94" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        /* block comment [block:"BitmapFont_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BitmapFont_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (c >= /* param start [id:"BitmapFont_value_266" type:"character"] */'0'/* param end [id:"BitmapFont_value_266"] */ && c <= /* param start [id:"BitmapFont_value_267" type:"character"] */'9'/* param end [id:"BitmapFont_value_267"] */) return c + /* param start [id:"BitmapFont_value_268" type:"text"] */".png"/* param end [id:"BitmapFont_value_268"] */;
        /* block end [id:"BitmapFont_block_94"] */
        /* block end [id:"BitmapFont_block_3"] */
        /* block comment [block:"BitmapFont_block_95" text:"Returns a value or ends the current method."] */
        /* block start [id:"BitmapFont_block_95" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return PUNCT.get(c);
        /* block end [id:"BitmapFont_block_95"] */
    }

    private static Glyph glyph(char c) {
        /* block comment [block:"BitmapFont_block_96" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_96" category:"Variables" label:"set gl" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Glyph gl = GLYPHS.get(c);
        /* block end [id:"BitmapFont_block_96"] */
        /* block comment [block:"BitmapFont_block_97" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"BitmapFont_block_97" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        /* block comment [block:"BitmapFont_block_4" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BitmapFont_block_4" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (gl != /* param start [id:"BitmapFont_value_269" type:"value"] */null/* param end [id:"BitmapFont_value_269"] */) return gl;
        /* block end [id:"BitmapFont_block_97"] */
        /* block end [id:"BitmapFont_block_4"] */
        /* block comment [block:"BitmapFont_block_98" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"BitmapFont_block_98" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        gl = new Glyph();
        /* block end [id:"BitmapFont_block_98"] */
        /* block comment [block:"BitmapFont_block_99" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_99" category:"Variables" label:"set file" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        String file = fileFor(c);
        /* block end [id:"BitmapFont_block_99"] */
        /* block comment [block:"BitmapFont_block_5" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BitmapFont_block_5" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (file != /* param start [id:"BitmapFont_value_270" type:"value"] */null/* param end [id:"BitmapFont_value_270"] */) {
            /* block comment [block:"BitmapFont_block_100" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"BitmapFont_block_100" category:"Variables" label:"set src" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            BufferedImage src = load(ASSET_DIR + /* param start [id:"BitmapFont_value_271" type:"text"] */"/"/* param end [id:"BitmapFont_value_271"] */ + file);
            /* block end [id:"BitmapFont_block_100"] */
            /* block comment [block:"BitmapFont_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"BitmapFont_block_6" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (src != /* param start [id:"BitmapFont_value_272" type:"value"] */null/* param end [id:"BitmapFont_value_272"] */) {
                /* block comment [block:"BitmapFont_block_101" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"BitmapFont_block_101" category:"Variables" label:"set minX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int minX = src.getWidth(), minY = src.getHeight(), maxX = -/* param start [id:"BitmapFont_value_273" type:"number"] */1/* param end [id:"BitmapFont_value_273"] */, maxY = -/* param start [id:"BitmapFont_value_274" type:"number"] */1/* param end [id:"BitmapFont_value_274"] */;
                /* block end [id:"BitmapFont_block_101"] */
                /* block comment [block:"BitmapFont_block_7" text:"Repeats the nested operations for each loop turn."] */
                /* block start [id:"BitmapFont_block_7" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
                for (int y = /* param start [id:"BitmapFont_value_275" type:"number"] */0/* param end [id:"BitmapFont_value_275"] */; y < src.getHeight(); y++) {
                    /* block comment [block:"BitmapFont_block_8" text:"Repeats the nested operations for each loop turn."] */
                    /* block start [id:"BitmapFont_block_8" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
                    for (int x = /* param start [id:"BitmapFont_value_276" type:"number"] */0/* param end [id:"BitmapFont_value_276"] */; x < src.getWidth(); x++) {
                        /* block comment [block:"BitmapFont_block_9" text:"Checks a Boolean condition and runs the body when it is true."] */
                        /* block start [id:"BitmapFont_block_9" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                        if ((src.getRGB(x, y) >>> /* param start [id:"BitmapFont_value_277" type:"number"] */24/* param end [id:"BitmapFont_value_277"] */) != /* param start [id:"BitmapFont_value_278" type:"number"] */0/* param end [id:"BitmapFont_value_278"] */) {
                            /* block comment [block:"BitmapFont_block_102" text:"Evaluates this expression and applies its result."] */
                            /* block start [id:"BitmapFont_block_102" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                            /* block comment [block:"BitmapFont_block_10" text:"Checks a Boolean condition and runs the body when it is true."] */
                            /* block start [id:"BitmapFont_block_10" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                            if (x < minX) minX = x;
                            /* block end [id:"BitmapFont_block_102"] */
                            /* block end [id:"BitmapFont_block_10"] */
                            /* block comment [block:"BitmapFont_block_103" text:"Evaluates this expression and applies its result."] */
                            /* block start [id:"BitmapFont_block_103" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                            /* block comment [block:"BitmapFont_block_11" text:"Checks a Boolean condition and runs the body when it is true."] */
                            /* block start [id:"BitmapFont_block_11" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                            if (x > maxX) maxX = x;
                            /* block end [id:"BitmapFont_block_103"] */
                            /* block end [id:"BitmapFont_block_11"] */
                            /* block comment [block:"BitmapFont_block_104" text:"Evaluates this expression and applies its result."] */
                            /* block start [id:"BitmapFont_block_104" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                            /* block comment [block:"BitmapFont_block_12" text:"Checks a Boolean condition and runs the body when it is true."] */
                            /* block start [id:"BitmapFont_block_12" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                            if (y < minY) minY = y;
                            /* block end [id:"BitmapFont_block_104"] */
                            /* block end [id:"BitmapFont_block_12"] */
                            /* block comment [block:"BitmapFont_block_105" text:"Evaluates this expression and applies its result."] */
                            /* block start [id:"BitmapFont_block_105" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                            /* block comment [block:"BitmapFont_block_13" text:"Checks a Boolean condition and runs the body when it is true."] */
                            /* block start [id:"BitmapFont_block_13" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                            if (y > maxY) maxY = y;
                            /* block end [id:"BitmapFont_block_105"] */
                            /* block end [id:"BitmapFont_block_13"] */
                        }
                        /* block end [id:"BitmapFont_block_9"] */
                    }
                    /* block end [id:"BitmapFont_block_8"] */
                }
                /* block end [id:"BitmapFont_block_7"] */
                /* block comment [block:"BitmapFont_block_14" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"BitmapFont_block_14" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (maxX >= /* param start [id:"BitmapFont_value_279" type:"number"] */0/* param end [id:"BitmapFont_value_279"] */) {
                    /* block comment [block:"BitmapFont_block_106" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"BitmapFont_block_106" category:"Variables" label:"set w" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    int w = maxX - minX + /* param start [id:"BitmapFont_value_280" type:"number"] */1/* param end [id:"BitmapFont_value_280"] */;
                    /* block end [id:"BitmapFont_block_106"] */
                    /* block comment [block:"BitmapFont_block_107" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"BitmapFont_block_107" category:"Variables" label:"set h" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    int h = maxY - minY + /* param start [id:"BitmapFont_value_281" type:"number"] */1/* param end [id:"BitmapFont_value_281"] */;
                    /* block end [id:"BitmapFont_block_107"] */
                    /* block comment [block:"BitmapFont_block_108" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"BitmapFont_block_108" category:"Variables" label:"set cropped" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    BufferedImage cropped = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                    /* block end [id:"BitmapFont_block_108"] */
                    /* block comment [block:"BitmapFont_block_109" text:"Runs the set rgb action. Values in this block are editable inputs."] */
                    /* block start [id:"BitmapFont_block_109" category:"Variables" label:"set rgb" shape:"command" docs:"Runs the set rgb action. Values in this block are editable inputs."] */
                    cropped.setRGB(/* param start [id:"BitmapFont_value_282" type:"number"] */0/* param end [id:"BitmapFont_value_282"] */, /* param start [id:"BitmapFont_value_283" type:"number"] */0/* param end [id:"BitmapFont_value_283"] */, w, h, src.getRGB(minX, minY, w, h, /* param start [id:"BitmapFont_value_284" type:"value"] */null/* param end [id:"BitmapFont_value_284"] */, /* param start [id:"BitmapFont_value_285" type:"number"] */0/* param end [id:"BitmapFont_value_285"] */, w), /* param start [id:"BitmapFont_value_286" type:"number"] */0/* param end [id:"BitmapFont_value_286"] */, w);
                    /* block end [id:"BitmapFont_block_109"] */
                    /* block comment [block:"BitmapFont_block_110" text:"Evaluates this expression and applies its result."] */
                    /* block start [id:"BitmapFont_block_110" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                    gl.file = file;
                    /* block end [id:"BitmapFont_block_110"] */
                    /* block comment [block:"BitmapFont_block_111" text:"Evaluates this expression and applies its result."] */
                    /* block start [id:"BitmapFont_block_111" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                    gl.img = cropped;
                    /* block end [id:"BitmapFont_block_111"] */
                    /* block comment [block:"BitmapFont_block_112" text:"Evaluates this expression and applies its result."] */
                    /* block start [id:"BitmapFont_block_112" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                    gl.w = w;
                    /* block end [id:"BitmapFont_block_112"] */
                    /* block comment [block:"BitmapFont_block_113" text:"Evaluates this expression and applies its result."] */
                    /* block start [id:"BitmapFont_block_113" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                    gl.h = h;
                    /* block end [id:"BitmapFont_block_113"] */
                    /* block comment [block:"BitmapFont_block_114" text:"Calls the expectedUnits method using its declared arguments."] */
                    /* block start [id:"BitmapFont_block_114" category:"My Blocks" label:"call expected units" shape:"command" docs:"Calls the expectedUnits method using its declared arguments."] */
                    gl.units = expectedUnits(c, h);
                    /* block end [id:"BitmapFont_block_114"] */
                }
                /* block end [id:"BitmapFont_block_14"] */
            }
            /* block end [id:"BitmapFont_block_6"] */
        }
        /* block end [id:"BitmapFont_block_5"] */
        /* block comment [block:"BitmapFont_block_115" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_115" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        GLYPHS.put(c, gl);
        /* block end [id:"BitmapFont_block_115"] */
        /* block comment [block:"BitmapFont_block_116" text:"Returns a value or ends the current method."] */
        /* block start [id:"BitmapFont_block_116" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return gl;
        /* block end [id:"BitmapFont_block_116"] */
    }

    /** Cap-height expectations for glyph files that shipped with empty padding. */
    private static double expectedUnits(char c, double measured) {
        /* block comment [block:"BitmapFont_block_117" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"BitmapFont_block_117" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        /* block comment [block:"BitmapFont_block_15" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BitmapFont_block_15" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if ((c == /* param start [id:"BitmapFont_value_287" type:"character"] */'I'/* param end [id:"BitmapFont_value_287"] */ || c == /* param start [id:"BitmapFont_value_288" type:"character"] */'l'/* param end [id:"BitmapFont_value_288"] */) && measured < /* param start [id:"BitmapFont_value_289" type:"number"] */140/* param end [id:"BitmapFont_value_289"] */) return CAP_UNITS;
        /* block end [id:"BitmapFont_block_117"] */
        /* block end [id:"BitmapFont_block_15"] */
        /* block comment [block:"BitmapFont_block_118" text:"Returns a value or ends the current method."] */
        /* block start [id:"BitmapFont_block_118" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return measured;
        /* block end [id:"BitmapFont_block_118"] */
    }

    private static BufferedImage load(String path) {
        /* block comment [block:"BitmapFont_block_16" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"BitmapFont_block_16" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"BitmapFont_block_119" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"BitmapFont_block_119" category:"Variables" label:"set f" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            File f = new File(path);
            /* block end [id:"BitmapFont_block_119"] */
            /* block comment [block:"BitmapFont_block_17" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"BitmapFont_block_17" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!f.exists()) {
                /* block comment [block:"BitmapFont_block_120" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"BitmapFont_block_120" category:"Variables" label:"set root" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                String root = System.getProperty(/* param start [id:"BitmapFont_value_290" type:"text"] */"jscratch.project.root"/* param end [id:"BitmapFont_value_290"] */);
                /* block end [id:"BitmapFont_block_120"] */
                /* block comment [block:"BitmapFont_block_18" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"BitmapFont_block_18" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (root != /* param start [id:"BitmapFont_value_291" type:"value"] */null/* param end [id:"BitmapFont_value_291"] */) {
                    /* block comment [block:"BitmapFont_block_121" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"BitmapFont_block_121" category:"Variables" label:"set r" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    File r = new File(root, path);
                    /* block end [id:"BitmapFont_block_121"] */
                    /* block comment [block:"BitmapFont_block_122" text:"Calls the exists method using its declared arguments."] */
                    /* block start [id:"BitmapFont_block_122" category:"My Blocks" label:"call exists" shape:"command" docs:"Calls the exists method using its declared arguments."] */
                    /* block comment [block:"BitmapFont_block_19" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"BitmapFont_block_19" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (r.exists()) f = r;
                    /* block end [id:"BitmapFont_block_122"] */
                    /* block end [id:"BitmapFont_block_19"] */
                }
                /* block end [id:"BitmapFont_block_18"] */
            }
            /* block end [id:"BitmapFont_block_17"] */
            /* block comment [block:"BitmapFont_block_123" text:"Calls the read method using its declared arguments."] */
            /* block start [id:"BitmapFont_block_123" category:"My Blocks" label:"call read" shape:"command" docs:"Calls the read method using its declared arguments."] */
            /* block comment [block:"BitmapFont_block_20" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"BitmapFont_block_20" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (f.exists()) return ImageIO.read(f);
            /* block end [id:"BitmapFont_block_123"] */
            /* block end [id:"BitmapFont_block_20"] */
            /* block comment [block:"BitmapFont_block_124" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"BitmapFont_block_124" category:"Variables" label:"set cl" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            /* block end [id:"BitmapFont_block_124"] */
            /* block comment [block:"BitmapFont_block_125" text:"Calls the getClassLoader method using its declared arguments."] */
            /* block start [id:"BitmapFont_block_125" category:"My Blocks" label:"call get class loader" shape:"command" docs:"Calls the getClassLoader method using its declared arguments."] */
            /* block comment [block:"BitmapFont_block_21" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"BitmapFont_block_21" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (cl == /* param start [id:"BitmapFont_value_292" type:"value"] */null/* param end [id:"BitmapFont_value_292"] */) cl = BitmapFont.class.getClassLoader();
            /* block end [id:"BitmapFont_block_125"] */
            /* block end [id:"BitmapFont_block_21"] */
            /* block comment [block:"BitmapFont_block_22" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"BitmapFont_block_22" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (cl != /* param start [id:"BitmapFont_value_293" type:"value"] */null/* param end [id:"BitmapFont_value_293"] */) {
                /* block comment [block:"BitmapFont_block_126" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"BitmapFont_block_126" category:"Variables" label:"set url" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                URL url = cl.getResource(path);
                /* block end [id:"BitmapFont_block_126"] */
                /* block comment [block:"BitmapFont_block_127" text:"Calls the getResource method using its declared arguments."] */
                /* block start [id:"BitmapFont_block_127" category:"My Blocks" label:"call get resource" shape:"command" docs:"Calls the getResource method using its declared arguments."] */
                /* block comment [block:"BitmapFont_block_23" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"BitmapFont_block_23" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (url == /* param start [id:"BitmapFont_value_294" type:"value"] */null/* param end [id:"BitmapFont_value_294"] */) url = cl.getResource(path.startsWith(/* param start [id:"BitmapFont_value_295" type:"text"] */"/"/* param end [id:"BitmapFont_value_295"] */) ? path.substring(/* param start [id:"BitmapFont_value_296" type:"number"] */1/* param end [id:"BitmapFont_value_296"] */) : /* param start [id:"BitmapFont_value_297" type:"text"] */"/"/* param end [id:"BitmapFont_value_297"] */ + path);
                /* block end [id:"BitmapFont_block_127"] */
                /* block end [id:"BitmapFont_block_23"] */
                /* block comment [block:"BitmapFont_block_128" text:"Calls the read method using its declared arguments."] */
                /* block start [id:"BitmapFont_block_128" category:"My Blocks" label:"call read" shape:"command" docs:"Calls the read method using its declared arguments."] */
                /* block comment [block:"BitmapFont_block_24" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"BitmapFont_block_24" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (url != /* param start [id:"BitmapFont_value_298" type:"value"] */null/* param end [id:"BitmapFont_value_298"] */) return ImageIO.read(url);
                /* block end [id:"BitmapFont_block_128"] */
                /* block end [id:"BitmapFont_block_24"] */
            }
            /* block end [id:"BitmapFont_block_22"] */
            /* block comment [block:"BitmapFont_block_129" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"BitmapFont_block_129" category:"Variables" label:"set err" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            System.err.println(/* param start [id:"BitmapFont_value_299" type:"text"] */"BitmapFont: missing glyph asset "/* param end [id:"BitmapFont_value_299"] */ + path);
            /* block end [id:"BitmapFont_block_129"] */
        }
        /* block end [id:"BitmapFont_block_16"] */ catch (Exception ignored) {
        }
        /* block comment [block:"BitmapFont_block_130" text:"Returns a value or ends the current method."] */
        /* block start [id:"BitmapFont_block_130" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"BitmapFont_value_300" type:"value"] */null/* param end [id:"BitmapFont_value_300"] */;
        /* block end [id:"BitmapFont_block_130"] */
    }

    private static BufferedImage tinted(Glyph gl, int rgb) {
        /* block comment [block:"BitmapFont_block_131" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_131" category:"Variables" label:"set key" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        String key = gl.file + /* param start [id:"BitmapFont_value_301" type:"text"] */"#"/* param end [id:"BitmapFont_value_301"] */ + rgb;
        /* block end [id:"BitmapFont_block_131"] */
        /* block comment [block:"BitmapFont_block_132" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_132" category:"Variables" label:"set t" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        BufferedImage t = TINTED.get(key);
        /* block end [id:"BitmapFont_block_132"] */
        /* block comment [block:"BitmapFont_block_133" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"BitmapFont_block_133" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        /* block comment [block:"BitmapFont_block_25" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BitmapFont_block_25" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (t != /* param start [id:"BitmapFont_value_302" type:"value"] */null/* param end [id:"BitmapFont_value_302"] */) return t;
        /* block end [id:"BitmapFont_block_133"] */
        /* block end [id:"BitmapFont_block_25"] */
        /* block comment [block:"BitmapFont_block_134" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"BitmapFont_block_134" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        t = new BufferedImage(gl.w, gl.h, BufferedImage.TYPE_INT_ARGB);
        /* block end [id:"BitmapFont_block_134"] */
        /* block comment [block:"BitmapFont_block_135" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_135" category:"Variables" label:"set px" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int[] px = gl.img.getRGB(/* param start [id:"BitmapFont_value_303" type:"number"] */0/* param end [id:"BitmapFont_value_303"] */, /* param start [id:"BitmapFont_value_304" type:"number"] */0/* param end [id:"BitmapFont_value_304"] */, gl.w, gl.h, /* param start [id:"BitmapFont_value_305" type:"value"] */null/* param end [id:"BitmapFont_value_305"] */, /* param start [id:"BitmapFont_value_306" type:"number"] */0/* param end [id:"BitmapFont_value_306"] */, gl.w);
        /* block end [id:"BitmapFont_block_135"] */
        /* block comment [block:"BitmapFont_block_26" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"BitmapFont_block_26" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int i = /* param start [id:"BitmapFont_value_307" type:"number"] */0/* param end [id:"BitmapFont_value_307"] */; i < px.length; i++) {
            /* block comment [block:"BitmapFont_block_136" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"BitmapFont_block_136" category:"Variables" label:"set a" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int a = px[i] >>> /* param start [id:"BitmapFont_value_308" type:"number"] */24/* param end [id:"BitmapFont_value_308"] */;
            /* block end [id:"BitmapFont_block_136"] */
            /* block comment [block:"BitmapFont_block_137" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"BitmapFont_block_137" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            /* block comment [block:"BitmapFont_block_27" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"BitmapFont_block_27" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (a != /* param start [id:"BitmapFont_value_309" type:"number"] */0/* param end [id:"BitmapFont_value_309"] */) px[i] = (a << /* param start [id:"BitmapFont_value_310" type:"number"] */24/* param end [id:"BitmapFont_value_310"] */) | rgb;
            /* block end [id:"BitmapFont_block_137"] */
            /* block end [id:"BitmapFont_block_27"] */
        }
        /* block end [id:"BitmapFont_block_26"] */
        /* block comment [block:"BitmapFont_block_138" text:"Runs the set rgb action. Values in this block are editable inputs."] */
        /* block start [id:"BitmapFont_block_138" category:"Variables" label:"set rgb" shape:"command" docs:"Runs the set rgb action. Values in this block are editable inputs."] */
        t.setRGB(/* param start [id:"BitmapFont_value_311" type:"number"] */0/* param end [id:"BitmapFont_value_311"] */, /* param start [id:"BitmapFont_value_312" type:"number"] */0/* param end [id:"BitmapFont_value_312"] */, gl.w, gl.h, px, /* param start [id:"BitmapFont_value_313" type:"number"] */0/* param end [id:"BitmapFont_value_313"] */, gl.w);
        /* block end [id:"BitmapFont_block_138"] */
        /* block comment [block:"BitmapFont_block_139" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_139" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        TINTED.put(key, t);
        /* block end [id:"BitmapFont_block_139"] */
        /* block comment [block:"BitmapFont_block_140" text:"Returns a value or ends the current method."] */
        /* block start [id:"BitmapFont_block_140" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return t;
        /* block end [id:"BitmapFont_block_140"] */
    }

    private static BufferedImage scaled(Glyph gl, int rgb, int dw, int dh) {
        /* block comment [block:"BitmapFont_block_141" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_141" category:"Variables" label:"set key" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        String key = gl.file + /* param start [id:"BitmapFont_value_314" type:"text"] */"#"/* param end [id:"BitmapFont_value_314"] */ + rgb + /* param start [id:"BitmapFont_value_315" type:"text"] */"@"/* param end [id:"BitmapFont_value_315"] */ + dw + /* param start [id:"BitmapFont_value_316" type:"text"] */"x"/* param end [id:"BitmapFont_value_316"] */ + dh;
        /* block end [id:"BitmapFont_block_141"] */
        /* block comment [block:"BitmapFont_block_142" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_142" category:"Variables" label:"set out" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        BufferedImage out = SCALED.get(key);
        /* block end [id:"BitmapFont_block_142"] */
        /* block comment [block:"BitmapFont_block_143" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"BitmapFont_block_143" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        /* block comment [block:"BitmapFont_block_28" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BitmapFont_block_28" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (out != /* param start [id:"BitmapFont_value_317" type:"value"] */null/* param end [id:"BitmapFont_value_317"] */) return out;
        /* block end [id:"BitmapFont_block_143"] */
        /* block end [id:"BitmapFont_block_28"] */
        /* block comment [block:"BitmapFont_block_144" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_144" category:"Variables" label:"set nativeTint" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        BufferedImage nativeTint = tinted(gl, rgb);
        /* block end [id:"BitmapFont_block_144"] */
        /* block comment [block:"BitmapFont_block_145" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"BitmapFont_block_145" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        /* block comment [block:"BitmapFont_block_29" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BitmapFont_block_29" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (nativeTint == /* param start [id:"BitmapFont_value_318" type:"value"] */null/* param end [id:"BitmapFont_value_318"] */) return /* param start [id:"BitmapFont_value_319" type:"value"] */null/* param end [id:"BitmapFont_value_319"] */;
        /* block end [id:"BitmapFont_block_145"] */
        /* block end [id:"BitmapFont_block_29"] */
        /* block comment [block:"BitmapFont_block_30" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BitmapFont_block_30" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (dw == gl.w && dh == gl.h) {
            /* block comment [block:"BitmapFont_block_146" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"BitmapFont_block_146" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            out = nativeTint;
            /* block end [id:"BitmapFont_block_146"] */
        } else {
            /* block comment [block:"BitmapFont_block_147" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"BitmapFont_block_147" category:"Variables" label:"set small" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Image small = nativeTint.getScaledInstance(dw, dh, Image.SCALE_AREA_AVERAGING);
            /* block end [id:"BitmapFont_block_147"] */
            /* block comment [block:"BitmapFont_block_148" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"BitmapFont_block_148" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            out = new BufferedImage(dw, dh, BufferedImage.TYPE_INT_ARGB);
            /* block end [id:"BitmapFont_block_148"] */
            /* block comment [block:"BitmapFont_block_149" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"BitmapFont_block_149" category:"Variables" label:"set gg" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Graphics2D gg = out.createGraphics();
            /* block end [id:"BitmapFont_block_149"] */
            /* block comment [block:"BitmapFont_block_150" text:"Calls the drawImage method using its declared arguments."] */
            /* block start [id:"BitmapFont_block_150" category:"My Blocks" label:"call draw image" shape:"command" docs:"Calls the drawImage method using its declared arguments."] */
            gg.drawImage(small, /* param start [id:"BitmapFont_value_320" type:"number"] */0/* param end [id:"BitmapFont_value_320"] */, /* param start [id:"BitmapFont_value_321" type:"number"] */0/* param end [id:"BitmapFont_value_321"] */, /* param start [id:"BitmapFont_value_322" type:"value"] */null/* param end [id:"BitmapFont_value_322"] */);
            /* block end [id:"BitmapFont_block_150"] */
            /* block comment [block:"BitmapFont_block_151" text:"Calls the dispose method using its declared arguments."] */
            /* block start [id:"BitmapFont_block_151" category:"My Blocks" label:"call dispose" shape:"command" docs:"Calls the dispose method using its declared arguments."] */
            gg.dispose();
            /* block end [id:"BitmapFont_block_151"] */
        }
        /* block end [id:"BitmapFont_block_30"] */
        /* block comment [block:"BitmapFont_block_152" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_152" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        SCALED.put(key, out);
        /* block end [id:"BitmapFont_block_152"] */
        /* block comment [block:"BitmapFont_block_153" text:"Returns a value or ends the current method."] */
        /* block start [id:"BitmapFont_block_153" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return out;
        /* block end [id:"BitmapFont_block_153"] */
    }

    private static int tracking(int cap) {
        /* block comment [block:"BitmapFont_block_154" text:"Returns a value or ends the current method."] */
        /* block start [id:"BitmapFont_block_154" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return Math.max(/* param start [id:"BitmapFont_value_323" type:"number"] */1/* param end [id:"BitmapFont_value_323"] */, (int) Math.round(cap * /* param start [id:"BitmapFont_value_324" type:"number"] */0.12/* param end [id:"BitmapFont_value_324"] */));
        /* block end [id:"BitmapFont_block_154"] */
    }

    private static int spaceWidth(int cap) {
        /* block comment [block:"BitmapFont_block_155" text:"Returns a value or ends the current method."] */
        /* block start [id:"BitmapFont_block_155" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return Math.max(/* param start [id:"BitmapFont_value_325" type:"number"] */3/* param end [id:"BitmapFont_value_325"] */, (int) Math.round(cap * /* param start [id:"BitmapFont_value_326" type:"number"] */0.5/* param end [id:"BitmapFont_value_326"] */));
        /* block end [id:"BitmapFont_block_155"] */
    }

    private static int boldShift(Style st) {
        /* block comment [block:"BitmapFont_block_156" text:"Returns a value or ends the current method."] */
        /* block start [id:"BitmapFont_block_156" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return st.bold ? Math.max(/* param start [id:"BitmapFont_value_327" type:"number"] */1/* param end [id:"BitmapFont_value_327"] */, (int) Math.round(st.cap / /* param start [id:"BitmapFont_value_328" type:"number"] */9.0/* param end [id:"BitmapFont_value_328"] */)) : /* param start [id:"BitmapFont_value_329" type:"number"] */0/* param end [id:"BitmapFont_value_329"] */;
        /* block end [id:"BitmapFont_block_156"] */
    }

    /** Vertical placement: returns the top pixel of the glyph for a baseline. */
    private static int topFor(char c, int baseline, int ink, int cap) {
        /* block comment [block:"BitmapFont_block_157" text:"Calls the max method using its declared arguments."] */
        /* block start [id:"BitmapFont_block_157" category:"My Blocks" label:"call max" shape:"command" docs:"Calls the max method using its declared arguments."] */
        /* block comment [block:"BitmapFont_block_31" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BitmapFont_block_31" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (c == /* param start [id:"BitmapFont_value_330" type:"character"] */'_'/* param end [id:"BitmapFont_value_330"] */) return baseline + Math.max(/* param start [id:"BitmapFont_value_331" type:"number"] */1/* param end [id:"BitmapFont_value_331"] */, (int) Math.round(cap * /* param start [id:"BitmapFont_value_332" type:"number"] */0.08/* param end [id:"BitmapFont_value_332"] */));
        /* block end [id:"BitmapFont_block_157"] */
        /* block end [id:"BitmapFont_block_31"] */
        /* block comment [block:"BitmapFont_block_158" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"BitmapFont_block_158" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        /* block comment [block:"BitmapFont_block_32" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BitmapFont_block_32" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (c == /* param start [id:"BitmapFont_value_333" type:"character"] */'\''/* param end [id:"BitmapFont_value_333"] */ || c == /* param start [id:"BitmapFont_value_334" type:"character"] */'"'/* param end [id:"BitmapFont_value_334"] */ || c == /* param start [id:"BitmapFont_value_335" type:"character"] */'`'/* param end [id:"BitmapFont_value_335"] */) return baseline - cap;
        /* block end [id:"BitmapFont_block_158"] */
        /* block end [id:"BitmapFont_block_32"] */
        /* block comment [block:"BitmapFont_block_33" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BitmapFont_block_33" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (c == /* param start [id:"BitmapFont_value_336" type:"character"] */'-'/* param end [id:"BitmapFont_value_336"] */ || c == /* param start [id:"BitmapFont_value_337" type:"character"] */'+'/* param end [id:"BitmapFont_value_337"] */ || c == /* param start [id:"BitmapFont_value_338" type:"character"] */'='/* param end [id:"BitmapFont_value_338"] */ || c == /* param start [id:"BitmapFont_value_339" type:"character"] */'*'/* param end [id:"BitmapFont_value_339"] */ || c == /* param start [id:"BitmapFont_value_340" type:"character"] */'~'/* param end [id:"BitmapFont_value_340"] */ || c == /* param start [id:"BitmapFont_value_341" type:"character"] */'^'/* param end [id:"BitmapFont_value_341"] */) {
            /* block comment [block:"BitmapFont_block_159" text:"Returns a value or ends the current method."] */
            /* block start [id:"BitmapFont_block_159" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return baseline - (int) Math.round(cap - (cap - ink) / /* param start [id:"BitmapFont_value_342" type:"number"] */2.0/* param end [id:"BitmapFont_value_342"] */);
            /* block end [id:"BitmapFont_block_159"] */
        }
        /* block end [id:"BitmapFont_block_33"] */
        /* block comment [block:"BitmapFont_block_34" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BitmapFont_block_34" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (c == /* param start [id:"BitmapFont_value_343" type:"character"] */'g'/* param end [id:"BitmapFont_value_343"] */ || c == /* param start [id:"BitmapFont_value_344" type:"character"] */'p'/* param end [id:"BitmapFont_value_344"] */ || c == /* param start [id:"BitmapFont_value_345" type:"character"] */'q'/* param end [id:"BitmapFont_value_345"] */ || c == /* param start [id:"BitmapFont_value_346" type:"character"] */'y'/* param end [id:"BitmapFont_value_346"] */ || c == /* param start [id:"BitmapFont_value_347" type:"character"] */'j'/* param end [id:"BitmapFont_value_347"] */ || c == /* param start [id:"BitmapFont_value_348" type:"character"] */'Q'/* param end [id:"BitmapFont_value_348"] */ || c == /* param start [id:"BitmapFont_value_349" type:"character"] */'@'/* param end [id:"BitmapFont_value_349"] */
        || c == /* param start [id:"BitmapFont_value_350" type:"character"] */','/* param end [id:"BitmapFont_value_350"] */ || c == /* param start [id:"BitmapFont_value_351" type:"character"] */';'/* param end [id:"BitmapFont_value_351"] */) {
            /* block comment [block:"BitmapFont_block_160" text:"Returns a value or ends the current method."] */
            /* block start [id:"BitmapFont_block_160" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return baseline - (int) Math.round(ink - DESCENT_UNITS * (cap / CAP_UNITS));
            /* block end [id:"BitmapFont_block_160"] */
        }
        /* block end [id:"BitmapFont_block_34"] */
        /* block comment [block:"BitmapFont_block_161" text:"Returns a value or ends the current method."] */
        /* block start [id:"BitmapFont_block_161" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return baseline - ink;
        /* block end [id:"BitmapFont_block_161"] */
    }

    /** Pixels consumed by one character, including tracking. */
    private static int advanceOf(char c, Style st) {
        /* block comment [block:"BitmapFont_block_162" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_162" category:"Variables" label:"set cap" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int cap = st.cap;
        /* block end [id:"BitmapFont_block_162"] */
        /* block comment [block:"BitmapFont_block_163" text:"Calls the tracking method using its declared arguments."] */
        /* block start [id:"BitmapFont_block_163" category:"My Blocks" label:"call tracking" shape:"command" docs:"Calls the tracking method using its declared arguments."] */
        /* block comment [block:"BitmapFont_block_35" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BitmapFont_block_35" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (c == /* param start [id:"BitmapFont_value_352" type:"character"] */' '/* param end [id:"BitmapFont_value_352"] */) return spaceWidth(cap) + tracking(cap);
        /* block end [id:"BitmapFont_block_163"] */
        /* block end [id:"BitmapFont_block_35"] */
        /* block comment [block:"BitmapFont_block_164" text:"Runs the return action. Values in this block are editable inputs."] */
        /* block start [id:"BitmapFont_block_164" category:"Motion" label:"return" shape:"command" docs:"Runs the return action. Values in this block are editable inputs."] */
        /* block comment [block:"BitmapFont_block_36" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BitmapFont_block_36" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (c == /* param start [id:"BitmapFont_value_353" type:"character"] */'\t'/* param end [id:"BitmapFont_value_353"] */) return (spaceWidth(cap) + tracking(cap)) * /* param start [id:"BitmapFont_value_354" type:"number"] */2/* param end [id:"BitmapFont_value_354"] */;
        /* block end [id:"BitmapFont_block_164"] */
        /* block end [id:"BitmapFont_block_36"] */
        /* block comment [block:"BitmapFont_block_165" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"BitmapFont_block_165" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        /* block comment [block:"BitmapFont_block_37" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BitmapFont_block_37" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (c == /* param start [id:"BitmapFont_value_355" type:"character"] */'\n'/* param end [id:"BitmapFont_value_355"] */ || c == /* param start [id:"BitmapFont_value_356" type:"character"] */'\r'/* param end [id:"BitmapFont_value_356"] */) return /* param start [id:"BitmapFont_value_357" type:"number"] */0/* param end [id:"BitmapFont_value_357"] */;
        /* block end [id:"BitmapFont_block_165"] */
        /* block end [id:"BitmapFont_block_37"] */
        /* block comment [block:"BitmapFont_block_166" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_166" category:"Variables" label:"set gl" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Glyph gl = glyph(c);
        /* block end [id:"BitmapFont_block_166"] */
        /* block comment [block:"BitmapFont_block_167" text:"Calls the tracking method using its declared arguments."] */
        /* block start [id:"BitmapFont_block_167" category:"My Blocks" label:"call tracking" shape:"command" docs:"Calls the tracking method using its declared arguments."] */
        /* block comment [block:"BitmapFont_block_38" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BitmapFont_block_38" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (gl == /* param start [id:"BitmapFont_value_358" type:"value"] */null/* param end [id:"BitmapFont_value_358"] */ || gl.img == /* param start [id:"BitmapFont_value_359" type:"value"] */null/* param end [id:"BitmapFont_value_359"] */) return spaceWidth(cap) + tracking(cap);
        /* block end [id:"BitmapFont_block_167"] */
        /* block end [id:"BitmapFont_block_38"] */
        /* block comment [block:"BitmapFont_block_168" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_168" category:"Variables" label:"set unitScale" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double unitScale = cap / CAP_UNITS;
        /* block end [id:"BitmapFont_block_168"] */
        /* block comment [block:"BitmapFont_block_169" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_169" category:"Variables" label:"set scale" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double scale = unitScale * (gl.units / gl.h);
        /* block end [id:"BitmapFont_block_169"] */
        /* block comment [block:"BitmapFont_block_170" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_170" category:"Variables" label:"set dw" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int dw = Math.max(/* param start [id:"BitmapFont_value_360" type:"number"] */1/* param end [id:"BitmapFont_value_360"] */, (int) Math.round(gl.w * scale));
        /* block end [id:"BitmapFont_block_170"] */
        /* block comment [block:"BitmapFont_block_171" text:"Returns a value or ends the current method."] */
        /* block start [id:"BitmapFont_block_171" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return dw + boldShift(st) + tracking(cap);
        /* block end [id:"BitmapFont_block_171"] */
    }

    public static int width(String s, Style st) {
        /* block comment [block:"BitmapFont_block_172" text:"Calls the length method using its declared arguments."] */
        /* block start [id:"BitmapFont_block_172" category:"My Blocks" label:"call length" shape:"command" docs:"Calls the length method using its declared arguments."] */
        /* block comment [block:"BitmapFont_block_39" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BitmapFont_block_39" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (s == /* param start [id:"BitmapFont_value_361" type:"value"] */null/* param end [id:"BitmapFont_value_361"] */ || s.length() == /* param start [id:"BitmapFont_value_362" type:"number"] */0/* param end [id:"BitmapFont_value_362"] */) return /* param start [id:"BitmapFont_value_363" type:"number"] */0/* param end [id:"BitmapFont_value_363"] */;
        /* block end [id:"BitmapFont_block_172"] */
        /* block end [id:"BitmapFont_block_39"] */
        /* block comment [block:"BitmapFont_block_173" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_173" category:"Variables" label:"set total" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int total = /* param start [id:"BitmapFont_value_364" type:"number"] */0/* param end [id:"BitmapFont_value_364"] */;
        /* block end [id:"BitmapFont_block_173"] */
        /* block comment [block:"BitmapFont_block_40" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"BitmapFont_block_40" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int i = /* param start [id:"BitmapFont_value_365" type:"number"] */0/* param end [id:"BitmapFont_value_365"] */; i < s.length(); i++) total += advanceOf(s.charAt(i), st);
        /* block end [id:"BitmapFont_block_40"] */
        /* block comment [block:"BitmapFont_block_174" text:"Returns a value or ends the current method."] */
        /* block start [id:"BitmapFont_block_174" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return Math.max(/* param start [id:"BitmapFont_value_366" type:"number"] */0/* param end [id:"BitmapFont_value_366"] */, total - tracking(st.cap));
        /* block end [id:"BitmapFont_block_174"] */
    }

    public static void draw(Graphics2D g, String s, int x, int baseline, Style st) {
        /* block comment [block:"BitmapFont_block_175" text:"Calls the length method using its declared arguments."] */
        /* block start [id:"BitmapFont_block_175" category:"My Blocks" label:"call length" shape:"command" docs:"Calls the length method using its declared arguments."] */
        /* block comment [block:"BitmapFont_block_41" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BitmapFont_block_41" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (g == /* param start [id:"BitmapFont_value_367" type:"value"] */null/* param end [id:"BitmapFont_value_367"] */ || s == /* param start [id:"BitmapFont_value_368" type:"value"] */null/* param end [id:"BitmapFont_value_368"] */ || s.length() == /* param start [id:"BitmapFont_value_369" type:"number"] */0/* param end [id:"BitmapFont_value_369"] */) return;
        /* block end [id:"BitmapFont_block_175"] */
        /* block end [id:"BitmapFont_block_41"] */
        /* block comment [block:"BitmapFont_block_176" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_176" category:"Variables" label:"set cap" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int cap = st.cap;
        /* block end [id:"BitmapFont_block_176"] */
        /* block comment [block:"BitmapFont_block_177" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_177" category:"Variables" label:"set unitScale" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double unitScale = cap / CAP_UNITS;
        /* block end [id:"BitmapFont_block_177"] */
        /* block comment [block:"BitmapFont_block_178" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_178" category:"Variables" label:"set rgb" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int rgb = g.getColor().getRGB() & /* param start [id:"BitmapFont_value_370" type:"number"] */0xFFFFFF/* param end [id:"BitmapFont_value_370"] */;
        /* block end [id:"BitmapFont_block_178"] */
        /* block comment [block:"BitmapFont_block_179" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_179" category:"Variables" label:"set bold" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int bold = boldShift(st);
        /* block end [id:"BitmapFont_block_179"] */
        /* block comment [block:"BitmapFont_block_180" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BitmapFont_block_180" category:"Variables" label:"set cx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int cx = x;
        /* block end [id:"BitmapFont_block_180"] */
        /* block comment [block:"BitmapFont_block_42" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"BitmapFont_block_42" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int i = /* param start [id:"BitmapFont_value_371" type:"number"] */0/* param end [id:"BitmapFont_value_371"] */; i < s.length(); i++) {
            /* block comment [block:"BitmapFont_block_181" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"BitmapFont_block_181" category:"Variables" label:"set c" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            char c = s.charAt(i);
            /* block end [id:"BitmapFont_block_181"] */
            /* block comment [block:"BitmapFont_block_182" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"BitmapFont_block_182" category:"Variables" label:"set adv" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int adv = advanceOf(c, st);
            /* block end [id:"BitmapFont_block_182"] */
            /* block comment [block:"BitmapFont_block_43" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"BitmapFont_block_43" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (c != /* param start [id:"BitmapFont_value_372" type:"character"] */' '/* param end [id:"BitmapFont_value_372"] */ && c != /* param start [id:"BitmapFont_value_373" type:"character"] */'\t'/* param end [id:"BitmapFont_value_373"] */ && c != /* param start [id:"BitmapFont_value_374" type:"character"] */'\n'/* param end [id:"BitmapFont_value_374"] */ && c != /* param start [id:"BitmapFont_value_375" type:"character"] */'\r'/* param end [id:"BitmapFont_value_375"] */ && adv > /* param start [id:"BitmapFont_value_376" type:"number"] */0/* param end [id:"BitmapFont_value_376"] */) {
                /* block comment [block:"BitmapFont_block_183" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"BitmapFont_block_183" category:"Variables" label:"set gl" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                Glyph gl = glyph(c);
                /* block end [id:"BitmapFont_block_183"] */
                /* block comment [block:"BitmapFont_block_44" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"BitmapFont_block_44" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (gl != /* param start [id:"BitmapFont_value_377" type:"value"] */null/* param end [id:"BitmapFont_value_377"] */ && gl.img != /* param start [id:"BitmapFont_value_378" type:"value"] */null/* param end [id:"BitmapFont_value_378"] */) {
                    /* block comment [block:"BitmapFont_block_184" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"BitmapFont_block_184" category:"Variables" label:"set scale" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    double scale = unitScale * (gl.units / gl.h);
                    /* block end [id:"BitmapFont_block_184"] */
                    /* block comment [block:"BitmapFont_block_185" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"BitmapFont_block_185" category:"Variables" label:"set dw" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    int dw = Math.max(/* param start [id:"BitmapFont_value_379" type:"number"] */1/* param end [id:"BitmapFont_value_379"] */, (int) Math.round(gl.w * scale));
                    /* block end [id:"BitmapFont_block_185"] */
                    /* block comment [block:"BitmapFont_block_186" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"BitmapFont_block_186" category:"Variables" label:"set dh" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    int dh = Math.max(/* param start [id:"BitmapFont_value_380" type:"number"] */1/* param end [id:"BitmapFont_value_380"] */, (int) Math.round(gl.units * unitScale));
                    /* block end [id:"BitmapFont_block_186"] */
                    /* block comment [block:"BitmapFont_block_187" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"BitmapFont_block_187" category:"Variables" label:"set img" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    BufferedImage img = scaled(gl, rgb, dw, dh);
                    /* block end [id:"BitmapFont_block_187"] */
                    /* block comment [block:"BitmapFont_block_45" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"BitmapFont_block_45" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (img != /* param start [id:"BitmapFont_value_381" type:"value"] */null/* param end [id:"BitmapFont_value_381"] */) {
                        /* block comment [block:"BitmapFont_block_188" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                        /* block start [id:"BitmapFont_block_188" category:"Variables" label:"set top" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                        int top = topFor(c, baseline, dh, cap);
                        /* block end [id:"BitmapFont_block_188"] */
                        /* block comment [block:"BitmapFont_block_189" text:"Calls the drawImage method using its declared arguments."] */
                        /* block start [id:"BitmapFont_block_189" category:"My Blocks" label:"call draw image" shape:"command" docs:"Calls the drawImage method using its declared arguments."] */
                        g.drawImage(img, cx, top, /* param start [id:"BitmapFont_value_382" type:"value"] */null/* param end [id:"BitmapFont_value_382"] */);
                        /* block end [id:"BitmapFont_block_189"] */
                        /* block comment [block:"BitmapFont_block_190" text:"Calls the drawImage method using its declared arguments."] */
                        /* block start [id:"BitmapFont_block_190" category:"My Blocks" label:"call draw image" shape:"command" docs:"Calls the drawImage method using its declared arguments."] */
                        /* block comment [block:"BitmapFont_block_46" text:"Checks a Boolean condition and runs the body when it is true."] */
                        /* block start [id:"BitmapFont_block_46" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                        if (st.bold) g.drawImage(img, cx + bold, top, /* param start [id:"BitmapFont_value_383" type:"value"] */null/* param end [id:"BitmapFont_value_383"] */);
                        /* block end [id:"BitmapFont_block_190"] */
                        /* block end [id:"BitmapFont_block_46"] */
                    }
                    /* block end [id:"BitmapFont_block_45"] */
                }
                /* block end [id:"BitmapFont_block_44"] */
            }
            /* block end [id:"BitmapFont_block_43"] */
            /* block comment [block:"BitmapFont_block_191" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"BitmapFont_block_191" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            cx += adv;
            /* block end [id:"BitmapFont_block_191"] */
        }
        /* block end [id:"BitmapFont_block_42"] */
    }

    public static void drawCentered(Graphics2D g, String s, int cx, int baseline, Style st) {
        /* block comment [block:"BitmapFont_block_192" text:"Calls the draw method using its declared arguments."] */
        /* block start [id:"BitmapFont_block_192" category:"My Blocks" label:"call draw" shape:"command" docs:"Calls the draw method using its declared arguments."] */
        draw(g, s, cx - width(s, st) / /* param start [id:"BitmapFont_value_384" type:"number"] */2/* param end [id:"BitmapFont_value_384"] */, baseline, st);
        /* block end [id:"BitmapFont_block_192"] */
    }
}
