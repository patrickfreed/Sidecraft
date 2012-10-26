package com.freedsuniverse.sidecraft.screen;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.freedsuniverse.sidecraft.Settings;
import com.freedsuniverse.sidecraft.Sidecraft;
import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.input.Mouse;

public class Menu extends Screen{   
    private BufferedImage background;   
    
    private Button start, settings;

    public Menu(){
        start = new Button("Play game", Button.DEFAULT_TILE, Sidecraft.width / 2 - 32 * 4, Sidecraft.height / 2 + 16, 32*8, 32);
        settings = new Button("Settings", Button.DEFAULT_TILE, Sidecraft.width / 2 - 32 * 4, Sidecraft.height / 2 + 54, 32*8, 32);
        super.setVisible(false);
        background = Sidecraft.getImage(Settings.MENU_BACKGROUND);
        super.setBackgroundTile(Sidecraft.getImage(Settings.MENU_BACKGROUND_TILE));
    }
    
    @Override
    public void update() {
        if(isVisible()){
            if(Mouse.clicked(MouseEvent.BUTTON1)){
                if(start.getBounds().contains(Mouse.getPoint())){
                    hide();
                }else if(settings.getBounds().contains(Mouse.getPoint())){
                    Sidecraft.currentScreen = new SettingsMenu(this);
                }
            }
        } 
    }
    
    @Override
    public void draw() {
        if(isVisible()){
            for(int x = 0; x < (Sidecraft.width / 32) + 1; x++){
                for(int y = 0; y < (Sidecraft.height / 32) + 1; y++){
                    Engine.render(x * 32, y * 32, getBackgroundTile());
                }
            }
            Engine.render(Sidecraft.width / 2 - background.getWidth() / 2, 50, background);
            
            start.draw();
            settings.draw();
        }
    }
}
