package com.jscratch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Engine base: variables, lists, operators, script threads, and SFX clips.
 *
 * <p>Call graph / interactions:
 * <ul>
 *   <li>Parent of {@code Sprite} (hence every game sprite) and of the
 *       anonymous {@code Stage} backing object for globals.</li>
 *   <li>{@link #startScript(Runnable)} spawns a <b>daemon</b> thread per
 *       script — used by the player loop, projectile flights, timed VFX,
 *       and scale listeners. Daemon so closing the window always exits.</li>
 *   <li>{@link #playSound(String)} spawns a short <b>daemon</b> loader
 *       thread per SFX hit (long music uses {@code MusicManager} instead).</li>
 *   <li>Broadcasts delegate to {@code Stage.broadcast}; frame pacing via
 *       {@code Stage.waitNextFrame}.</li>
 * </ul>
 */
public abstract class ScratchObject {
    protected String name;
    protected Map<String, Object> variables = new ConcurrentHashMap<>();
    protected Map<String, List<Object>> lists = new ConcurrentHashMap<>();
    protected double volume = 100;
    protected List<Thread> activeScripts = new CopyOnWriteArrayList<>();

    public ScratchObject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // --- Variables ---
    public Object getVariable(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        }
        return 0; // Default
    }

    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }

    public void removeVariable(String name) {
        variables.remove(name);
    }
    
    public double getVariableAsNumber(String name) {
        return castToDouble(getVariable(name));
    }
    
    public String getVariableAsString(String name) {
        return castToString(getVariable(name));
    }

    public String answer() {
        return getVariableAsString("answer");
    }
    
    public void changeVariableBy(String name, double amount) {
        Object val = getVariable(name);
        double current = castToDouble(val);
        variables.put(name, current + amount);
    }

    // --- Lists ---
    public List<Object> getList(String name) {
        return lists.computeIfAbsent(name, k -> new CopyOnWriteArrayList<>());
    }

    public void addToList(String listName, Object item) {
        getList(listName).add(item);
    }
    
    public void deleteFromList(String listName, int index) {
        List<Object> list = getList(listName);
        if (index >= 1 && index <= list.size()) {
            list.remove(index - 1);
        }
    }
    
    public Object itemOfList(String listName, int index) {
        List<Object> list = getList(listName);
        if (index >= 1 && index <= list.size()) {
            return list.get(index - 1);
        }
        return "";
    }

    // --- Operators ---
    public int pickRandom(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }
    
    public String join(Object a, Object b) {
        return castToString(a) + castToString(b);
    }
    
    public String letterOf(int index, String text) {
        if (index >= 1 && index <= text.length()) {
            return String.valueOf(text.charAt(index - 1));
        }
        return "";
    }
    
    public int lengthOf(String text) {
        return text.length();
    }

    // --- Helpers ---
    protected double castToDouble(Object o) {
        if (o instanceof Number) return ((Number) o).doubleValue();
        try {
            return Double.parseDouble(o.toString());
        } catch (Exception e) {
            return 0;
        }
    }
    
    protected String castToString(Object o) {
        return o == null ? "" : o.toString();
    }

    // --- Script Execution ---
    public void startScript(Runnable script) {
        Thread t = new Thread(() -> {
            try {
                script.run();
            } finally {
                activeScripts.remove(Thread.currentThread());
            }
        });
        t.setDaemon(true);
        activeScripts.add(t);
        t.start();
    }

    public void broadcast(String message) {
        Stage.getInstance().broadcast(message);
    }
    
    public void whenIReceive(String message, Runnable action) {
        Stage.getInstance().whenIReceive(message, action);
    }

    public void whenKeyPressed(int keyCode, Runnable action) {
        Stage.getInstance().whenKeyPressed(keyCode, action);
    }
    
    public void waitTillNextFrame() {
        Stage.getInstance().waitNextFrame();
    }

    public void wait(double sec) {
        try {
            Thread.sleep((long) (sec * 1000));
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    // --- Audio ---
    private List<javax.sound.sampled.Clip> activeClips = new java.util.concurrent.CopyOnWriteArrayList<>();

    public void playSound(String soundPath) {
        Thread t = new Thread(() -> {
            try {
                javax.sound.sampled.AudioInputStream ais = null;
                java.io.File file = new java.io.File(soundPath);
                if (!file.exists()) {
                    String projectRoot = System.getProperty("jscratch.project.root");
                    if (projectRoot != null) {
                        file = new java.io.File(projectRoot, soundPath);
                    }
                }
                if (file.exists()) {
                    ais = javax.sound.sampled.AudioSystem.getAudioInputStream(file);
                } else {
                    // Fall back to classpath (covers external assets dir mounted
                    // beside the exported jar by GameBootstrap).
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    if (cl == null) cl = getClass().getClassLoader();
                    java.net.URL url = cl.getResource(soundPath);
                    if (url == null && soundPath.startsWith("/")) {
                        url = cl.getResource(soundPath.substring(1));
                    }
                    if (url == null) return;
                    ais = javax.sound.sampled.AudioSystem.getAudioInputStream(url);
                }
                javax.sound.sampled.Clip clip = javax.sound.sampled.AudioSystem.getClip();
                clip.open(ais);
                
                javax.sound.sampled.FloatControl gainControl = (javax.sound.sampled.FloatControl) clip.getControl(javax.sound.sampled.FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(volume / 100.0) / Math.log(10.0) * 20.0);
                if (dB < -80.0f) dB = -80.0f;
                gainControl.setValue(dB);

                activeClips.add(clip);
                clip.addLineListener(event -> {
                    if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                        clip.close();
                        activeClips.remove(clip);
                    }
                });
                clip.start();
            } catch (Exception e) {}
        });
        t.setDaemon(true);
        t.start();
    }

    public void stopAllSounds() {
        for (javax.sound.sampled.Clip clip : activeClips) {
            clip.stop();
            clip.close();
        }
        activeClips.clear();
    }

    public void setVolume(double percent) {
        this.volume = percent;
    }
}
