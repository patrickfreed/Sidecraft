package com.freedsuniverse.sidecraft.material;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.engine.RenderQueueItem;
import com.freedsuniverse.sidecraft.entity.Entity;

public abstract class Item {
    
    private int am;
    private Material type;
    
    public Item(Material m, int amount) {
        am = amount;
        type = m;
    }
    
    public int getAmount() {
        return am;
    }
    
    public void modifyAmount(int t) {
        am += t;
    }
    
    public void setAmount(int n) {
        am = n;
    }
    
    public BufferedImage getImage() { 
        return type.getImage();
    }
    
    public Material getType() {
        return type;
    }
    
    public void setType(Material m) {
        type = m;
    }
    public int getDamage(Entity e) {
        return type.getDamage();
    }
    
    public void draw(Rectangle box, boolean queue) {
        draw(box.x, box.y, queue);
    }

    public void draw(int x, int y, boolean queue) {
        RenderQueueItem i = new RenderQueueItem(x, y, this.getImage());
        if(queue) Engine.addQueueItem(i);
        else Engine.render(i);  
    }
    
    public String toString() {
        return type + ":" + am;
    }
    
}
