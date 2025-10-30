package entity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.KeyHandler;
import main.UtilityTool;
import object.OBJ_Fireball;

public class Player extends Entity {

    public OBJ_Fireball projectile;

    KeyHandler keyH;

    public final int screenX;
    public final int screenY;

    int shootCounter = 0;

    GamePanel gp;
    // progression
    public int coins = 0;
    public int exp = 0;
    public int level = 1;
    public int expToNext = 10;
    public int shootCooldown = 60; // frames between shots

    // Permanent (default) stats that persist across runs and are modified by upgrades
    public int baseMaxLife;
    public int baseAttack;
    public int baseSpeed;
    public int baseShootCooldown;

    public Player(GamePanel gp, KeyHandler keyH) {

        super(gp);
        this.gp = gp;
        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 32;
        solidArea.height = 32;
        // solidArea = new Rectangle(8, 16, 32, 32);

        // initialize permanent base stats and progression defaults
        initializeBaseStats();
        // progression defaults (kept across runs if modified)
        this.coins = 0;
        this.exp = 0;
        this.level = 1;
        this.expToNext = 10;

        // set current run values from base stats
        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        direction = "down";

        // PLAYER STATUS - use permanent base stats for current run values
        maxLife = baseMaxLife;
        life = maxLife;
        speed = baseSpeed;
        projectile = new OBJ_Fireball(gp);
        attack = baseAttack;
        shootCooldown = baseShootCooldown;
    }

    // Initialize the permanent base stats. Called once on construction.
    private void initializeBaseStats() {
        baseMaxLife = 6;
        baseAttack = 1;
        baseSpeed = 4;
        baseShootCooldown = 60;
    }

    public void getPlayerImage() {

        up1 = setup("Slime_up1");
        up2 = setup("Slime_up2");
        down1 = setup("Slime_down1");
        down2 = setup("Slime_down2");
        left1 = setup("Slime_left1");
        left2 = setup("Slime_left2");
        right1 = setup("Slime_right1");
        right2 = setup("Slime_right2");
    }

    public BufferedImage setup(String imageName) {
        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/res/player/" + imageName + ".png"));
            image = uTool.scaledImage(image, gp.tileSize, gp.tileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void update() {

    // 1. ตรวจสอบปุ่มกดเพื่อเคลื่อนที่
    if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
        if (keyH.upPressed) {
            direction = "up";
        } else if (keyH.downPressed) {
            direction = "down";
        } else if (keyH.leftPressed) {
            direction = "left";
        } else if (keyH.rightPressed) {
            direction = "right";
        }

        collisionOn = false;
        gp.cChecker.checkTile(this);
        int objIndex = gp.cChecker.checkObject(this, true);
        pickUpObject(objIndex);

        // ถ้าไม่มี collision ให้เคลื่อนที่
        if (!collisionOn) {
            switch (direction) {
                case "up": worldY -= speed; break;
                case "down": worldY += speed; break;
                case "left": worldX -= speed; break;
                case "right": worldX += speed; break;
            }
        }

        // อัปเดต sprite
        spriteCounter++;
        if (spriteCounter > 12) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    // 2. ยิง Fireball **ทุกเฟรม**
    shootFireballAuto();

    // 3. ตรวจสอบการชนศัตรู **ทุกเฟรม**
    int enemyIndex = gp.cChecker.checkEntity(this, gp.enemy);
    contactEnemy(enemyIndex);

    // 4. อัปเดต invincible
    if (invincible) {
        invincibleCounter++;
        if (invincibleCounter > 60) {
            invincible = false;
            invincibleCounter = 0;
        }
    }
}


    public void pickUpObject(int i) {

        if (i != 999) {
            if (i >= 1000) {
                int idx = i - 1000;
                if (idx >= 0 && idx < gp.items.size()) {
                    Entity obj = gp.items.get(idx);
                    if (obj != null) {
                        String objectName = obj.name;
                        switch (objectName) {
                            case "Soul":
                                exp += 5;
                                checkLevelUp();
                                gp.items.remove(idx);
                                break;
                            case "Coin":
                                coins += 1;
                                gp.items.remove(idx);
                                break;
                            default:
                                gp.items.remove(idx);
                                break;
                        }
                    }
                }
            } else {
                if (i >= 0 && i < gp.obj.length && gp.obj[i] != null) {
                    String objectName = gp.obj[i].name;
                    switch (objectName) {
                        case "Soul":
                            // gain exp
                            int soulExp = 5;
                            exp += soulExp;
                            checkLevelUp();
                            gp.obj[i] = null;
                            break;
                        case "Coin":
                            // coin object may carry a value, but default +1
                            coins += 1;
                            gp.obj[i] = null;
                            break;
                        default:
                            // other objects
                            gp.obj[i] = null;
                            break;
                    }
                }
            }
        }
    }

    private void checkLevelUp() {
        while (exp >= expToNext) {
            exp -= expToNext;
            level++;
            // increase stats on level up
            attack += 1;
            maxLife += 2;
            life = Math.min(maxLife, life +  (maxLife/2));
            expToNext = 10 * level;
        }
    }

    public void contactEnemy(int i) {
        if (i != 999) {

            if (invincible == false) {
                life -= 1;
                invincible = true;
            }
        }

    }

    public void shootFireballAuto() {
        shootCounter++;
        if (shootCounter > shootCooldown) { // ยิงตาม cooldown (เฟรม)
            // หาศัตรูที่ใกล้ที่สุด
            Entity nearestEnemy = null;
            double shortestDistance = Double.MAX_VALUE;

            for (Entity enemy : gp.enemy) {
                if (enemy != null) {
                    double distance = Math.sqrt(
                        Math.pow(enemy.worldX - worldX, 2) + 
                        Math.pow(enemy.worldY - worldY, 2)
                    );
                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                        nearestEnemy = enemy;
                    }
                }
            }

            // ถ้าเจอศัตรู ให้ยิง Fireball ไปที่ศัตรู
            if (nearestEnemy != null) {
                OBJ_Fireball fireball = new OBJ_Fireball(gp);
                fireball.worldX = worldX + gp.tileSize / 2 - gp.tileSize / 4;
                fireball.worldY = worldY + gp.tileSize / 2 - gp.tileSize / 4;

                // Make the projectile damage scale with the player's current attack (upgrades/level-ups)
                fireball.attack = this.attack;

                // คำนวณทิศทางที่จะยิงไปหาศัตรู
                double dx = nearestEnemy.worldX - worldX;
                double dy = nearestEnemy.worldY - worldY;

                // กำหนดทิศทางตามแกนหลักที่ใกล้ที่สุด
                if (Math.abs(dx) > Math.abs(dy)) {
                    fireball.direction = (dx > 0) ? "right" : "left";
                } else {
                    fireball.direction = (dy > 0) ? "down" : "up";
                }

                gp.projectiles.add(fireball);
                shootCounter = 0;
            }
        }
    }

    public void draw(Graphics2D g2) {
        // g2.setColor(Color.white);
        // g2.fillRect(x, y, gp.tileSize, gp.tileSize);
        BufferedImage image = null;
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

        if (invincible == true) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        }

        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);

        // RESET alpha
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // DEBUG
        // g2.setFont(new Font("Arial",Font.PLAIN,26));
        // g2.setColor(Color.white);
        // g2.drawString("Invicible:"+invincibleCounter, 10, 400);
    }

}
