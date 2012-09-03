package com.freedsuniverse.sidecraft.screen;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import com.freedsuniverse.sidecraft.Sidecraft;
import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.input.Mouse;


public class Menu implements Screen{

    private HashMap<Rectangle, Boolean> buttons;
    
    private Color selected, idle;
    
    private boolean visible;
    
    Rectangle START_GAME = new Rectangle(96, 100, 96, 32);
    
    public Menu(){
        visible = false;
        buttons = new HashMap<Rectangle, Boolean>();
        buttons.put(START_GAME, false);
        selected = Color.cyan;
        idle = Color.red;
    }
    
    @Override
    public void update() {
        if(visible){
            Point mouse = new Point(Mouse.getX(), Mouse.getY());

            for(Rectangle r:buttons.keySet()){
                if(r.contains(mouse)){
                    buttons.put(r, true);
                    if(Mouse.isDown(MouseEvent.BUTTON1)){
                        useButton(r);
                    }
                }else{
                    buttons.put(r, false);
                }
            }
        } 
    }

    @Override
    public void useButton(Rectangle r) {
        if(r == START_GAME){
            hide();
        }
    }

    @Override
    public boolean isVisible(){
        return visible;
    }
    
    @Override
    public void show() {
        visible = true;
        
    }

    @Override
    public void hide() {
        visible = false; 
        Sidecraft.isScreen = false;
    }

    @Override
    public void draw() {
        if(visible){
            for(Rectangle r:buttons.keySet()){
                if(buttons.get(r)){
                    Engine.graphics.setColor(selected);    
                }else{
                    Engine.graphics.setColor(idle);
                }
                Engine.graphics.fillRect((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
            }
        }
    }
}
