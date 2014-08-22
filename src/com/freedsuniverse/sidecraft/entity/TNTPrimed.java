package com.freedsuniverse.sidecraft.entity;

import java.awt.Color;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyType;

import com.freedsuniverse.sidecraft.Settings;
import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.world.Explosion;
import com.freedsuniverse.sidecraft.world.Location;

public class TNTPrimed extends Entity {
    private int width, height, fuseTicks;

    private float power;

    public TNTPrimed(int fuseTicks, float power) {
        super();

        bd.type = BodyType.DYNAMIC;
        fd.friction = 2;
        
        PolygonShape ps = new PolygonShape();
        ps.setAsBox(0.5f, 0.5f);
        
        fd.shape = ps;
        
        this.setSkin(Material.TNT.getImage());
        
        width = Settings.BLOCK_SIZE;
        height = Settings.BLOCK_SIZE;

        this.power = power;
        this.fuseTicks = fuseTicks;
    }

    public void spawn(Location l) {
        this.setLocation(l);
        this.b = getLocation().getWorld().preRegisterEntity(this);
    }

    public void update() {        
        super.update();
        
        --fuseTicks;

        if (fuseTicks <= 0) {
            destroy();
            explode();
        }

    }

    public void draw() {
        if (fuseTicks % 10 < 5) {
        	Engine.render(getLocation(), height, width, Material.TNT.getImage());
        } else {
                Engine.graphics.setColor(Color.WHITE);
                int[] locs = getLocation().toArray();
                Engine.graphics.fillRect(locs[0] - width / 2, locs[1] - height / 2, width, height);
        }
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
