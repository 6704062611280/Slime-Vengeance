package object;

import java.io.IOException;

import javax.imageio.ImageIO;

import entity.Entity;
import main.GamePanel;

public class OBJ_Heart extends Entity{

    

    public OBJ_Heart(GamePanel gp){

        super(gp);

        name = "key";
        image = setup("/object/heart_full");
        image2 = setup("/object/heart_half");
        image3 = setup("/object/heart_blank");

        
    }
}
