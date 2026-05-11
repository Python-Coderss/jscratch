package com.jscratch;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Sprite extends ScratchObject {
    public enum RotationStyle { ALL_AROUND, LEFT_RIGHT, DONT_ROTATE }

    // Motion
    private double x = 0;
    private double y = 0;
    private double direction = 90;
    private RotationStyle rotationStyle = RotationStyle.ALL_AROUND;

    // Looks
    private boolean visible = true;
    private double size = 100;
    private double scaleX = 1.0;
    private double scaleY = 1.0;
    private List<Costume> costumes = new ArrayList<>();
    private int costumeIndex = 0;
    
    // Effects
    private double ghostEffect = 0;
    private double brightnessEffect = 0;
    private double colorEffect = 0;

    private Stage stage;
    private List<Runnable> clickListeners = new java.util.concurrent.CopyOnWriteArrayList<>();
    private List<java.util.function.BiConsumer<Double, Double>> scaleListeners = new java.util.concurrent.CopyOnWriteArrayList<>();

    public Sprite(String name) { super(name); }
    public static BufferedImage createPlaceholder(Color c, int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(c);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, w - 1, h - 1);
        g.dispose();
        return img;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getScaleX() { return scaleX; }
    public double getScaleY() { return scaleY; }
    public double getWidth() { return width(); }
    public double getHeight() { return height(); }

    public void whenClicked(Runnable action) {
        clickListeners.add(action);
    }

    public void whenScaleChanged(java.util.function.BiConsumer<Double, Double> action) {
        scaleListeners.add(action);
    }

    public boolean containsPoint(int mx, int my) {
        if (!visible || costumes.isEmpty()) return false;
        Costume c = costumes.get(costumeIndex);
        BufferedImage img = c.getImage();
        if (img == null) return false;

        try {
            java.awt.geom.AffineTransform tx = new java.awt.geom.AffineTransform();
            double screenX = x + stage.getWidth() / 2.0;
            double screenY = stage.getHeight() / 2.0 - y;

            tx.translate(screenX, screenY);
            tx.scale((size / 100.0) * scaleX, (size / 100.0) * scaleY);

            if (rotationStyle == RotationStyle.ALL_AROUND) {
                tx.rotate(Math.toRadians(direction - 90));
            } else if (rotationStyle == RotationStyle.LEFT_RIGHT && (direction < 0 || (direction > 180 && direction < 360))) {
                tx.scale(-1, 1);
            }
            tx.translate(-c.getRotationCenterX(), -c.getRotationCenterY());

            java.awt.geom.AffineTransform inv = tx.createInverse();
            java.awt.geom.Point2D local = inv.transform(new java.awt.geom.Point2D.Double(mx, my), null);

            int ix = (int) local.getX();
            int iy = (int) local.getY();

            if (ix >= 0 && ix < img.getWidth() && iy >= 0 && iy < img.getHeight()) {
                int argb = img.getRGB(ix, iy);
                return ((argb >> 24) & 0xff) > 0;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public void handleMouseClick(int mx, int my) {
        // containsPoint check is now expected to be done by the caller (Stage) 
        // to handle layering, but we keep it here for safety if called directly.
        if (visible) {
            for (Runnable r : clickListeners) {
                startScript(r);
            }
        }
    }

    public void handleScaleChange(double oldScale, double newScale) {
        for (java.util.function.BiConsumer<Double, Double> listener : scaleListeners) {
            startScript(() -> listener.accept(oldScale, newScale));
        }
    }

    // --- Drawing ---
    public void draw(Graphics2D g) {
        if (!visible || costumes.isEmpty()) return;
        Costume c = costumes.get(costumeIndex);
        BufferedImage img = c.getImage();
        if (img == null) return;

        AffineTransform old = g.getTransform();
        double screenX = x + stage.getWidth() / 2.0;
        double screenY = stage.getHeight() / 2.0 - y;

        g.translate(screenX, screenY);
        g.scale((size / 100.0) * scaleX, (size / 100.0) * scaleY);

        if (rotationStyle == RotationStyle.ALL_AROUND) {
            g.rotate(Math.toRadians(direction - 90));
        } else if (rotationStyle == RotationStyle.LEFT_RIGHT && (direction < 0 || (direction > 180 && direction < 360))) {
            g.scale(-1, 1);
        }

        if (ghostEffect > 0) {
            float alpha = (float) Math.max(0, Math.min(1, 1.0 - (ghostEffect / 100.0)));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        }

        if (brightnessEffect != 0) {
             g.drawImage(img, -c.getRotationCenterX(), -c.getRotationCenterY(), null);
             g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, (float)(Math.abs(brightnessEffect)/200.0)));
             g.setColor(brightnessEffect > 0 ? Color.WHITE : Color.BLACK);
             g.fillRect(-c.getRotationCenterX(), -c.getRotationCenterY(), img.getWidth(), img.getHeight());
        } else {
             g.drawImage(img, -c.getRotationCenterX(), -c.getRotationCenterY(), null);
        }

        g.setTransform(old);
        g.setComposite(AlphaComposite.SrcOver);
    }
    
    public double width() { return costumes.isEmpty() ? 0 : costumes.get(costumeIndex).getImage().getWidth() * (size/100.0) * Math.abs(scaleX); }
    public double height() { return costumes.isEmpty() ? 0 : costumes.get(costumeIndex).getImage().getHeight() * (size/100.0) * Math.abs(scaleY); }
    public void setScaleX(double sx) { this.scaleX = sx; }
    public void setScaleY(double sy) { this.scaleY = sy; }

    public Rectangle getBounds() {
        int w = (int)width(), h = (int)height();
        return new Rectangle((int)(x + stage.getWidth()/2.0 - w/2), (int)(stage.getHeight()/2.0 - y - h/2), w, h);
    }

    // --- Blocks ---
    public void move(double steps) {
        double rad = Math.toRadians(90 - direction);
        x += steps * Math.cos(rad);
        y += steps * Math.sin(rad);
    }
    public void turnRight(double deg) { direction += deg; normalize(); }
    public void turnLeft(double deg) { direction -= deg; normalize(); }
    public void pointInDirection(double dir) { direction = dir; normalize(); }
    public void goTo(double x, double y) { this.x = x; this.y = y; }
    public void changeXBy(double dx) { x += dx; }
    public void changeYBy(double dy) { y += dy; }
    
    public void glide(double sec, double tx, double ty) {
        long dur = (long)(sec * 1000), start = System.currentTimeMillis();
        double sx = x, sy = y;
        while (System.currentTimeMillis() - start < dur) {
            double p = (System.currentTimeMillis() - start) / (double)dur;
            x = sx + (tx - sx) * p; y = sy + (ty - sy) * p;
            waitTillNextFrame();
        }
        x = tx; y = ty;
    }

    public void goToRandomPosition() {
        int w = stage.getWidth()/2, h = stage.getHeight()/2;
        x = (Math.random()*w*2)-w; y = (Math.random()*h*2)-h;
    }

    public void ifOnEdgeBounce() {
        int w = stage.getWidth()/2, h = stage.getHeight()/2;
        if (x > w) { x = w; direction = -direction; }
        if (x < -w) { x = -w; direction = -direction; }
        if (y > h) { y = h; direction = 180 - direction; }
        if (y < -h) { y = -h; direction = 180 - direction; }
        normalize();
    }

    public void setEffect(String effect, double value) {
        if (effect.equalsIgnoreCase("ghost")) ghostEffect = value;
        else if (effect.equalsIgnoreCase("brightness")) brightnessEffect = value;
    }
    public void changeEffectBy(String effect, double amount) {
        if (effect.equalsIgnoreCase("ghost")) ghostEffect += amount;
        else if (effect.equalsIgnoreCase("brightness")) brightnessEffect += amount;
    }
    public void clearGraphicsEffects() { ghostEffect = 0; brightnessEffect = 0; colorEffect = 0; }

    public void say(String text) { System.out.println(name + ": " + text); }
    public void say(String text, double sec) { say(text); wait(sec); say(""); }

    public void switchCostume(String n) { 
    	
    	for(int i=0; i<costumes.size(); i++) {
    		if(costumes.get(i).getName().equals(n)) {

    			costumeIndex = i; 
    			
    			return;
    		} 
    	}
		costumeIndex = costumes.size()-1;
		
    	}
    public void nextCostume() { if(!costumes.isEmpty()) costumeIndex = (costumeIndex+1)%costumes.size(); }
    public void setSize(double s) { size = s; }
    public void changeSizeBy(double a) { size += a; }
    public void show() { visible = true; }
    public void hide() { visible = false; }
    public boolean isVisible() { return visible; }
    public void addCostume(Costume c) { costumes.add(c); }

    public boolean isTouching(Sprite o) { return getBounds().intersects(o.getBounds()); }
    public boolean isTouchingColor(Color c) {
        Costume b = stage.getBackdrop();
        if (b == null) return false;
        int sx = (int)(x + stage.getWidth()/2.0), sy = (int)(stage.getHeight()/2.0 - y);
        if (sx < 0 || sx >= b.getImage().getWidth() || sy < 0 || sy >= b.getImage().getHeight()) return false;
        return new Color(b.getImage().getRGB(sx, sy)).equals(c);
    }

    private void normalize() { while(direction > 180) direction -= 360; while(direction <= -180) direction += 360; }
    public void setRotationStyle(RotationStyle s) { rotationStyle = s; }
    
    @Override
    public Object getVariable(String name) {
        switch(name.toLowerCase()) {
            case "ghost": return ghostEffect;
            case "brightness": return brightnessEffect;
            case "x": return x;
            case "y": return y;
            case "direction": return direction;
            case "size": return size;
        }
        return super.getVariable(name);
    }
}
