package com.freedsuniverse.sidecraft.world;

import java.awt.Rectangle;

import com.freedsuniverse.sidecraft.Sound;
import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.entity.DropEntity;
import com.freedsuniverse.sidecraft.entity.Entity;
import com.freedsuniverse.sidecraft.entity.LightSource;
import com.freedsuniverse.sidecraft.entity.TNTPrimed;
import com.freedsuniverse.sidecraft.material.Material;

public class Block extends Entity{     
    public static final int TYPE = 0, X = 1, Y = 2, WORLD = 3;
    
    private Material data;
    protected LightSource s;
    private int health;

    public Block(Material d) {
        this.data = d;
        health = data.getDurability();       
    }    

    public void interact(Entity e){
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
    }

    public Rectangle getBounds() {
        return getLocation().toRectangle(this.data.getImage().getWidth(), this.data.getImage().getHeight()); 
    }
    
    public void draw(){
        Engine.render(getBounds().x, getBounds().y, getType().getImage());
    }

    public void damage(int d) {
        Sound.blockDamage.play();
        this.health -= d;
                     
        if (health <= 0){
            destroy();
        }
    }

    public void destroy(){
        Sound.blockDamage.play();
        
        Material material = this.getType();
        setType(Material.AIR);

        //TODO: Move this to a better place
        if (material == Material.TNT) {
            new TNTPrimed(getLocation(), 100, 5).spawn();
            return;
        }

        for (int x = 0; x < material.getDropAmount(); x++) {
            new DropEntity(material.getDropType(), new Location(getLocation().getX() + 0.6 * x, getLocation().getY())).spawn();
        }
    }
    
    public String toString() {
        String s = "";
        s += getType();
        s += ":" + this.getLocation();
        return s;
    }
}
