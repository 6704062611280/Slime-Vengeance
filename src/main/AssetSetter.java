package main;

import enemy.ENEMY_Huntman;
import object.OBJ_Soul;

public class AssetSetter {
    GamePanel gp;

    public AssetSetter(GamePanel gp){
        this.gp = gp;
    }

    public void setObject(){
        gp.obj[0] = new OBJ_Soul(gp);
        gp.obj[0].worldX = 23 * gp.tileSize;
        gp.obj[0].worldY = 7 * gp.tileSize;

        gp.obj[1] = new OBJ_Soul(gp);
        gp.obj[1].worldX = 23 * gp.tileSize;
        gp.obj[1].worldY = 40 * gp.tileSize;

    }

    public void setEnemy(){
        gp.enemy[0] = new ENEMY_Huntman(gp);
        gp.enemy[0].worldX = gp.tileSize*21;
        gp.enemy[0].worldY = gp.tileSize*36;

        gp.enemy[1] = new ENEMY_Huntman(gp);
        gp.enemy[1].worldX = gp.tileSize*21;
        gp.enemy[1].worldY = gp.tileSize*37;

    }
}
