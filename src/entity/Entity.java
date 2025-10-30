package entity;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.UtilityTool;

public class Entity {

    GamePanel gp;

    public int worldX,worldY;
    
    public BufferedImage up1,up2,down1,down2,left1,left2,right1,right2;
    public String direction = "down";
    public int actionLockCounter;
    public int spriteCounter = 0;
    public boolean invincible = false;
    public int invincibleCounter = 0;
    public int spriteNum = 1;

    // death animation / removal
    public boolean dying = false;
    public int deathCounter = 0;
    public int deathDuration = 60; // frames to wait before removal (1 second at 60 FPS)
    public boolean readyToRemove = false;

    
    public Rectangle solidArea;
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;
    public BufferedImage image,image2,image3;
    public boolean collision = false;


    // CHARACTER STATUS
    public String name;
    public int speed;
    public int maxLife;
    public int life;
    public int attack;
    public Boolean alive = true;


    public GamePanel projectile;

    public Entity(GamePanel gp){
        this.gp = gp;
    }

    // UPDATE
    public void setAction() {}
    public void update(){

        // If currently in dying state, count down and mark readyToRemove when finished
        if (dying) {
            deathCounter++;
            // simple flicker effect
            if (deathCounter % 10 < 5) {
                spriteNum = 1;
            } else {
                spriteNum = 2;
            }
            if (deathCounter > deathDuration) {
                readyToRemove = true;
            }
            return; // skip other updates while dying
        }

        setAction();

        collisionOn = false;
        gp.cChecker.checkTile(this);
        gp.cChecker.checkObject(this, false);
        gp.cChecker.checkPlayer(this);
        gp.cChecker.checkEntity(this, gp.enemy);
        if (collisionOn == false) {
                switch (direction) {
                    case "up":
                        worldY -= speed;
                        break;
                    case "down":
                        worldY += speed;
                        break;
                    case "left":
                        worldX -= speed;
                        break;
                    case "right":
                        worldX += speed;
                        break;

                }
            }
            
            spriteCounter++;
            if (spriteCounter > 12) {
                if (spriteNum == 1) {
                    spriteNum = 2;
                } else if (spriteNum == 2) {
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }

    }

    public BufferedImage setup(String imageName){
        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/res"+imageName+".png"));
            image = uTool.scaledImage(image, gp.tileSize, gp.tileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void draw(Graphics2D g2) {

        BufferedImage image = null;
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if(worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
           worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
           worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
           worldY - gp.tileSize < gp.player.worldY + gp.player.screenY ){

            switch (direction) {
            case "up":
                if (spriteNum == 1) {
                    image = up1;
                }
                if (spriteNum == 2) {
                    image = up2;
                }

                break;
            case "down":
                if (spriteNum == 1) {
                    image = down1;
                }
                if (spriteNum == 2) {
                    image = down2;
                }
                break;
            case "left":
                if (spriteNum == 1) {
                    image = left1;
                }
                if (spriteNum == 2) {
                    image = left2;
                }

                break;
            case "right":
                if (spriteNum == 1) {
                    image = right1;
                }
                if (spriteNum == 2) {
                    image = right2;
                }

                break;
            }

            g2.drawImage(image,screenX,screenY,gp.tileSize,gp.tileSize,null);

            // Fallback drawing when image is missing for simple items
            if (image == null && name != null) {
                // draw simple shapes for common items so drops are visible
                if (name.equalsIgnoreCase("coin")) {
                    int size = gp.tileSize/2;
                    int cx = screenX + gp.tileSize/2 - size/2;
                    int cy = screenY + gp.tileSize/2 - size/2;
                    g2.setColor(new java.awt.Color(212,175,55)); // gold
                    g2.fillOval(cx, cy, size, size);
                    g2.setColor(java.awt.Color.BLACK);
                    g2.drawOval(cx, cy, size, size);
                } else if (name.equalsIgnoreCase("soul")) {
                    int size = gp.tileSize/2;
                    int cx = screenX + gp.tileSize/2 - size/2;
                    int cy = screenY + gp.tileSize/2 - size/2;
                    g2.setColor(new java.awt.Color(153,50,204)); // purple
                    g2.fillOval(cx, cy, size, size);
                    g2.setColor(java.awt.Color.WHITE);
                    g2.drawOval(cx, cy, size, size);
                } else {
                    // generic placeholder for unknowns
                    int size = gp.tileSize/2;
                    int cx = screenX + gp.tileSize/2 - size/2;
                    int cy = screenY + gp.tileSize/2 - size/2;
                    g2.setColor(java.awt.Color.GRAY);
                    g2.fillRect(cx, cy, size, size);
                }
            }

            // Draw small health bar under non-player entities that have life
            if (this != gp.player && this.maxLife > 0) {
                int barWidth = gp.tileSize - 8;
                int barHeight = 6;
                int barX = screenX + 4;
                int barY = screenY + gp.tileSize + 4;

                // background
                g2.setColor(new java.awt.Color(0,0,0,150));
                g2.fillRect(barX - 1, barY - 1, barWidth + 2, barHeight + 2);

                // empty
                g2.setColor(java.awt.Color.RED);
                g2.fillRect(barX, barY, barWidth, barHeight);

                // current
                double ratio = (double)this.life / (double)this.maxLife;
                int currentWidth = (int)(barWidth * Math.max(0, Math.min(1.0, ratio)));
                g2.setColor(java.awt.Color.GREEN);
                g2.fillRect(barX, barY, currentWidth, barHeight);
            }
            
        }



    }

    
}
