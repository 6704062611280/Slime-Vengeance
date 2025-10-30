package object;

import entity.Projectile;
import main.GamePanel;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import main.UtilityTool;

public class OBJ_Fireball extends Projectile {

    GamePanel gp;

    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    UtilityTool uTool = new UtilityTool();

    public OBJ_Fireball(GamePanel gp) {
        super(gp);
        this.gp = gp;

        name = "Fireball";
        speed = 5;
        maxLife = 80;
        life = maxLife;
        attack = 2;

        getImage();
    }

    public void getImage() {
        try {
            up1 = uTool.scaledImage(ImageIO.read(getClass().getResourceAsStream("/res/projectile/fireball_up1.png")), gp.tileSize/2, gp.tileSize/2);
            up2 = uTool.scaledImage(ImageIO.read(getClass().getResourceAsStream("/res/projectile/fireball_up2.png")), gp.tileSize/2, gp.tileSize/2);
            down1 = uTool.scaledImage(ImageIO.read(getClass().getResourceAsStream("/res/projectile/fireball_down1.png")), gp.tileSize/2, gp.tileSize/2);
            down2 = uTool.scaledImage(ImageIO.read(getClass().getResourceAsStream("/res/projectile/fireball_down2.png")), gp.tileSize/2, gp.tileSize/2);
            left1 = uTool.scaledImage(ImageIO.read(getClass().getResourceAsStream("/res/projectile/fireball_left1.png")), gp.tileSize/2, gp.tileSize/2);
            left2 = uTool.scaledImage(ImageIO.read(getClass().getResourceAsStream("/res/projectile/fireball_left2.png")), gp.tileSize/2, gp.tileSize/2);
            right1 = uTool.scaledImage(ImageIO.read(getClass().getResourceAsStream("/res/projectile/fireball_right1.png")), gp.tileSize/2, gp.tileSize/2);
            right2 = uTool.scaledImage(ImageIO.read(getClass().getResourceAsStream("/res/projectile/fireball_right2.png")), gp.tileSize/2, gp.tileSize/2);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage getCurrentImage(int spriteNum, String direction) {
        switch(direction) {
            case "up": return (spriteNum == 1) ? up1 : up2;
            case "down": return (spriteNum == 1) ? down1 : down2;
            case "left": return (spriteNum == 1) ? left1 : left2;
            case "right": return (spriteNum == 1) ? right1 : right2;
            default: return down1;
        }
    }
}
