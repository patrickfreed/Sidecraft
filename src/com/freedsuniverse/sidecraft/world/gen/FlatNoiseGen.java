package com.freedsuniverse.sidecraft.world.gen;

import java.util.Random;

import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.world.GameWorld;

import net.royawesome.jlibnoise.Noise;
import net.royawesome.jlibnoise.NoiseQuality;

public class FlatNoiseGen extends WorldGen {

    public static final Material GRASS = Material.GRASS;
    public static final Material DIRT = Material.DIRT;
    public static final Material STONE = Material.STONE;
    public static final Material AIR = Material.AIR;

    public double threshold = 0.0;
    public double scale = 16.0;
    public int surface = 10;
    public int lower = -10;
    public Random rand;
    /** Random used for population */
    public Random pRnd = new Random();

    public FlatNoiseGen() {
        super(new Random().nextInt(10000000 - 1000000) + 1000000);
        rand = new Random(getSeed());
    }

    public double noise(int x, int y) {
        return Noise.GradientCoherentNoise3D(x / (scale * 2), y / scale, 0, getSeed(), NoiseQuality.BEST);
    }

    @SuppressWarnings("unused")
    public Material getBlock(GameWorld world, int x, int y) {
        Material block = getRawBlock(x, y);
        // population: ores
        if (block == STONE && false) {
            int oreAround = world.oreAmountAround(x, y);
            if (oreAround == 4 || (oreAround > 0 && pRnd.nextInt(4) < oreAround) || pRnd.nextInt(64) == 0) {
                block = getRandomOreMaterial();
            }
        }
        return block;
    }

    public Material getRawBlock(int x, int y) {
        if (y > 128) {
            System.err.println("Height exceeded!");
            return AIR;
        }
        // hard limit
        double d = noise(x, y);
        if (y > surface) {
            int p = y - surface;
            d = d - p / scale;
        } else if (y < lower) {
            int p = lower - y;
            d = d + p / 32.0;
        }

        if (d >= threshold) {
            Material id = getRawBlock(x, y + 1);
            if (id == AIR) {
                return GRASS;
            }
            if (id == GRASS) {
                return DIRT;
            }
            return STONE;
        }
        return AIR;
    }

    private Material getRandomOreMaterial() {
        int oreType = pRnd.nextInt(5);
        switch (oreType) {
        case 0:
        case 1:
        case 2:
            return Material.COAL_ORE;
        case 3:
            return Material.SILVER_ORE;
        case 4:
            return Material.GOLD_ORE;
        default:
            return Material.COAL_ORE; // should never get here
        }
    }
}