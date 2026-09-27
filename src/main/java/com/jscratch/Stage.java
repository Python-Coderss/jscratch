package com.jscratch;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;

/**
 * Engine hub: owns the sprite draw list, the 60fps render loop, input state,
 * broadcasts, and stage variables/watchers.
 *
 * <p>Call graph / interactions:
 * <ul>
 *   <li>Opened by {@link #run(Stage, Runnable)} — called from
 *       {@code GameBootstrap} (exported jar) or {@code ProjectRunner}
 *       (in-editor run), which then invokes {@code generated.Main}.</li>
 *   <li>{@code generated.Scroller2d} reads {@link #getScaleFactor()} every
 *       update and positions all world/UI sprites from it.</li>
 *   <li>{@code generated.Sprite1} (player loop) drives the game by calling
 *       {@code EnemyManager.update}, {@code FloorManager.update},
 *       {@code MusicDirector.update} and {@code LootDrop.checkAll} once per
 *       frame, then {@code waitTillNextFrame()} here to pace itself.</li>
 *   <li>Sprites register via {@link #addSprite(Sprite)}; layer order is
 *       managed with {@link #goToFront(Sprite)}/{@link #goToBack(Sprite)}.
 *       These are cheap synchronized memmoves (NOT copy-on-write), but
 *       callers must still avoid calling them per frame — see Scroller2d.</li>
 *   <li>Scripts run on daemon threads via {@code ScratchObject.startScript};
 *       they block on {@link #waitNextFrame()} which the render timer
 *       releases. Sounds stop on {@code clearSprites/disposeStage}.</li>
 * </ul>
 */
