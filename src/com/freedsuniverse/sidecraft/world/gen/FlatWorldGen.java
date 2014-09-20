package com.freedsuniverse.sidecraft.world.gen;

import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.world.GameWorld;

public class FlatWorldGen extends WorldGen {

    public FlatWorldGen() {
        super(0);
    }

    public Material getBlock(GameWorld l, int x, int y) {
        if (y > -1) {
            return Material.AIR;
        }
        if (y == -1) {
            return Material.GRASS;
        } else if (y == -2) {
            return Material.DIRT;
        } else {
            return Material.STONE;
        }
    }
}
