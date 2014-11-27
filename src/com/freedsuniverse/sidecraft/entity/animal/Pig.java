package com.freedsuniverse.sidecraft.entity.animal;

import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.material.MaterialStack;

public class Pig extends Animal {
    
	public Pig() {
        super("pig", 30, 30, 10, 16.0f);
        getInventory().add(new MaterialStack(Material.DIRT, 1));
    }
}
