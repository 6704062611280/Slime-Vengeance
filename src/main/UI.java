package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

import entity.Entity;
import object.OBJ_Heart;


public class UI {
    GamePanel gp;
    Graphics2D g2;
    Font arial_40, arial_80B;
    BufferedImage heart_full, heart_half, heart_blank;
    public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0;
    public boolean gameFinished = false;
    public int commandNum = 0;

    double playTime;
    DecimalFormat dFormat = new DecimalFormat("#0.00");

    public UI(GamePanel gp) {
        this.gp = gp;
        arial_40 = new Font("Arial", Font.PLAIN, 40);
        arial_80B = new Font("Arial", Font.BOLD, 80);

        //  CREATE HUD OBJECT
        Entity heart = new OBJ_Heart(gp);
        heart_full = heart.image;
        heart_half = heart.image2;
        heart_blank = heart.image3;
    }

    public void showMessage(String text) {
        message = text;
        messageOn = true;
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setFont(arial_40);
        g2.setColor(Color.white);

        // TITLE STATE
        if (gp.gameState == gp.titleState) {
            drawTitleScreen();
        }

        // PLAY STATE
        if (gp.gameState == gp.playState) {
            drawPlayerLife();
            drawPlayerStats();
        }
        // GAME OVER
        if (gp.gameState == gp.gameOverState) {
            drawGameOver();
        }
        // UPGRADE STATE
        if (gp.gameState == gp.upgradeState) {
            drawUpgradeScreen();
        }
        // PAUSE STATE
        if (gp.gameState == gp.pauseState) {
            drawPlayerLife();
            drawPauseScreen();
        }

    }

    public void drawPlayerStats() {
        g2.setFont(arial_40.deriveFont(Font.PLAIN, 18f));
        g2.setColor(Color.WHITE);
        // Coins
        String coinText = "Coins: " + gp.player.coins;
        g2.drawString(coinText, gp.tileSize/2, gp.tileSize/2 + gp.tileSize + 20);

        // Level and EXP
        String lvlText = "Lv " + gp.player.level + "  EXP: " + gp.player.exp + "/" + gp.player.expToNext;
        g2.drawString(lvlText, gp.tileSize/2, gp.tileSize/2 + gp.tileSize + 40);

        // Time remaining (top center)
        int elapsedSec = (int)(gp.elapsedFrames / gp.FPS);
        int remaining = Math.max(0, gp.levelDurationSeconds - elapsedSec);
        int mins = remaining / 60;
        int secs = remaining % 60;
        String timeText = String.format("%02d:%02d", mins, secs);
        g2.setFont(arial_40.deriveFont(Font.BOLD, 20f));
        g2.setColor(Color.YELLOW);
        g2.drawString(timeText, getXforCenteredText(timeText), gp.tileSize/2);
    }

    public void drawPlayerLife(){

        

        int x = gp.tileSize/2;
        int y = gp.tileSize/2;
        int i = 0;
        
        // DRAW MAX HEART
        while (i < gp.player.maxLife/2) {
            g2.drawImage(heart_blank, x, y, null);
            i++;
            x += gp.tileSize;
        }

        // RESET
        x = gp.tileSize/2;
        y = gp.tileSize/2;
        i = 0;

        // DRAW CURRENT LIFE
        while(i < gp.player.life){
            g2.drawImage(heart_half,x, y,null);
            i++;
            if(i < gp.player.life){
                g2.drawImage(heart_full, x, y, null);
            }
            i++;
            x += gp.tileSize;
        }


    }
    

