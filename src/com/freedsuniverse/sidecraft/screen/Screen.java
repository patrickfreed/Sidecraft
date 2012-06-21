package com.freedsuniverse.sidecraft.screen;

import java.awt.Rectangle;



public interface Screen {
    
    public void update();
    
    public boolean isVisible();
    
    public void useButton(Rectangle r);
    
    public void show();
    
    public void hide();
    
    public void draw();
}
