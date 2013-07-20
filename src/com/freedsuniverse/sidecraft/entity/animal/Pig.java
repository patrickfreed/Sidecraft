package com.freedsuniverse.sidecraft.entity.animal;

import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.material.MaterialStack;

public class Pig extends PassiveAnimal {
    public Pig() {
        super("pig", 32, 32, 10);
        getInventory().add(new MaterialStack(Material.DIRT, 1));
    }
}
