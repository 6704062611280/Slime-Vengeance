package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import javax.swing.JPanel;

import enemy.ENEMY_Huntman;
import entity.Entity;
import entity.Player;

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
    int FPS = 60;

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

    // GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;

    // MAX SPAWN
    int spawnTimer = 0;
    int spawnInterval = 300; // ทุก 300 เฟรม
    int maxEnemies = 20;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // set size JPanel
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.addMouseListener(mouseH); // ✅ เพิ่มตรงนี้
        this.setFocusable(true);
    }

    public void setupGame() {
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

        double drawInterval = 1000000000 / FPS; // 0.01666 seconds
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {

            // 1. UPDATE: information such as character positions
            update();
            // 2. DRAW: draw the screen with the update information
            repaint();
            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);

                nextDrawTime += drawInterval;
                // System.out.println("RUN");
            } catch (Exception e) {
                // TODO: handle exception
            }

        }

    }

    public void update() {

        if (gameState == playState) {
            // PLAYER
            player.update();

            // ENEMY
            for (int i = 0; i < enemy.length; i++) {
            if (enemy[i] != null) {
            enemy[i].update();
            }
            }

            spawnTimer++;
            if (spawnTimer > spawnInterval && countAliveEnemies() < maxEnemies) {
                spawnRandomEnemyNearPlayer();
                spawnTimer = 0;
            }

        }
        if (gameState == pauseState) {
            // nothing
        }

    }

    // RANDOM SPAWN ENEMY
    public int[] getRandomPositionNearPlayer(int rangeTiles) {
    Random rand = new Random();
    int col, row;

    int playerCol = player.worldX / tileSize;
    int playerRow = player.worldY / tileSize;

    for (int i = 0; i < 100; i++) { // ลองสุ่มไม่เกิน 100 ครั้ง
        int offsetCol = rand.nextInt(rangeTiles * 2 + 1) - rangeTiles;
        int offsetRow = rand.nextInt(rangeTiles * 2 + 1) - rangeTiles;

        col = playerCol + offsetCol;
        row = playerRow + offsetRow;

        // ตรวจสอบว่าภายในขอบเขตของแมพ
        if (col < 0) col = 0;
        if (col >= maxWorldCol) col = maxWorldCol - 1;
        if (row < 0) row = 0;
        if (row >= maxWorldRow) row = maxWorldRow - 1;

        // ตรวจสอบว่า tile เดินได้ไหม
        if (!tileM.tile[tileM.mapTileNum[col][row]].collision) {
            int worldX = col * tileSize;
            int worldY = row * tileSize;

            // ตรวจสอบไม่ให้เกิดทับผู้เล่นตรง ๆ (ห่างอย่างน้อย 2 tile)
            double distance = Math.sqrt(Math.pow(worldX - player.worldX, 2) + Math.pow(worldY - player.worldY, 2));
            if (distance > tileSize * 2) {
                return new int[]{worldX, worldY};
            }
        }
    }

    // ถ้าหาไม่ได้คืนตำแหน่งผู้เล่น (กัน error)
    return new int[]{player.worldX, player.worldY};
}


    public void spawnRandomEnemyNearPlayer() {
        for (int i = 0; i < enemy.length; i++) {
            if (enemy[i] == null) {
                ENEMY_Huntman newEnemy = new ENEMY_Huntman(this);
                enemy[i] = newEnemy;
                break; // spawn ทีละตัว
            }
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

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // TITLE SCREEN
        if (gameState == titleState) {
            ui.draw(g2);
        }
        // OTHERS
        else {
            // TILE
            tileM.draw(g2);

            // ADD ENTITY TO THE LIST
            entityList.add(player);

            for (int i = 0; i < enemy.length; i++) {
                if (enemy[i] != null) {
                    entityList.add(enemy[i]);
                }
            }

            for (int i = 0; i < obj.length; i++) {
                if (obj[i] != null) {
                    entityList.add(obj[i]);
                }
            }

            // SORT
            Collections.sort(entityList, new Comparator<Entity>() {

                @Override
                public int compare(Entity e1, Entity e2) {

                    int result = Integer.compare(e1.worldY, e2.worldY);
                    return result;
                }

            });

            // DRAW ENTITY
            for (int i = 0; i < entityList.size(); i++) {
                entityList.get(i).draw(g2);
            }
            // EMPTY ENTITY LIST
            entityList.clear();

            // UI
            ui.draw(g2);

        }

        g2.dispose();
    }
}