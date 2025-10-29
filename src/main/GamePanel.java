package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

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

    //WORLD SETTING
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    // FPS
    int FPS = 60;

    // SYSTEM
    TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler(this);
    Thread gameThread;
    public CollisionChecker cChecker = new CollisionChecker(this);
    public UI ui = new UI(this);


    // ENTITY AND OBJECT
    public Player player = new Player(this,keyH);

    // GAME STATE
    public int gameState;
    public final int playState = 1;
    public final int pauseState = 2;

    

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // set size JPanel
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void setupGame(){
        gameState = playState;
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
                remainingTime = remainingTime/1000000;

                if(remainingTime <0){
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

        if(gameState == playState){
            player.update();
        }
        if(gameState == pauseState){
            //nothing
        }
        
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        //TILE
        tileM.draw(g2);

        // PLAYER
        player.draw(g2);

        //UI
        ui.draw(g2);

        g2.dispose();
    }
}