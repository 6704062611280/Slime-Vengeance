package object;

import java.awt.Color;
import java.awt.Graphics2D;

import entity.Entity;
import main.GamePanel;

public class OBJ_Particle extends Entity {

    int vx, vy;
    int lifespan = 30;
    Color color;
    GamePanel gpRef;

    public OBJ_Particle(GamePanel gp, int vx, int vy) {
        super(gp);
        this.gpRef = gp;
        this.vx = vx;
        this.vy = vy;
        this.life = lifespan;
        this.maxLife = lifespan;
        this.collision = false;
        this.name = "particle";
        this.color = new Color(255, 160, 64);
        // set a tiny solid area so it doesn't interfere
        solidArea = new java.awt.Rectangle(0,0,4,4);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    @Override
    public void update() {
        worldX += vx;
        worldY += vy;
        life--;
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - gpRef.player.worldX + gpRef.player.screenX;
        int screenY = worldY - gpRef.player.worldY + gpRef.player.screenY;

        float alpha = Math.max(0f, Math.min(1f, (float)life / (float)maxLife));
        java.awt.Composite prev = g2.getComposite();
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
        g2.setColor(color);
        int size = gpRef.tileSize / 6;
        g2.fillOval(screenX, screenY, size, size);
        g2.setComposite(prev);
    }

}
