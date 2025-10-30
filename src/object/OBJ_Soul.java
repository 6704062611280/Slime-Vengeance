package object;

import java.awt.Rectangle;
import java.io.IOException;

import javax.imageio.ImageIO;

import entity.Entity;
import main.GamePanel;

public class OBJ_Soul extends Entity{

    public OBJ_Soul(GamePanel gp){
        super(gp);

    name = "Soul";
    down1 = setup("/object/soul");
    down2 = down1;

        // ✅ เพิ่มส่วนนี้
        solidArea = new Rectangle(0, 0, gp.tileSize, gp.tileSize);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        collision = false; // ถ้าให้เก็บได้ก็ false

        
    }
}
