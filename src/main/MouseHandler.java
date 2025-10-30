package main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseHandler extends MouseAdapter {

    GamePanel gp;

    public MouseHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // ถ้าอยู่ในหน้า Title
        if (gp.gameState == gp.titleState || gp.gameState == gp.gameOverState || gp.gameState == gp.upgradeState) {
            gp.ui.checkClick(x, y);
        }

        // ในอนาคตจะเพิ่มส่วนอื่น เช่น pause menu, inventory ฯลฯ
    }
}
