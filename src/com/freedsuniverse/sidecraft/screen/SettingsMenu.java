package com.freedsuniverse.sidecraft.screen;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import com.freedsuniverse.sidecraft.Settings;
import com.freedsuniverse.sidecraft.Sidecraft;
import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.input.Mouse;

public class SettingsMenu extends Screen{
    
    Screen last;
    Button sound, back;
    public SettingsMenu(Screen l){
        last = l;
        setBackgroundTile(l.getBackgroundTile());
        
        sound = new Button("Sound: " + Settings.SOUND, Button.DEFAULT_TILE, new Rectangle(Sidecraft.width / 2 - 32 * 4, Sidecraft.height / 2 + 16, 32*8, 32));
        back = new Button("Back", Button.DEFAULT_TILE, new Rectangle(10, 10, 32*8, 32));
        setVisible(true);
    }

    @Override
    public void update() {
        if(!Mouse.clicked(MouseEvent.BUTTON1)) return;
        
        if(sound.getBounds().contains(Mouse.getPoint())){
            Settings.SOUND = !Settings.SOUND;
            sound.setText("Sound: " + Settings.SOUND);
            Sidecraft.music.stop();
        }else if(back.getBounds().contains(Mouse.getPoint())){
            Sidecraft.currentScreen = last;
        }
        
    }
    
    @Override
    public void draw() {
        if(!isVisible()) return;
        
        for(int x = 0; x < (Sidecraft.width / 32) + 1; x++){
            for(int y = 0; y < (Sidecraft.height / 32) + 1; y++){
                Engine.render(x * 32, y * 32, getBackgroundTile());
            }
        }
        sound.draw();
        back.draw();       
    }
}
