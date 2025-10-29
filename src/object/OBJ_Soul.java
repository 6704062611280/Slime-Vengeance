package object;

import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;

public class OBJ_Soul extends SuperObject{

    GamePanel gp;
    public OBJ_Soul(GamePanel gp){
        name = "Soul";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/res/object/soul.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
