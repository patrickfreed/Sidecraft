package com.freedsuniverse.sidecraft.world.active;

import java.awt.Color;

import com.freedsuniverse.sidecraft.entity.LightSource;
import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.world.Block;

public class Torch extends Block{
    private LightSource s;
    
    public Torch() {
        super(Material.TORCH);
        s = new LightSource(Color.red, 155, 5);
    }
    
    public void destroy() {
        super.destroy();
        s = null;
    }
    
    public void update() {
        super.update(); 
        
        if(getLocation() != null) {
            s.setLocation(getLocation());
        }
        
        s.update();
    } 
}
