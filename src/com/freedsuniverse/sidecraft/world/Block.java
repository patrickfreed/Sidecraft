package com.freedsuniverse.sidecraft.world;

import java.awt.Graphics;
import java.awt.Rectangle;

import com.freedsuniverse.sidecraft.Engine;
import com.freedsuniverse.sidecraft.Settings;
import com.freedsuniverse.sidecraft.entity.DropEntity;
import com.freedsuniverse.sidecraft.entity.TNTPrimed;
import com.freedsuniverse.sidecraft.material.Material;

public class Block {
    private Location location;

    private int health;

    private Material data;

    public Block(Material d) {
        this.data = d;
        this.health = data.getDurability();
    }

    public int getTypeId() {
        return this.data.getId();
    }

    public Material getType() {
        return this.data;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location newc) {
        this.location = newc;
    }

    public void setType(Material dataToSet) {
        this.data = dataToSet;
        this.health = dataToSet.getDurability();
    }

    public void draw(){
        Engine.render(getBounds().x, getBounds().y, getType().getImage());
    }
    
    public void draw(Graphics g) {
        g.drawImage(getType().getImage(), Settings.BLOCK_SIZE, Settings.BLOCK_SIZE, null);
    }

    public int getHealth() {
        return health;
    }

    public void damage(int d) {
        this.health -= d;

        if (health <= 0)
            destroy();
    }

    public Rectangle getBounds() {
        return location.toRectangle(32, 32);
    }

    public void destroy(){
        Material material = this.getType();
        setType(Material.AIR);

        //TODO: Move this to a better place
        if (material == Material.TNT) {
            new TNTPrimed(location, 100, 5);
            return;
        }

        for (int x = 0; x < material.getDropAmount(); x++) {
            new DropEntity(material.getDropType(), new Location(location.getX() + 0.6 * x, location.getY()));
        }
    }
}
