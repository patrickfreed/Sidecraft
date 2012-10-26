package com.freedsuniverse.sidecraft.screen;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.freedsuniverse.sidecraft.Sidecraft;

public class Screen {   
    private BufferedImage backgroundTile;
    
    private boolean visible;
    
    
    public void update() {
    }

    public void useButton(Rectangle r) {
    }

    public boolean isVisible(){
        return visible;
    }
    
    public void show() {
        setVisible(true);
    }

    public void hide() {
        setVisible(false); 
        Sidecraft.isPaused = false;
    }
    
    public void draw() {
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public BufferedImage getBackgroundTile() {
        return backgroundTile;
    }

    public void setBackgroundTile(BufferedImage backgroundTile) {
        this.backgroundTile = backgroundTile;
    }
}
