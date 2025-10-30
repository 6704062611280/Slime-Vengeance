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
        speed = 1;
        maxLife = 4;
        life = maxLife;

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

    }

    public void update() {
        setAction(); // อัปเดตทิศทาง

        // move according to direction
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

        // ตรวจสอบ collision กับ tile หรือ player (ถ้ามี)
        gp.cChecker.checkTile(this);

        // อัปเดต animation frame ถ้ามี

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
