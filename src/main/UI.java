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
        }
        // PAUSE STATE
        if (gp.gameState == gp.pauseState) {
            drawPlayerLife();
            drawPauseScreen();
        }

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
            bg = ImageIO.read(getClass().getResourceAsStream("/res/Title_screen.png"));
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
            st = ImageIO.read(getClass().getResourceAsStream("/res/Setting_icon.png"));
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
                gp.gameState = gp.playState; // ✅ เริ่มเกม

            }

            // UPGRADE
            int upgX = getXforCenteredText("UPGRADE") - 46;
            int upgY = startY + (int) (gp.tileSize * 1.5);
            if (mouseX >= upgX && mouseX <= upgX + 250 &&
                    mouseY >= upgY - 40 && mouseY <= upgY + 20) {
                commandNum = 1;
                System.out.println("เปิดหน้าอัปเกรด");
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
