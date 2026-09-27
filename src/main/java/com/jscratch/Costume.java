package com.jscratch;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * Named image + rotation center. Loaded from disk ({@code jscratch.project.root}
 * aware) or the classpath so exported jars keep resolving.
 *
 * <p>Call graph / interactions:
 * <ul>
 *   <li>Created directly ({@code new Costume}) or shared via game-layer
 *       {@code generated.ResourcePool.getCostume} (one image per id).</li>
 *   <li>Owned by {@code Sprite}; drawn in {@code Sprite.draw},
 *       hit-tested in {@code Sprite.containsPoint}.</li>
 *   <li>{@code FloorManager} re-tints the shared tile costumes in place,
 *       so world + minimap update together.</li>
 * </ul>
 */
public class Costume {
    private String name;
    private BufferedImage image;
    private int rotationCenterX;
    private int rotationCenterY;

    public Costume(String name, String imagePath, int cx, int cy) {
        this.name = name;
        this.rotationCenterX = cx;
        this.rotationCenterY = cy;
        try {
            File f = new File(imagePath);
            if (!f.exists()) {
                String projectRoot = System.getProperty("jscratch.project.root");
                if (projectRoot != null) {
                    f = new File(projectRoot, imagePath);
                }
            }

            if (f.exists()) {
                this.image = ImageIO.read(f);
            } else {
                // Try classpath via context class loader
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl == null) cl = getClass().getClassLoader();
                
                URL url = cl.getResource(imagePath);
                if (url == null && imagePath.startsWith("/")) {
                    url = cl.getResource(imagePath.substring(1));
                }
                if (url == null && !imagePath.startsWith("/")) {
                    url = cl.getResource("/" + imagePath);
                }
                
                if (url != null) {
                    this.image = ImageIO.read(url);
                } else {
                    System.err.println("Failed to load costume: " + imagePath + " (Checked project root: " + System.getProperty("jscratch.project.root") + ")");
                    this.image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB); 
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading costume: " + imagePath);
            this.image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        }
    }
    
    public Costume(String name, BufferedImage img, int cx, int cy) {
        this.name = name;
        this.image = img;
        this.rotationCenterX = cx;
        this.rotationCenterY = cy;
    }

    public BufferedImage getImage() {
        return image;
    }
    
    public String getName() {
        return name;
    }

    public int getRotationCenterX() {
        return rotationCenterX;
    }

    public int getRotationCenterY() {
        return rotationCenterY;
    }
}
