package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import javax.swing.JPanel;

import enemy.ENEMY_Huntman;
import entity.Entity;
import entity.Player;
import entity.Projectile;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {
    // screen settting
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; // 40x40 tile
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;

    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    // WORLD SETTING
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    // FPS
    public final int FPS = 60;

    // SYSTEM
    TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler(this);
    MouseHandler mouseH = new MouseHandler(this);
    Thread gameThread;
    public CollisionChecker cChecker = new CollisionChecker(this);
    public UI ui = new UI(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public EventHandler eHandler = new EventHandler(this);

    // ENTITY AND OBJECT
    public Player player = new Player(this, keyH);
    public Entity obj[] = new Entity[10];
    public Entity enemy[] = new Entity[20];
    ArrayList<Entity> entityList = new ArrayList<>();
    public ArrayList<Projectile> projectiles = new ArrayList<>(); // <-- แก้ตรงนี้
    public ArrayList<entity.Entity> particles = new ArrayList<>();
    public ArrayList<entity.Entity> items = new ArrayList<>();

    // GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int gameOverState = 3;
    public final int upgradeState = 4;

    // level timer
    public int levelDurationSeconds = 180; // 3 minutes default
    public long elapsedFrames = 0;
    public int coinsAtLevelStart = 0;
    public long finalTimeFrames = 0;  // store frames count at game over
    public int finalCoins = 0;        // store coins at game over

    // MAX SPAWN
    int spawnTimer = 0;
    int baseSpawnInterval = 300; // Base spawn interval (5 seconds at 60 FPS)
    int spawnInterval = baseSpawnInterval;
    int maxEnemies = 20;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // set size JPanel
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.addMouseListener(mouseH);
        this.setFocusable(true);
    }

    public void resetGame() {
        // Reset player current run values to permanent base stats
        if (player != null) {
            player.setDefaultValues();
        }
        
        // Reset spawn interval to base value
        spawnInterval = baseSpawnInterval;

        // Reset progression for a fresh run: level and EXP are per-run and should start fresh
        if (player != null) {
            player.level = 1;
            player.exp = 0;
            player.expToNext = 10;
        }

        // Clear all lists and world entities
        for (int i = 0; i < enemy.length; i++) enemy[i] = null;
        for (int i = 0; i < obj.length; i++) obj[i] = null;
        projectiles.clear();
        items.clear();
        particles.clear();

        // Reset counters
        elapsedFrames = 0;
        coinsAtLevelStart = (player != null) ? player.coins : 0;  // set new baseline for coin counting
        finalTimeFrames = 0;
        finalCoins = 0;
        spawnTimer = 0;
    }

    public void setupGame() {
        // Reset world and player run-state to base stats
        resetGame();
        aSetter.setObject();
        aSetter.setEnemy();
        gameState = titleState;
        this.requestFocusInWindow();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update();
            repaint();
            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if (gameState == playState) {
            // PLAYER
            player.update();

            // update timer
            elapsedFrames++;
            
            // Adjust spawn rate based on remaining time - spawn faster as time decreases
            int remainingSeconds = levelDurationSeconds - (int)(elapsedFrames / FPS);
            double timeRatio = (double)remainingSeconds / levelDurationSeconds; // 1.0 -> 0.0
            spawnInterval = (int)(baseSpawnInterval * Math.max(0.3, timeRatio)); // Fastest is 30% of base interval
            
            if (elapsedFrames >= (long)levelDurationSeconds * FPS) {
                // time's up -> game over
                finalTimeFrames = elapsedFrames;
                finalCoins = player.coins;
                // Reset per-run progression when showing Game Over
                if (player != null) {
                    player.level = 1;
                    player.exp = 0;
                    player.expToNext = 10;
                }
                gameState = gameOverState;
            }

            // ENEMY
            for (int i = 0; i < enemy.length; i++) {
                if (enemy[i] != null) {
                    enemy[i].update();

                    // If enemy just reached 0 life, start dying animation (once)
                    if (enemy[i].life <= 0 && !enemy[i].dying) {
                        enemy[i].dying = true;
                        enemy[i].deathCounter = 0;
                        enemy[i].alive = false;
                        // spawn particles for visual death effect
                        spawnDeathParticles(enemy[i], 12);
                    }

                    // If enemy finished dying, drop items and remove from array
                    if (enemy[i].readyToRemove) {
                        dropItems(enemy[i]);
                        enemy[i] = null;
                    }
                }
            }

            // Projectile update
            for (int i = 0; i < projectiles.size(); i++) {
                projectiles.get(i).update();
                if (!projectiles.get(i).isAlive()) {
                    projectiles.remove(i);
                    i--;
                }
            }

            // Particles update
            for (int i = 0; i < particles.size(); i++) {
                particles.get(i).update();
                if (particles.get(i).life <= 0) {
                    particles.remove(i);
                    i--;
                }
            }

            spawnTimer++;
            if (spawnTimer > spawnInterval && countAliveEnemies() < maxEnemies) {
                spawnRandomEnemyNearPlayer();
                spawnTimer = 0;
            }

            // Game over if player dead
            if (player.life <= 0) {
                finalTimeFrames = elapsedFrames;
                finalCoins = player.coins;
                // Reset per-run progression when showing Game Over
                if (player != null) {
                    player.level = 1;
                    player.exp = 0;
                    player.expToNext = 10;
                }
                gameState = gameOverState;
            }
        }
    }

    public int[] getRandomPositionNearPlayer(int rangeTiles) {
        Random rand = new Random();
        int col, row;

        int playerCol = player.worldX / tileSize;
        int playerRow = player.worldY / tileSize;

        for (int i = 0; i < 100; i++) {
            int offsetCol = rand.nextInt(rangeTiles * 2 + 1) - rangeTiles;
            int offsetRow = rand.nextInt(rangeTiles * 2 + 1) - rangeTiles;

            col = playerCol + offsetCol;
            row = playerRow + offsetRow;

            if (col < 0)
                col = 0;
            if (col >= maxWorldCol)
                col = maxWorldCol - 1;
            if (row < 0)
                row = 0;
            if (row >= maxWorldRow)
                row = maxWorldRow - 1;

            if (!tileM.tile[tileM.mapTileNum[col][row]].collision) {
                int worldX = col * tileSize;
                int worldY = row * tileSize;
                double distance = Math.sqrt(Math.pow(worldX - player.worldX, 2) + Math.pow(worldY - player.worldY, 2));
                if (distance > tileSize * 2) {
                    return new int[] { worldX, worldY };
                }
            }
        }
        return new int[] { player.worldX, player.worldY };
    }

    public void spawnRandomEnemyNearPlayer() {
        for (int i = 0; i < enemy.length; i++) {
            if (enemy[i] == null) {
                ENEMY_Huntman newEnemy = new ENEMY_Huntman(this);
                enemy[i] = newEnemy;
                break;
            }
        }
    }

    public void startNextLevel() {
        // Record coins at start of this level/run
        coinsAtLevelStart = (player != null) ? player.coins : 0;

        // Reset player current run values from base stats (keep permanent upgrades)
        if (player != null) player.setDefaultValues();

        // Clear existing enemies and objects
        for (int i = 0; i < enemy.length; i++) enemy[i] = null;
        for (int i = 0; i < obj.length; i++) obj[i] = null;
        projectiles.clear();
        items.clear();
        particles.clear();

        // Reset timers
        elapsedFrames = 0;
        spawnTimer = 0;

        // Return to play state
        gameState = playState;
    }

    public void spawnDeathParticles(entity.Entity source, int amount) {
        if (source == null) return;
        java.util.Random rnd = new java.util.Random();
        for (int i = 0; i < amount; i++) {
            object.OBJ_Particle p = new object.OBJ_Particle(this, rnd.nextInt(8)-4, rnd.nextInt(8)-4);
            p.worldX = source.worldX + rnd.nextInt(this.tileSize) - this.tileSize/2;
            p.worldY = source.worldY + rnd.nextInt(this.tileSize) - this.tileSize/2;
            particles.add(p);
        }
    }

    public int countAliveEnemies() {
        int count = 0;
        for (Entity e : enemy) {
            if (e != null) {
                count++;
            }
        }
        return count;
    }

    // Drop items (Soul and Coins) when an enemy dies
    public void dropItems(Entity deadEnemy) {
        if (deadEnemy == null) return;

        // Spawn a soul
        boolean placed = false;
        for (int i = 0; i < obj.length; i++) {
            if (obj[i] == null) {
                object.OBJ_Soul soul = new object.OBJ_Soul(this);
                soul.worldX = deadEnemy.worldX;
                soul.worldY = deadEnemy.worldY;
                obj[i] = soul;
                placed = true;
                break;
            }
        }
        if (!placed) {
            object.OBJ_Soul soul = new object.OBJ_Soul(this);
            soul.worldX = deadEnemy.worldX;
            soul.worldY = deadEnemy.worldY;
            items.add(soul);
        }

        // Spawn some coins (1-3)
        int coinsToDrop = new java.util.Random().nextInt(3) + 1;
        for (int c = 0; c < coinsToDrop; c++) {
            boolean placedCoin = false;
            for (int i = 0; i < obj.length; i++) {
                if (obj[i] == null) {
                    object.OBJ_Coin coin = new object.OBJ_Coin(this);
                    coin.worldX = deadEnemy.worldX + (c * 8);
                    coin.worldY = deadEnemy.worldY + (c * 8);
                    obj[i] = coin;
                    placedCoin = true;
                    break;
                }
            }
            if (!placedCoin) {
                object.OBJ_Coin coin = new object.OBJ_Coin(this);
                coin.worldX = deadEnemy.worldX + (c * 8);
                coin.worldY = deadEnemy.worldY + (c * 8);
                items.add(coin);
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == titleState) {
            ui.draw(g2);
        } else if (gameState == gameOverState) {
            ui.draw(g2);
        } else if (gameState == upgradeState) {
            ui.draw(g2);
        } else {
            // TILE
            tileM.draw(g2);

            // PARTICLES
            for (int i = 0; i < particles.size(); i++) {
                particles.get(i).draw(g2);
            }

            // ENTITY
            entityList.add(player);
            for (int i = 0; i < enemy.length; i++) {
                if (enemy[i] != null)
                    entityList.add(enemy[i]);
            }
            for (int i = 0; i < obj.length; i++) {
                if (obj[i] != null)
                    entityList.add(obj[i]);
            }
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i) != null)
                    entityList.add(items.get(i));
            }

            // SORT
            Collections.sort(entityList, Comparator.comparingInt(e -> e.worldY));
            // PROJECTILE
            for (int i = 0; i < projectiles.size(); i++) {
                projectiles.get(i).draw(g2);
            }

            // DRAW PROJECTILES
            for (Projectile p : projectiles) {
                p.draw(g2);
            }

            // DRAW ENTITY
            for (Entity e : entityList) {
                e.draw(g2);
            }
            entityList.clear();

            // UI
            ui.draw(g2);
        }

        g2.dispose();
    }
}
