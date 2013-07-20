package com.freedsuniverse.sidecraft.world.active;

import java.awt.Color;

import com.freedsuniverse.sidecraft.entity.LightSource;
import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.world.Block;

public class Torch extends Block{
    public Torch() {
        super(Material.GRASS);
        s = new LightSource(Color.red, 155, 5);
    }
    
    public void update() {
        super.update();
        
        if(getLocation() != null) {
            if(s.getLocation() == null) {
                s.setLocation(getLocation());
                getLocation().getWorld().registerEntity(s);
            }        
        }
    }
    
}
