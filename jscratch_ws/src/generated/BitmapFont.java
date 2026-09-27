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
        PUNCT.put('&', "andpersayand.png");
        PUNCT.put('*', "asterisk.png");
        PUNCT.put('@', "at.png");
        PUNCT.put('`', "backtick.png");
        PUNCT.put('\\', "back_slash.png");
        PUNCT.put('^', "caret.png");
        PUNCT.put('>', "close_angle_bracket.png");
        PUNCT.put(')', "close_bracket.png");
        PUNCT.put('}', "close_curly_bracket.png");
        PUNCT.put(']', "close_square_bracket.png");
        PUNCT.put(':', "colon.png");
        PUNCT.put(',', "comma.png");
        PUNCT.put('-', "dash.png");
        PUNCT.put('$', "dollar_sign.png");
        PUNCT.put('"', "double_quote.png");
        PUNCT.put('=', "equals.png");
        PUNCT.put('!', "exclamation_mark.png");
        PUNCT.put('/', "forward_slash.png");
        PUNCT.put('#', "hashtag.png");
        PUNCT.put('<', "open_angle_bracket.png");
        PUNCT.put('(', "open_bracket.png");
        PUNCT.put('{', "open_curly_bracket.png");
        PUNCT.put('[', "open_square_bracket.png");
        PUNCT.put('%', "percent.png");
        PUNCT.put('.', "period.png");
        PUNCT.put('|', "pipe.png");
        PUNCT.put('+', "plus.png");
        PUNCT.put('?', "question_mark.png");
        PUNCT.put('\'', "quote.png");
        PUNCT.put(';', "semicolon.png");
        PUNCT.put('~', "tilde.png");
        PUNCT.put('_', "underscore.png");
    }

    private BitmapFont() {
    }

    /** Requested text size plus bold flag; cap height is derived from size. */
    public static final class Style {
        public final int size;
        public final boolean bold;
        public final int cap;

        Style(int size, boolean bold) {
            this.size = size;
            this.bold = bold;
            this.cap = Math.max(4, (int) Math.round(size * SIZE_TO_CAP));
        }
    }

    public static Style style(int size, boolean bold) {
        return new Style(Math.max(6, size), bold);
    }

    public static int capHeight(Style st) {
        return st.cap;
    }

    private static final class Glyph {
        String file;
        BufferedImage img;
        int w;
        int h;
        double units;
    }

    private static String fileFor(char c) {
        if (c >= 'a' && c <= 'z') return c + ".png";
        if (c >= 'A' && c <= 'Z') return "captial_" + c + ".png";
        if (c >= '0' && c <= '9') return c + ".png";
        return PUNCT.get(c);
    }

    private static Glyph glyph(char c) {
        Glyph gl = GLYPHS.get(c);
        if (gl != null) return gl;
        gl = new Glyph();
        String file = fileFor(c);
        if (file != null) {
            BufferedImage src = load(ASSET_DIR + "/" + file);
            if (src != null) {
                int minX = src.getWidth(), minY = src.getHeight(), maxX = -1, maxY = -1;
                for (int y = 0; y < src.getHeight(); y++) {
                    for (int x = 0; x < src.getWidth(); x++) {
                        if ((src.getRGB(x, y) >>> 24) != 0) {
                            if (x < minX) minX = x;
                            if (x > maxX) maxX = x;
                            if (y < minY) minY = y;
                            if (y > maxY) maxY = y;
                        }
                    }
                }
                if (maxX >= 0) {
                    int w = maxX - minX + 1;
                    int h = maxY - minY + 1;
                    BufferedImage cropped = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                    cropped.setRGB(0, 0, w, h, src.getRGB(minX, minY, w, h, null, 0, w), 0, w);
                    gl.file = file;
                    gl.img = cropped;
                    gl.w = w;
                    gl.h = h;
                    gl.units = expectedUnits(c, h);
                }
            }
        }
        GLYPHS.put(c, gl);
        return gl;
    }

    /** Cap-height expectations for glyph files that shipped with empty padding. */
    private static double expectedUnits(char c, double measured) {
        if ((c == 'I' || c == 'l') && measured < 140) return CAP_UNITS;
        return measured;
    }

    private static BufferedImage load(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                String root = System.getProperty("jscratch.project.root");
                if (root != null) {
                    File r = new File(root, path);
                    if (r.exists()) f = r;
                }
            }
            if (f.exists()) return ImageIO.read(f);
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) cl = BitmapFont.class.getClassLoader();
            if (cl != null) {
                URL url = cl.getResource(path);
                if (url == null) url = cl.getResource(path.startsWith("/") ? path.substring(1) : "/" + path);
                if (url != null) return ImageIO.read(url);
            }
            System.err.println("BitmapFont: missing glyph asset " + path);
        } catch (Exception ignored) {
        }
        return null;
    }

    private static BufferedImage tinted(Glyph gl, int rgb) {
        String key = gl.file + "#" + rgb;
        BufferedImage t = TINTED.get(key);
        if (t != null) return t;
        t = new BufferedImage(gl.w, gl.h, BufferedImage.TYPE_INT_ARGB);
        int[] px = gl.img.getRGB(0, 0, gl.w, gl.h, null, 0, gl.w);
        for (int i = 0; i < px.length; i++) {
            int a = px[i] >>> 24;
            if (a != 0) px[i] = (a << 24) | rgb;
        }
        t.setRGB(0, 0, gl.w, gl.h, px, 0, gl.w);
        TINTED.put(key, t);
        return t;
    }

    private static BufferedImage scaled(Glyph gl, int rgb, int dw, int dh) {
        String key = gl.file + "#" + rgb + "@" + dw + "x" + dh;
        BufferedImage out = SCALED.get(key);
        if (out != null) return out;
        BufferedImage nativeTint = tinted(gl, rgb);
        if (nativeTint == null) return null;
        if (dw == gl.w && dh == gl.h) {
            out = nativeTint;
        } else {
            Image small = nativeTint.getScaledInstance(dw, dh, Image.SCALE_AREA_AVERAGING);
            out = new BufferedImage(dw, dh, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gg = out.createGraphics();
            gg.drawImage(small, 0, 0, null);
            gg.dispose();
        }
        SCALED.put(key, out);
        return out;
    }

    private static int tracking(int cap) {
        return Math.max(1, (int) Math.round(cap * 0.12));
    }

    private static int spaceWidth(int cap) {
        return Math.max(3, (int) Math.round(cap * 0.5));
    }

    private static int boldShift(Style st) {
        return st.bold ? Math.max(1, (int) Math.round(st.cap / 9.0)) : 0;
    }

    /** Vertical placement: returns the top pixel of the glyph for a baseline. */
    private static int topFor(char c, int baseline, int ink, int cap) {
        if (c == '_') return baseline + Math.max(1, (int) Math.round(cap * 0.08));
        if (c == '\'' || c == '"' || c == '`') return baseline - cap;
        if (c == '-' || c == '+' || c == '=' || c == '*' || c == '~' || c == '^') {
            return baseline - (int) Math.round(cap - (cap - ink) / 2.0);
        }
        if (c == 'g' || c == 'p' || c == 'q' || c == 'y' || c == 'j' || c == 'Q' || c == '@'
                || c == ',' || c == ';') {
            return baseline - (int) Math.round(ink - DESCENT_UNITS * (cap / CAP_UNITS));
        }
        return baseline - ink;
    }

    /** Pixels consumed by one character, including tracking. */
    private static int advanceOf(char c, Style st) {
        int cap = st.cap;
        if (c == ' ') return spaceWidth(cap) + tracking(cap);
        if (c == '\t') return (spaceWidth(cap) + tracking(cap)) * 2;
        if (c == '\n' || c == '\r') return 0;
        Glyph gl = glyph(c);
        if (gl == null || gl.img == null) return spaceWidth(cap) + tracking(cap);
        double unitScale = cap / CAP_UNITS;
        double scale = unitScale * (gl.units / gl.h);
        int dw = Math.max(1, (int) Math.round(gl.w * scale));
        return dw + boldShift(st) + tracking(cap);
    }

    public static int width(String s, Style st) {
        if (s == null || s.length() == 0) return 0;
        int total = 0;
        for (int i = 0; i < s.length(); i++) total += advanceOf(s.charAt(i), st);
        return Math.max(0, total - tracking(st.cap));
    }

    public static void draw(Graphics2D g, String s, int x, int baseline, Style st) {
        if (g == null || s == null || s.length() == 0) return;
        int cap = st.cap;
        double unitScale = cap / CAP_UNITS;
        int rgb = g.getColor().getRGB() & 0xFFFFFF;
        int bold = boldShift(st);
        int cx = x;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int adv = advanceOf(c, st);
            if (c != ' ' && c != '\t' && c != '\n' && c != '\r' && adv > 0) {
                Glyph gl = glyph(c);
                if (gl != null && gl.img != null) {
                    double scale = unitScale * (gl.units / gl.h);
                    int dw = Math.max(1, (int) Math.round(gl.w * scale));
                    int dh = Math.max(1, (int) Math.round(gl.units * unitScale));
                    BufferedImage img = scaled(gl, rgb, dw, dh);
                    if (img != null) {
                        int top = topFor(c, baseline, dh, cap);
                        g.drawImage(img, cx, top, null);
                        if (st.bold) g.drawImage(img, cx + bold, top, null);
                    }
                }
            }
            cx += adv;
        }
    }

    public static void drawCentered(Graphics2D g, String s, int cx, int baseline, Style st) {
        draw(g, s, cx - width(s, st) / 2, baseline, st);
    }
}
