package com.freedsuniverse.sidecraft.material;

import java.awt.Rectangle;

import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.engine.RenderQueueItem;
import com.freedsuniverse.sidecraft.material.tools.Pickaxe;
import com.freedsuniverse.sidecraft.material.tools.Quality;

public class Tool extends Item{
    
    private Quality q;
    
    public static Tool valueOf(String s, String q) {
        if(s.equals("PICKAXE")) {
            return new Pickaxe(Quality.valueOf(q));
        }else {
            return null;
        }
    }
    
    public Tool(Material m, Quality qual) {
        super(m, 1);
        q = qual;
    }
    
    @Override
    public void setAmount(int n) {
        return;
    }
    
    public Quality getQuality() {
        return q;
    }
    
    public void draw(Rectangle box, boolean queue) {
        RenderQueueItem texture = new RenderQueueItem(getImage(), box);        
        if(queue){
            Engine.addQueueItem(texture);
        }else{
            Engine.render(texture);
        } 
    }
    
    public String toString() {
        return super.toString();
    }
}