    public void drawTitleScreen() {
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 80F));
        String text = "Slime Vengeance";
        int x = getXforCenteredText(text);
        int y = gp.tileSize * 3;

        BufferedImage bg = null;
        try {
            bg = ImageIO.read(getClass().getResourceAsStream("/Title_screen.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // BACKGROUND IMAGE
        g2.drawImage(bg, 0, 0, gp.screenWidth, gp.screenHeight, null);
        g2.setColor(Color.white);

        // =====================================MANU========================================
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 30f));

        // =================================START=GAME======================================
        text = "START GAME";
        x = getXforCenteredText(text);
        y += gp.tileSize * 3.5;
        // ① วาดเงา
        g2.setColor(new Color(0, 0, 0, 100)); // สีดำโปร่งใส
        g2.fillRoundRect(x - 19, y - 45, 250, 60, 20, 20);
        // box
        g2.setColor(Color.decode("#e0e0e0"));
        g2.fillRoundRect(x - 24, y - 40, 250, 60, 20, 20);
        // STORK
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(x - 24, y - 40, 250, 60, 20, 20);
        // TEXT
        g2.drawString(text, x, y);

        

        // =================================UPGRADE======================================
        text = "UPGRADE";
        x = getXforCenteredText(text);
        y += gp.tileSize * 1.5;
        // ① วาดเงา
        g2.setColor(new Color(0, 0, 0, 100)); // สีดำโปร่งใส
        g2.fillRoundRect(x - 41, y - 45, 250, 60, 20, 20);
        // box
        g2.setColor(Color.decode("#e0e0e0"));
        g2.fillRoundRect(x - 46, y - 40, 250, 60, 20, 20);
        // STORK
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(x - 46, y - 40, 250, 60, 20, 20);
        // TEXT
        g2.drawString(text, x, y);

       

        // =================================QUITE======================================
        text = "Quite";
        x = getXforCenteredText(text);
        y += gp.tileSize * 1.5;
        // ① วาดเงา
        g2.setColor(new Color(0, 0, 0, 100)); // สีดำโปร่งใส
        g2.fillRoundRect(x - 78, y - 45, 250, 60, 20, 20);
        // box
        g2.setColor(Color.decode("#e0e0e0"));
        g2.fillRoundRect(x - 83, y - 40, 250, 60, 20, 20);
        // STORK
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(x - 83, y - 40, 250, 60, 20, 20);
        // TEXT
        g2.drawString(text, x, y);

    

        // =================================SETTING======================================
        BufferedImage st = null;
        try {
            st = ImageIO.read(getClass().getResourceAsStream("/Setting_icon.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        int iconSize = 50;
        g2.setColor(Color.decode("#e0e0e0")); // กำหนดสี

        g2.fillOval(gp.screenWidth - iconSize - 30, gp.screenHeight - iconSize - 30, iconSize + 20, iconSize + 20);

        g2.drawImage(st, gp.screenWidth - iconSize - 20, gp.screenHeight - iconSize - 20, iconSize, iconSize, null);

    }

    public void checkClick(int mouseX, int mouseY) {
        if (gp.gameState == gp.titleState) {
            // START GAME
            int startX = getXforCenteredText("START GAME") - 24;
            int startY = gp.tileSize * 3 + gp.tileSize * 3; // y ตอนวาด START
            if (mouseX >= startX && mouseX <= startX + 250 &&
                    mouseY >= startY - 40 && mouseY <= startY + 20) {
                commandNum = 0;
                gp.startNextLevel(); // start the level and reset timers

            }

            // UPGRADE
            int upgX = getXforCenteredText("UPGRADE") - 46;
            int upgY = startY + (int) (gp.tileSize * 1.5);
            if (mouseX >= upgX && mouseX <= upgX + 250 &&
                    mouseY >= upgY - 40 && mouseY <= upgY + 20) {
                commandNum = 1;
                gp.gameState = gp.upgradeState; // open upgrade screen from title
            }

            // QUIT
            int quitX = getXforCenteredText("Quite") - 83;
            int quitY = upgY + (int) (gp.tileSize * 1.5);
            if (mouseX >= quitX && mouseX <= quitX + 250 &&
                    mouseY >= quitY - 40 && mouseY <= quitY + 20) {
                commandNum = 2;
                System.exit(0); // ✅ ออกจากเกม
            }

            // SETTING ICON (มุมขวาล่าง)
            int iconSize = 50;
            int iconX = gp.screenWidth - iconSize - 30;
            int iconY = gp.screenHeight - iconSize - 30;
            if (mouseX >= iconX && mouseX <= iconX + iconSize + 20 &&
                    mouseY >= iconY && mouseY <= iconY + iconSize + 20) {
                System.out.println("เปิดหน้าตั้งค่า");
            }
        }
        // GAME OVER screen clicks
        if (gp.gameState == gp.gameOverState) {
            int btnW = 200;
            int btnH = 60;
            int gap = 20;
            int startY = gp.screenHeight - gp.tileSize*3;  // match drawGameOver position

            // Next Level button (left)
            int leftX = gp.screenWidth/2 - btnW - gap/2;
            if (mouseX >= leftX && mouseX <= leftX+btnW && 
                mouseY >= startY && mouseY <= startY+btnH) {
                gp.startNextLevel();
            }

            // Main Menu button (middle)
            int middleX = gp.screenWidth/2 - btnW/2;
            if (mouseX >= middleX && mouseX <= middleX+btnW && 
                mouseY >= startY+btnH+gap && mouseY <= startY+btnH+gap+btnH) {
                gp.resetGame();
                gp.gameState = gp.titleState;
            }

            // Upgrade button (right)
            int rightX = gp.screenWidth/2 + gap/2;
            if (mouseX >= rightX && mouseX <= rightX+btnW && 
                mouseY >= startY && mouseY <= startY+btnH) {
                gp.gameState = gp.upgradeState;
            }
        }

        // UPGRADE screen clicks
        if (gp.gameState == gp.upgradeState) {
            int startX = gp.screenWidth/2 - 220;
            int startY = gp.screenHeight/2 - 80;
            int w = 150; int h = 50; int gap = 20;
            // MaxHP
            if (mouseX >= startX && mouseX <= startX+w && mouseY >= startY && mouseY <= startY+h) {
                if (gp.player.coins >= 5) {
                    gp.player.coins -= 5;
                    gp.player.baseMaxLife += 2; // increase permanent default
                    gp.player.maxLife = gp.player.baseMaxLife; // immediate reflect
                    gp.player.life = gp.player.baseMaxLife;
                }
            }
            // Speed
            if (mouseX >= startX && mouseX <= startX+w && mouseY >= startY + (h+gap) && mouseY <= startY + (h+gap) + h) {
                if (gp.player.coins >= 10) {
                    gp.player.coins -= 10;
                    gp.player.baseSpeed += 1;
                    gp.player.speed = gp.player.baseSpeed;
                }
            }
            // Attack
            if (mouseX >= startX && mouseX <= startX+w && mouseY >= startY + 2*(h+gap) && mouseY <= startY + 2*(h+gap) + h) {
                if (gp.player.coins >= 8) {
                    gp.player.coins -= 8;
                    gp.player.baseAttack += 1;
                    gp.player.attack = gp.player.baseAttack;
                }
            }
            // Attack Speed
            if (mouseX >= startX && mouseX <= startX+w && mouseY >= startY + 3*(h+gap) && mouseY <= startY + 3*(h+gap) + h) {
                if (gp.player.coins >= 12) {
                    gp.player.coins -= 12;
                    gp.player.baseShootCooldown = Math.max(10, gp.player.baseShootCooldown - 5);
                    gp.player.shootCooldown = gp.player.baseShootCooldown;
                }
            }
            // Back button to go to next level
            int bx = gp.screenWidth/2 + 100; int by = gp.screenHeight/2 + 120; int bw = 150; int bh=50;
            if (mouseX >= bx && mouseX <= bx+bw && mouseY >= by && mouseY <= by+bh) {
                gp.startNextLevel();
            }
        }
    }

    public void drawGameOver() {
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 50F));
        String text = "GAME OVER";
        int x = getXforCenteredText(text);
        int y = gp.screenHeight/2 - 120;
        g2.drawString(text, x, y);

        // Draw stats (time and coins)
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));
        int secondsPlayed = (int)(gp.finalTimeFrames / gp.FPS);
        int pm = secondsPlayed / 60;
        int ps = secondsPlayed % 60;
        String timePlayed = String.format("Time: %02d:%02d", pm, ps);
        g2.drawString(timePlayed, getXforCenteredText(timePlayed), y+50);

        int coinsEarned = gp.finalCoins - gp.coinsAtLevelStart;
        String coinText = String.format("Coins: %d   (+%d)", gp.finalCoins, coinsEarned);
        g2.drawString(coinText, getXforCenteredText(coinText), y+90);

        String levelText = "Level: " + gp.player.level;
        g2.drawString(levelText, getXforCenteredText(levelText), y+130);

        // Buttons at bottom
        int btnW = 200; int btnH = 60;
        int gap = 20;
        int startY = gp.screenHeight - gp.tileSize*3;  // start buttons 3 tiles from bottom

        // Next Level button (left)
        int leftX = gp.screenWidth/2 - btnW - gap/2;
        g2.setColor(Color.decode("#e0e0e0"));
        g2.fillRoundRect(leftX, startY, btnW, btnH, 12, 12);
        g2.setColor(Color.BLACK);
        g2.drawString("Next Level", leftX + 35, startY + 40);

        // Main Menu button (middle)
        int middleX = gp.screenWidth/2 - btnW/2;
        g2.setColor(Color.decode("#e0e0e0"));
        g2.fillRoundRect(middleX, startY + btnH + gap, btnW, btnH, 12, 12);
        g2.setColor(Color.BLACK);
        g2.drawString("Main Menu", middleX + 35, startY + btnH + gap + 40);

        // Upgrade button (right)
        int rightX = gp.screenWidth/2 + gap/2;
        g2.setColor(Color.decode("#e0e0e0"));
        g2.fillRoundRect(rightX, startY, btnW, btnH, 12, 12);
        g2.setColor(Color.BLACK);
        g2.drawString("Upgrade", rightX + 50, startY + 40);
    }

    public void drawUpgradeScreen() {
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 36F));
        String text = "Upgrade Shop";
        g2.drawString(text, getXforCenteredText(text), gp.tileSize);

        int startX = gp.screenWidth/2 - 280;  // moved left to make room for stats
        int startY = gp.screenHeight/2 - 80;
        int w = 150; int h = 50; int gap = 20;

        // Show current coins
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
        g2.drawString("Coins: " + gp.player.coins, startX, startY - 40);

        // Current Stats panel on right
        int statsX = startX + w + 80;
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20F));
    g2.drawString("Current Stats:", statsX, startY - 40);
    g2.drawString("Max HP: " + gp.player.baseMaxLife, statsX, startY + 30);
    g2.drawString("Speed: " + gp.player.baseSpeed, statsX, startY + (h+gap) + 30);
    g2.drawString("Attack: " + gp.player.baseAttack, statsX, startY + 2*(h+gap) + 30);
    g2.drawString("Attack Speed: " + (60 - gp.player.baseShootCooldown), statsX, startY + 3*(h+gap) + 30);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20F));
        
        // Draw upgrade buttons with costs
        // MaxHP
        boolean canAffordHP = gp.player.coins >= 5;
        g2.setColor(canAffordHP ? Color.decode("#e0e0e0") : Color.decode("#a0a0a0"));
        g2.fillRect(startX, startY, w, h);
        g2.setColor(Color.BLACK);
        g2.drawString("MaxHP +2 (5c)", startX+10, startY+30);

        // Speed
        boolean canAffordSpeed = gp.player.coins >= 10;
        g2.setColor(canAffordSpeed ? Color.decode("#e0e0e0") : Color.decode("#a0a0a0"));
        g2.fillRect(startX, startY + (h+gap), w, h);
        g2.setColor(Color.BLACK);
        g2.drawString("Speed +1 (10c)", startX+10, startY + (h+gap) +30);
    // Attack
    boolean canAffordAttack = gp.player.coins >= 8;
    g2.setColor(canAffordAttack ? Color.decode("#e0e0e0") : Color.decode("#a0a0a0"));
    g2.fillRect(startX, startY + 2*(h+gap), w, h);
    g2.setColor(Color.BLACK);
    g2.drawString("Attack +1 (8c)", startX+10, startY + 2*(h+gap) +30);

    // Attack Speed
    boolean canAffordAtkSpd = gp.player.coins >= 12;
    g2.setColor(canAffordAtkSpd ? Color.decode("#e0e0e0") : Color.decode("#a0a0a0"));
    g2.fillRect(startX, startY + 3*(h+gap), w, h);
    g2.setColor(Color.BLACK);
    g2.drawString("Atk Speed -5f (12c)", startX+10, startY + 3*(h+gap) +30);

        // Back/Start Next Level button
        int bx = gp.screenWidth/2 + 100; 
        int by = gp.screenHeight/2 + 120; 
        int bw = 150; int bh=50;
        g2.setColor(Color.decode("#e0e0e0"));
        g2.fillRect(bx, by, bw, bh);
        g2.setColor(Color.BLACK);
        g2.drawString("Start Next", bx+20, by+30);
    }

    public void drawPauseScreen() {
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80F));
        String text = "PAUSED";
        int x = getXforCenteredText(text);
        int y = gp.screenHeight / 2;

        g2.drawString(text, x, y);
    }

    public int getXforCenteredText(String text) {
        int lenght = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth / 2 - lenght / 2;
        return x;
    }

}
