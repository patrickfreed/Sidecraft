package com.freedsuniverse.sidecraft.world;

import java.util.Random;

import com.freedsuniverse.sidecraft.material.Material;

import net.royawesome.jlibnoise.Noise;
import net.royawesome.jlibnoise.NoiseQuality;

public class FlatNoiseGen {

	public static final Material GRASS = Material.GRASS;
	public static final Material DIRT = Material.DIRT;
	public static final Material STONE = Material.STONE;
	public static final Material AIR = Material.AIR;

	public int seed = 2525353;
	public double threshold = 0.0;
	public double scale = 16.0;
	public int surface = 10;
	public int lower = -10;
	public Random rand = new Random(seed);

	public double noise(int x, int y) {
		return Noise.GradientCoherentNoise3D(x/(scale*2), y/scale, 0, seed, NoiseQuality.BEST);
	}

	public Material getBlock(int x, int y) {
		if(y > 128) {
			System.err.println("Height exceeded!");
			return AIR;
		}
		// hard limit
		double d = noise(x, y);
		if(y > surface) {
			int p = y-surface;
			d = d-p/scale;
		} else if(y < lower) {
			int p = lower-y;
			d = d+p/32.0;
		}

		if(d >= threshold) {
			Material id = getBlock(x, y+1);
			if(id == AIR) {
				return GRASS;
			} 
			if(id == GRASS) {
				return DIRT;
			}
			return STONE;
		}
		return AIR;
	}

}
