package com.freedsuniverse.sidecraft.world.gen;

import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.world.GameWorld;

public abstract class WorldGen {
    private int seed;

    public WorldGen(int seed) {
        this.seed = seed;
    }

    public int getSeed() {
        return seed;
    }

    public abstract Material getBlock(GameWorld l, int x, int y);
}
