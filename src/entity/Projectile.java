package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.GamePanel;
import object.OBJ_Fireball;

public class Projectile extends Entity {

    public Projectile(GamePanel gp) {
        super(gp);

        // กำหนด solidArea
        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 8;
        solidArea.width = 16;
        solidArea.height = 16;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        speed = 5;
        maxLife = 80;
        life = maxLife;
        attack = 2;
    }

    @Override
    public void update() {
        // เคลื่อนที่ projectile ตาม direction
        switch(direction) {
            case "up": worldY -= speed; break;
            case "down": worldY += speed; break;
            case "left": worldX -= speed; break;
            case "right": worldX += speed; break;
        }

        // ตรวจ collision กับศัตรู
        int enemyIndex = gp.cChecker.checkEntity(this, gp.enemy);
        if(enemyIndex != 999) {
            if (gp.enemy[enemyIndex] != null) {
                gp.enemy[enemyIndex].life -= attack;
                life = 0; // projectile หายหลังโดนศัตรู
            }
        }

        // อัพเดท sprite animation
        spriteCounter++;
        if (spriteCounter > 12) {
            if (spriteNum == 1) {
                spriteNum = 2;
            } else if (spriteNum == 2) {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }

        // ลด life ของ projectile ทุกเฟรม
        life--;
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if(this instanceof OBJ_Fireball) {
            OBJ_Fireball fireball = (OBJ_Fireball)this;
            image = fireball.getCurrentImage(spriteNum, direction);
        }

        if(image != null) {
            g2.drawImage(image, screenX, screenY, null);
        }
    }

    public boolean isAlive() {
        return life > 0;
    }
}