public class Stage extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    public static Stage instance;
    private JFrame frame;
    private Timer renderTimer;

    // Direct translations for Scratch concepts
    // NOTE: intentionally NOT CopyOnWriteArrayList. goToFront/goToBack run
    // per frame; COW copies the whole array on every remove+add (2 allocs
    // per call, ~250/frame from the minimap + HP bars). A synchronized
    // ArrayList makes reorder a cheap memmove; paint/click iterate a snapshot.
    private final List<Sprite> sprites = java.util.Collections.synchronizedList(new ArrayList<>());
    private List<Costume> backdrops = new ArrayList<>();
    private int backdropIndex = 0;
    
    // Stage properties
    private String currentBackdropName = "";
    private double tempo = 60;
    private double timerValue = 0;
    private long startTime;

    // Input state
    private int mouseX = 0;
    private int mouseY = 0;
    private boolean mouseDown = false;
    private final boolean[] keysDown = new boolean[65536];
    
    // Frame Sync
    private java.util.concurrent.CountDownLatch frameLatch = new java.util.concurrent.CountDownLatch(1);
    
    // Event Listeners
    private java.util.Map<String, List<Runnable>> broadcastListeners = new java.util.concurrent.ConcurrentHashMap<>();
    private java.util.Map<Integer, List<Runnable>> keyListeners = new java.util.concurrent.ConcurrentHashMap<>();
    private List<java.util.function.BiConsumer<Double, Double>> scaleListeners = new java.util.concurrent.CopyOnWriteArrayList<>();
    
    // Backing ScratchObject for global vars/lists/sounds
    private final ScratchObject stageObject;
    private double currentScale = 1.0;

    public Stage() {
        this.stageObject = new ScratchObject("Stage") {};
        
        this.setPreferredSize(new Dimension(480, 360));
        this.setBackground(Color.WHITE);
        this.setFocusable(true);
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        // Dropped keys wedged movement (e.g. after clicking a toolbar
        // button): a key released while unfocused never clears, so reset
        // all input the moment focus leaves the canvas.
        this.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                java.util.Arrays.fill(keysDown, false);
            }
        });
        
        renderTimer = new Timer(1000 / 60, e -> {
            update();
            repaint();
            java.util.concurrent.CountDownLatch oldLatch = frameLatch;
            frameLatch = new java.util.concurrent.CountDownLatch(1);
            oldLatch.countDown();
        });
        renderTimer.start();
        
        startTime = System.currentTimeMillis();
    }
    
    // --- Delegate ScratchObject Methods ---
    public Object getVariable(String name) { return stageObject.getVariable(name); }
    public double getVariableAsNumber(String name) { return stageObject.getVariableAsNumber(name); }
    public String getVariableAsString(String name) { return stageObject.getVariableAsString(name); }
    public String answer() { return stageObject.answer(); }
    public void setVariable(String name, Object value) { stageObject.setVariable(name, value); }
    public void removeVariable(String name) { stageObject.removeVariable(name); }
    public void changeVariableBy(String name, double amount) { stageObject.changeVariableBy(name, amount); }
    public void addToList(String listName, Object item) { stageObject.addToList(listName, item); }
    public String join(Object a, Object b) { return stageObject.join(a, b); }
    public void setVolume(double vol) { stageObject.setVolume(vol); }
    public void playSound(String path) { stageObject.playSound(path); }
    public void startScript(Runnable script) { stageObject.startScript(script); }

    // --- Broadcasts & Keys ---
    public void broadcast(String message) {
        List<Runnable> listeners = broadcastListeners.get(message);
        if (listeners != null) {
            for (Runnable r : listeners) {
                stageObject.startScript(r);
            }
        }
    }

    public void whenIReceive(String message, Runnable action) {
        broadcastListeners.computeIfAbsent(message, k -> new CopyOnWriteArrayList<>()).add(action);
    }
    
    public void whenKeyPressed(int keyCode, Runnable action) {
        keyListeners.computeIfAbsent(keyCode, k -> new java.util.concurrent.CopyOnWriteArrayList<>()).add(action);
    }

    public void whenScaleChanged(java.util.function.BiConsumer<Double, Double> action) {
        scaleListeners.add(action);
    }

    public boolean isKeyPressed(String keyName) {
        switch(keyName.toLowerCase()) {
            case "space": return isKeyPressed(KeyEvent.VK_SPACE);
            case "up arrow": return isKeyPressed(KeyEvent.VK_UP);
            case "down arrow": return isKeyPressed(KeyEvent.VK_DOWN);
            case "right arrow": return isKeyPressed(KeyEvent.VK_RIGHT);
            case "left arrow": return isKeyPressed(KeyEvent.VK_LEFT);
            case "any": 
                for(boolean b : keysDown) if(b) return true;
                return false;
            default:
                if(keyName.length() == 1) {
                    char c = keyName.toUpperCase().charAt(0);
                    return isKeyPressed((int)c);
                }
                return false;
        }
    }
    
    public void waitNextFrame() {
        try { frameLatch.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
    
    public void waitTillNextFrame() {
        waitNextFrame();
    }
    
    // --- Setup ---
    public void addSprite(Sprite s) {
        synchronized (sprites) {
            if (!sprites.contains(s)) sprites.add(s);
        }
        s.setStage(this);
    }
    
    public void goToFront(Sprite s) {
        synchronized (sprites) {
            if (sprites.remove(s)) {
                sprites.add(s);
            }
        }
    }

    public void goToBack(Sprite s) {
        synchronized (sprites) {
            if (sprites.remove(s)) {
                sprites.add(0, s);
            }
        }
    }
    
    public void addBackdrop(Costume c) {
        backdrops.add(c);
        if (backdrops.size() == 1) {
            switchBackdropTo(c.getName());
        }
    }
    
    public Costume getBackdrop() {
        if (!backdrops.isEmpty() && backdropIndex < backdrops.size()) {
            return backdrops.get(backdropIndex);
        }
        return null;
    }

    private void update() {
        timerValue = (System.currentTimeMillis() - startTime) / 1000.0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!backdrops.isEmpty() && backdropIndex < backdrops.size()) {
            BufferedImage bg = backdrops.get(backdropIndex).getImage();
            g2d.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
        }

        List<Sprite> snap;
        synchronized (sprites) {
            snap = new ArrayList<>(sprites);
        }
        for (Sprite s : snap) {
            if (s.isVisible()) {
                s.draw(g2d);
            }
        }

        // No variable watchers here by design: stage/sprite vars are never
        // rendered on screen.
    }

    public void switchBackdropTo(String name) {
        for (int i = 0; i < backdrops.size(); i++) {
            if (backdrops.get(i).getName().equals(name)) {
                backdropIndex = i;
                currentBackdropName = name;
                break;
            }
        }
        repaint();
    }
    
    public void nextBackdrop() {
        backdropIndex = (backdropIndex + 1) % backdrops.size();
        currentBackdropName = backdrops.get(backdropIndex).getName();
        repaint();
    }

    public int getMouseX() { return mouseX - getWidth() / 2; }
    public int getMouseY() { return -(mouseY - getHeight() / 2); }
    public int getStageWidth() { return getWidth(); }
    public int getStageHeight() { return getHeight(); }
    public double getScaleFactor() { return getWidth() / 480.0; }
    public boolean isMouseDown() { return mouseDown; }
    public boolean isKeyPressed(int keyCode) {
        return (keyCode >= 0 && keyCode < keysDown.length) ? keysDown[keyCode] : false;
    }
    public double getTimer() { return timerValue; }
    public void resetTimer() { startTime = System.currentTimeMillis(); }

    public void setStageScale(int scale, JFrame frame) {
        if (currentScale == scale) return;
        double oldScale = currentScale;
        currentScale = (double)scale;
        this.setPreferredSize(new Dimension(480 * scale, 360 * scale));
        SwingUtilities.invokeLater(() -> {
            if (frame != null) {
                frame.pack();
            }
            this.requestFocusInWindow();
        });
        for (java.util.function.BiConsumer<Double, Double> listener : scaleListeners) {
            listener.accept(oldScale, currentScale);
        }
        List<Sprite> snap;
        synchronized (sprites) {
            snap = new ArrayList<>(sprites);
        }
        for (Sprite s : snap) {
            s.handleScaleChange(oldScale, currentScale);
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code < keysDown.length) keysDown[code] = true;
        List<Runnable> listeners = keyListeners.get(code);
        if (listeners != null) for (Runnable r : listeners) stageObject.startScript(r);
    }
    @Override public void keyReleased(KeyEvent e) { if (e.getKeyCode() < keysDown.length) keysDown[e.getKeyCode()] = false; }
    @Override public void mouseClicked(MouseEvent e) {
        List<Sprite> snap;
        synchronized (sprites) {
            snap = new ArrayList<>(sprites);
        }
        for (int i = snap.size() - 1; i >= 0; i--) {
            Sprite s = snap.get(i);
            if (s.containsPoint(e.getX(), e.getY())) {
                s.handleMouseClick(e.getX(), e.getY());
                break;
            }
        }
    }
    @Override public void mousePressed(MouseEvent e) {
        mouseDown = true;
        // Clicking the canvas must grab keyboard focus: a JPanel does not
        // reliably take focus on click, so without this the first click
        // (e.g. Descend) works but all subsequent keys go nowhere.
        requestFocusInWindow();
    }
    @Override public void mouseReleased(MouseEvent e) { mouseDown = false; }
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
    @Override public void mouseMoved(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }

    public static void run(Runnable setup) {
        run(new Stage(), setup);
    }

    /**
     * Opens the stage in a new window, then runs the given setup on the EDT.
     * The default close operation is DISPOSE_ON_CLOSE (used by the editor and
     * the in-editor runner); use {@link #run(Stage, Runnable, int)} for other
     * behaviors such as EXIT_ON_CLOSE for standalone exported games.
     */
    public static void run(Stage stage, Runnable setup) {
        run(stage, setup, javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static void run(Stage stage, Runnable setup, int closeOperation) {
        instance = stage;
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("JScratch Stage");
            stage.frame = frame;
            frame.setLayout(new BorderLayout());
            
            JToolBar toolBar = new JToolBar();
            toolBar.setFloatable(false);
            // Scale buttons must never park keyboard focus (a focused
            // button eats Space/Enter and WASD never reaches the canvas).
            
            JButton btn1x = new JButton("480x360");
            btn1x.setFocusable(false);
            btn1x.addActionListener(e -> stage.setStageScale(1, frame));
            toolBar.add(btn1x);
            
            JButton btn2x = new JButton("960x720");
            btn2x.setFocusable(false);
            btn2x.addActionListener(e -> stage.setStageScale(2, frame));
            toolBar.add(btn2x);

            JButton btn3x = new JButton("1440x1080");
            btn3x.setFocusable(false);
            btn3x.addActionListener(e -> stage.setStageScale(3, frame));
            toolBar.add(btn3x);
            
            frame.add(toolBar, BorderLayout.NORTH);
            frame.add(stage, BorderLayout.CENTER);

            // Alt-Tab back (or any activation without a canvas click) must
            // restore keys to the canvas, not to whichever button had focus.
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowActivated(java.awt.event.WindowEvent e) {
                    stage.requestFocusInWindow();
                }
            });
            
            frame.pack();
            frame.setDefaultCloseOperation(closeOperation);
            frame.setVisible(true);
            stage.requestFocusInWindow();
            setup.run();
        });
    }

    public void clearSprites() {
        synchronized (sprites) {
            sprites.clear();
        }
        broadcastListeners.clear();
        keyListeners.clear();
        stageObject.stopAllSounds();
        stageObject.activeScripts.clear();
        stageObject.variables.clear();
        stageObject.lists.clear();
    }

    public void disposeStage() {
        if (renderTimer != null) renderTimer.stop();
        try { stageObject.stopAllSounds(); } catch (Exception ignored) {}
        if (frame != null) {
            frame.setVisible(false);
            frame.dispose();
        }
        if (instance == this) instance = null;
    }

    public static Stage getInstance() {
        if (instance == null) {
            instance = new Stage();
        }
        return instance;
    }
}
