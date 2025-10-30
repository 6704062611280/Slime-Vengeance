package object;

import java.io.IOException;

import javax.imageio.ImageIO;

import entity.Entity;
import main.GamePanel;
import main.UtilityTool;

public class OBJ_Coin extends Entity{

    public int value = 1;
    UtilityTool uTool = new UtilityTool();

    public OBJ_Coin(GamePanel gp){
        super(gp);
        name = "Coin";
        try {
            java.awt.image.BufferedImage img = uTool.scaledImage(ImageIO.read(getClass().getResourceAsStream("/res/object/coin.png")), gp.tileSize/2, gp.tileSize/2);
            // set as down1 so Entity.draw will display it
            down1 = img;
            down2 = img;
        } catch (IOException e) {
            // fallback: leave images null and draw primitive
        }
        // set solid area so player can pick it up
        solidArea = new java.awt.Rectangle(0,0,gp.tileSize/2,gp.tileSize/2);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        collision = false;
    }
}