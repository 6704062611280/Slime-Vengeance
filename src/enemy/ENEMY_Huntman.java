package enemy;

import java.awt.Rectangle;
import java.util.Random;

import entity.Entity;
import main.GamePanel;

public class ENEMY_Huntman extends Entity {

    GamePanel gp;

    int chaseRange = 10; // หน่วยเป็น tile

    public ENEMY_Huntman(GamePanel gp) {

        super(gp);
        this.gp = gp;

        name = "Huntman";
        speed = 1; // จะถูกปรับตามเวลาที่เหลือใน update()
        maxLife = 8;
        life = maxLife;
        attack = 1;   // damage to player on contact

        solidArea = new Rectangle();
        solidArea.x = 3;
        solidArea.y = 18;
        solidArea.width = 42;
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();

        // 🔹 ตั้งตำแหน่ง worldX, worldY แบบสุ่มใกล้ผู้เล่น
        int[] pos = gp.getRandomPositionNearPlayer(5); // 5 tile
        worldX = pos[0];
        worldY = pos[1];

        // แปลง chaseRange เป็นพิกเซล
        chaseRange = chaseRange * gp.tileSize;

    // set shorter death duration for this enemy
    this.deathDuration = 40;

    }

    public void update() {
        // Adjust speed based on remaining time
        int remainingSeconds = gp.levelDurationSeconds - (int)(gp.elapsedFrames / gp.FPS);
        double timeRatio = (double)remainingSeconds / gp.levelDurationSeconds;
        double maxSpeed = gp.player.speed * 0.75; // ไม่เกิน 75% ของความเร็วผู้เล่น
        double minSpeed = 1.0; // ความเร็วต่ำสุด
        speed = (int)Math.round(minSpeed + (maxSpeed - minSpeed) * (1.0 - timeRatio));

        // If dying, only update death animation
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

        // Normal update loop
        setAction(); // อัปเดตทิศทาง

        // Check collisions before moving
        collisionOn = false;
        gp.cChecker.checkTile(this);
        gp.cChecker.checkEntity(this, gp.enemy);
        gp.cChecker.checkPlayer(this);
        
        // Move if no collision
        if (!collisionOn) {
            switch (direction) {
                case "up": worldY -= speed; break;
                case "down": worldY += speed; break;
                case "left": worldX -= speed; break;
                case "right": worldX += speed; break;
            }
        }

        // Sprite animation
        spriteCounter++;
        if (spriteCounter > 12) {
            if (spriteNum == 1) {
                spriteNum = 2;
            } else if (spriteNum == 2) {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }

        // Check if dead
        if (life <= 0 && !dying) {
            dying = true;
            deathCounter = 0;
        }
    }

    public void getImage() {
        down1 = setup("/enemy/huntman_down1");
        down2 = setup("/enemy/huntman_down2");
        up1 = setup("/enemy/huntman_up1");
        up2 = setup("/enemy/huntman_up2");
        left1 = setup("/enemy/huntman_left1");
        left2 = setup("/enemy/huntman_left2");
        right1 = setup("/enemy/huntman_right1");
        right2 = setup("/enemy/huntman_right2");
    }

    public void setAction() {
        actionLockCounter++;

        // ตรวจสอบระยะห่างกับผู้เล่น
        double distance = Math.sqrt(Math.pow(worldX - gp.player.worldX, 2)
                + Math.pow(worldY - gp.player.worldY, 2));

        if (distance < chaseRange) {
            // ไล่ผู้เล่น
            if (worldX < gp.player.worldX) {
                direction = "right";
            } else if (worldX > gp.player.worldX) {
                direction = "left";
            }

            if (worldY < gp.player.worldY) {
                direction = "down";
            } else if (worldY > gp.player.worldY) {
                direction = "up";
            }

        } else {
            // เดินสุ่มเหมือนเดิม ทุก 120 frame
            if (actionLockCounter >= 120) {
                Random random = new Random();
                int i = random.nextInt(100) + 1;

                if (i <= 25) {
                    direction = "up";
                } else if (i <= 50) {
                    direction = "down";
                } else if (i <= 75) {
                    direction = "left";
                } else {
                    direction = "right";
                }

                actionLockCounter = 0;
            }
        }
    }

}
