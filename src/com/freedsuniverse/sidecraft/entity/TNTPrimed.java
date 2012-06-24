package com.freedsuniverse.sidecraft.entity;

import java.awt.Color;
import java.awt.Rectangle;

import com.freedsuniverse.sidecraft.Engine;
import com.freedsuniverse.sidecraft.Settings;
import com.freedsuniverse.sidecraft.Sidecraft;
import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.material.MaterialStack;
import com.freedsuniverse.sidecraft.world.Block;
import com.freedsuniverse.sidecraft.world.Explosion;
import com.freedsuniverse.sidecraft.world.Location;
import com.freedsuniverse.sidecraft.world.World;

public class TNTPrimed implements Entity {

    private Location location;

    private int width, height, fuseTicks;

    private float power;

    public TNTPrimed(Location loc, int fuseTicks, float power) {
        width = Settings.BLOCK_SIZE;
        height = Settings.BLOCK_SIZE;
        this.power = power;
        this.fuseTicks = fuseTicks;
        spawn(loc);
    }

    public void spawn(Location loc) {
        this.location = loc;
        loc.getWorld().registerEntity(this);
    }

    public void update() {
        Block nextBlock = this.getLocation().getWorld().getBlockAt(new Location(location.getX(), location.getY() - 1));
        if (!nextBlock.getType().isSolid()) {
            location.modifyY(-0.1);
        }

        --fuseTicks;

        if (fuseTicks <= 0) {
            destroy();
            explode();
        }

    }

    public void destroy() {
        getLocation().getWorld().unregisterEntity(this);
    }


    public void draw() {
        if (fuseTicks % 10 < 5) {
        	Engine.render(location, height, width, Material.TNT.getImage());
        } else {
                Engine.graphics.setColor(Color.WHITE);
                int[] locs = location.toArray();
                Engine.graphics.fillRect(locs[0], locs[1], height, width);
        }
    }

    public Location getLocation() {
        return this.location;
    }

    public Rectangle getBounds() {
        return location.toRectangle(width, height);
    }

    public float getExplosionPower() {
        return this.power;
    }

    public void setExplosionPower(float power) {
        this.power = power;
    }

    public int getFuseTicks() {
        return fuseTicks;
    }

    public void setFuseTicks(int fuseTicks) {
        this.fuseTicks = fuseTicks;
    }

    public void explode() {
        Explosion.createExplosion(this.getLocation(), this.power);
    }
}
