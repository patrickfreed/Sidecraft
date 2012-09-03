package com.freedsuniverse.sidecraft.inventory;

import java.awt.Rectangle;

import com.freedsuniverse.sidecraft.material.MaterialStack;

public class Slot {
    private MaterialStack content;
    private Rectangle box;
    
    public Slot(MaterialStack stack, Rectangle box){
        content = stack;
        this.box = box;
    }
    
    public MaterialStack getContent(){
        return content;
    }
    
    public void setContent(MaterialStack m){
        content = m;
    }
    
    public Rectangle getBounds(){
        return box;
    } 
}
