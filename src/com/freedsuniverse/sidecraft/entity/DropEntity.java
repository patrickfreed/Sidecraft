package com.freedsuniverse.sidecraft.entity;

import java.awt.Rectangle;

import com.freedsuniverse.sidecraft.Settings;
import com.freedsuniverse.sidecraft.Sidecraft;
import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.material.MaterialStack;
import com.freedsuniverse.sidecraft.world.Block;
import com.freedsuniverse.sidecraft.world.Location;
import com.freedsuniverse.sidecraft.world.World;

public class DropEntity implements Entity{
    final static double REACH_DISTANCE = 1.8;
    
    private final Material type;
    private Location location;

    private int WIDTH = 15;
    private int HEIGHT = 15;

    public DropEntity(Material t, Location loc) {
        HEIGHT = Settings.BLOCK_SIZE / 2;
        WIDTH = Settings.BLOCK_SIZE / 2;
        location = new Location(loc.getX(), loc.getY(), loc.getWorld().getName());
        type = t;
    }

    public DropEntity(Material t, double x, double y, World world) {
        location = new Location(x, y, world.getName());
        type = t; HEIGHT = Settings.BLOCK_SIZE / 2;
        WIDTH = Settings.BLOCK_SIZE / 2;
    }

    public DropEntity(Material t, double x, double y) {
        HEIGHT = Settings.BLOCK_SIZE / 2;
        WIDTH = Settings.BLOCK_SIZE / 2;
        type = t;
        location = new Location(x, y);
    }

    public void spawn() {
        while (this.location.getWorld().getBlockAt(location.modify(0, -0.5)).getType().isSolid()) {
            location.modifyY(1);
        }
       
        location.getWorld().registerEntity(this);
    }

    @Override
    public void update() {     
        
        boolean playerSide = Sidecraft.player.getLocation().getX() > getLocation().getX();
        
        Block nextBlockY = this.getLocation().getWorld().getBlockAt(new Location(location.getX(), location.getY() - 0.5));
        Block nextBlockX;
        
        if(playerSide){
            nextBlockX = this.getLocation().getWorld().getBlockAt(location.modify(0.2, 0));
        }else{
            nextBlockX = this.getLocation().getWorld().getBlockAt(location.modify(-0.2, 0));
        }
                
        if (!nextBlockY.getType().isSolid()) {
            location.modifyY(-0.1);
        }

        double xDistance = Math.abs(Sidecraft.player.getLocation().getX() - getLocation().getX());
        double yDistance = Math.abs(Sidecraft.player.getLocation().getY() - getLocation().getY());
        
        if(xDistance > 0.3 && yDistance > 0.3){
            if (xDistance <= REACH_DISTANCE && yDistance <= REACH_DISTANCE && !nextBlockX.getType().isSolid()) {
                if (playerSide) {
                    location.modifyX(0.2);
                }
                else {
                    location.modifyX(-0.2);
                }
            }
        }
        if(getBounds().intersects(Sidecraft.player.getBounds())){
            destroy();
        }
    }

    public void destroy() {
        Sidecraft.player.getInventory().add(new MaterialStack(this.type, 1));
        getLocation().getWorld().unregisterEntity(this);
    }

    public void draw() {
        Engine.render(location, HEIGHT, WIDTH, type.getImage());
    }

    public Location getLocation() {
        return this.location;
    }

    
    
    public Rectangle getBounds() {
        return location.toRectangle(WIDTH, HEIGHT);
    }

    @Override
    public void setLocation(Location l) {
        this.location = l; 
    }
}
