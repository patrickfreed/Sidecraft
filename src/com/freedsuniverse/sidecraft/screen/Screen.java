package com.freedsuniverse.sidecraft.screen;

import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.freedsuniverse.sidecraft.Main;
import com.freedsuniverse.sidecraft.Settings;

public class Screen extends JPanel {
    private static final long serialVersionUID = -4312314981973034251L;

    private BufferedImage img = Main.getImage(Settings.MENU_BACKGROUND_TILE);
    private String prev;

    public Screen() {   
        this.addComponentListener( new ComponentListener() {
            @Override
            public void componentShown( ComponentEvent e ) {
                Screen.this.requestFocusInWindow();
            }

            @Override
            public void componentResized(ComponentEvent e) {    
            }

            @Override
            public void componentMoved(ComponentEvent e) {   
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int x = 0; x < this.getWidth(); x += 32) {
            for (int y = 0; y < this.getHeight(); y += 32) {
                g.drawImage(img, x, y, null);
            }
        }
    }

    public void setPreviousScreen(Screen p) {
        prev = p.getClass().getName();
    }

    public String getPreviousScreen() {
        if (prev == null) {
            return Menu.class.getName();
        } else {
            return prev;
        }
    }
}
