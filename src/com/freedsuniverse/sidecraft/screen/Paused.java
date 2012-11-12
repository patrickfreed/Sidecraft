package com.freedsuniverse.sidecraft.screen;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import com.freedsuniverse.sidecraft.Settings;
import com.freedsuniverse.sidecraft.Sidecraft;
import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.input.Mouse;

public class Paused extends Screen{    
    private Button resume, settings;

    public Paused(){
        resume = new Button("Resume", Button.DEFAULT_TILE, new Rectangle(Sidecraft.width / 2 - 32 * 4, Sidecraft.height / 2 + 16, 32*8, 32));
        settings = new Button("Settings", Button.DEFAULT_TILE, new Rectangle(Sidecraft.width / 2 - 32 * 4, Sidecraft.height / 2 + 54, 32*8, 32));
        setVisible(false);
        setBackgroundTile(Sidecraft.getImage((Settings.PAUSED_BACKGROUND)));
    }
    
    @Override
    public void update() {
        if(isVisible()){
            if(Mouse.clicked(MouseEvent.BUTTON1)){
                if(resume.getBounds().contains(Mouse.getPoint())){
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
            Engine.renderString("Paused", Sidecraft.width / 2 - "Paused".length() * 3, 100, Color.white);
            resume.draw();
            settings.draw();
        }
    }
}
