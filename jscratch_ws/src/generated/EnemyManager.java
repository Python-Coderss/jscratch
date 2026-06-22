package generated;

import com.jscratch.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages spawning and updating all enemies.
 */
public class EnemyManager {
    private static EnemyManager instance;
    private List<EnemySprite> enemies = new ArrayList<>();
    private Random random = new Random();
    private long lastSpawnTime = 0;
    private int spawnInterval = 3000; // 3 seconds

    public static int enemyLimit = 15;
    public static int enemyCount = 0; 


    public static EnemyManager getInstance() {
        if (instance == null) instance = new EnemyManager();
        return instance;
    }

    public void update(int playerX, int playerY) {
        // 1. Spawning Logic
        if (System.currentTimeMillis() - lastSpawnTime > spawnInterval) {
            spawnEnemy(playerX, playerY);
            lastSpawnTime = System.currentTimeMillis();
        }

        // 2. Update all active enemies
        for (int i = enemies.size() - 1; i >= 0; i--) {
            EnemySprite e = enemies.get(i);
            if (e.isVisible()) {
                e.think(playerX, playerY);
            } else {
                enemies.remove(i);
            }
        }
    }

    private void spawnEnemy(int playerX, int playerY) {
        // Spawn randomly around the player just outside of view
        if (enemyCount >= enemyLimit) return; //ensure limit
        int radius = 400;
        double angle = random.nextDouble() * Math.PI * 2;
        int spawnX = playerX + (int)(Math.cos(angle) * radius);
        int spawnY = playerY + (int)(Math.sin(angle) * radius);

        int type = random.nextInt(3);
        EnemySprite e;
        
        switch (type) {
            case 0: e = new Orc("Orc_" + enemies.size()); break;
            case 1: e = new Goblin("Goblin_" + enemies.size()); break;
            default: e = new Zombie("Zombie_" + enemies.size()); break;
        }

        e.goTo(spawnX, spawnY);
        Stage.getInstance().addSprite(e);
        enemies.add(e);
        enemyCount++;
    }
    
    public List<EnemySprite> getEnemies() {
        return enemies;
    }
}
