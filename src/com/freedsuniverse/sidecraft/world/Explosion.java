package com.freedsuniverse.sidecraft.world;

import com.freedsuniverse.sidecraft.material.Material;

public class Explosion {

    /** Creates an explosion centered on loc with radius radius. */
    public static void createExplosion(Location loc, float radius) {
        GameWorld world = loc.getWorld();
        int centerX = (int) loc.getX();
        int centerY = (int) loc.getY();
        int r = ((int) radius) + 1;
        float hyp = radius * radius;

        // TODO: improver performance

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                if ((x * x) + (y * y) <= hyp) {
                    Block curBlock = world.getBlockAt(new Location(centerX + x, centerY + y, world.getName()));
                    if (curBlock != null && curBlock.getType() != Material.AIR) {
                        curBlock.destroy();
                    }
                }
            }
        }
    }
}
