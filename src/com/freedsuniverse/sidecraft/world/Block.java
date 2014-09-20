package com.freedsuniverse.sidecraft.world;

import java.awt.Color;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import com.freedsuniverse.sidecraft.Sound;
import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.entity.DropEntity;
import com.freedsuniverse.sidecraft.entity.Entity;
import com.freedsuniverse.sidecraft.entity.TNTPrimed;
import com.freedsuniverse.sidecraft.material.Material;

public class Block extends Entity {
    private Material data;
    private int health;

    public Block(Material d) {
        super();

        this.data = d;
        health = data.getDurability();

        bd.type = BodyType.STATIC;
        fd.friction = 1.8f;

        if (d == Material.AIR) {
            bd.active = false;
        }

        this.setGhostSkin(d.getImage());
    }

    public static FixtureDef fixtureDef() {
        FixtureDef fd = new FixtureDef();
        fd.friction = 1.8f;
        return fd;
    }

    public void interact(Entity e) {
    }

    public int getTypeId() {
        return this.data.getId();
    }

    public void update() {
        super.update();
    }

    public Material getType() {
        return this.data;
    }

    public void setType(Material dataToSet) {
        this.data = dataToSet;
        this.health = dataToSet.getDurability();
        setSkin(dataToSet.getImage());

        if (dataToSet == Material.AIR) {
            this.b.setActive(false);
        } else {
            this.b.setActive(true);
        }
    }

    public void draw() {
        Engine.render(getLocation(), this.getSkin());

        if (this.getType() == Material.SILVER_ORE) {
            Engine.renderRectangle(this.getBounds(), Color.RED);
        }
    }

    public void damage(int d) {
        Sound.blockDamage.play();
        this.health -= d;

        if (health <= 0) {
            destroy();
        }
    }

    public void destroy() {
        Material m = data;

        getLocation().getWorld().setBlockAt(getLocation(), new Block(Material.AIR));

        for (int x = 0; x < m.getDropAmount(); x++) {
            new DropEntity(m.getDropType()).spawn(getLocation().clone());
        }

        // TODO: Move this to a better place
        if (m == Material.TNT) {
            new TNTPrimed(100, 5).spawn(getLocation());
            return;
        }

        getLocation().getWorld().lightUpdate();
    }

    public String toString() {
        return getType() + ":" + this.getLocation();
    }

    public void setBody(Body body) {
        b = body;
    }

    public void setFixtureDef(FixtureDef fd) {
        this.fd = fd;
    }
}
