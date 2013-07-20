package com.freedsuniverse.sidecraft.entity;

import com.freedsuniverse.sidecraft.Main;
import com.freedsuniverse.sidecraft.Settings;
import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.material.Item;
import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.material.MaterialStack;
import com.freedsuniverse.sidecraft.world.Block;
import com.freedsuniverse.sidecraft.world.Location;
import com.freedsuniverse.sidecraft.world.World;

public class DropEntity extends Entity{
    final static double REACH_DISTANCE = 1.8;
    
    private final Item type;

    private int width = 15;
    private int height = 15;

    public DropEntity(Item t, Location loc) {
        height = Settings.BLOCK_SIZE / 2;
        width = Settings.BLOCK_SIZE / 2;
        setLocation(loc);
        type = t;
        setSkin(t.getType().getImage());
    }
    
    public DropEntity(Material t, Location loc) {
        this(new MaterialStack(t, 1), loc);
    }

    public DropEntity(Material t, double x, double y, World world) {
        this(t, new Location(x, y, world.getName()));
    }

    public DropEntity(Material t, double x, double y) {
        this(t, new Location(x, y));
    }

    public void spawn() {
        while (this.getLocation().getWorld().getBlockAt(getLocation().modify(0, -0.5)).getType().isSolid()) {
            getLocation().modifyY(1);
        }
       
        getLocation().getWorld().registerEntity(this);
    }

    @Override
    public void update() {     
        
        boolean playerSide = Main.getGame().player.getLocation().getX() > getLocation().getX();
        
        Block nextBlockY = this.getLocation().getWorld().getBlockAt(new Location(getLocation().getX(), getLocation().getY() - 0.5));
        Block nextBlockX;
        
        if(playerSide) {
            nextBlockX = this.getLocation().getWorld().getBlockAt(getLocation().modify(0.2, 0));
        }else {
            nextBlockX = this.getLocation().getWorld().getBlockAt(getLocation().modify(-0.2, 0));
        }
                
        if (!nextBlockY.getType().isSolid()) {
            getLocation().modifyY(-0.1);
        }

        double xDistance = Math.abs(Main.getGame().player.getLocation().getX() - getLocation().getX());
        double yDistance = Math.abs(Main.getGame().player.getLocation().getY() - getLocation().getY());
        
        if(xDistance > 0.3 && yDistance > 0.3) {
            if (xDistance <= REACH_DISTANCE && yDistance <= REACH_DISTANCE && !nextBlockX.getType().isSolid()) {
                if (playerSide) {
                    getLocation().modifyX(0.2);
                }
                else {
                    getLocation().modifyX(-0.2);
                }
            }
        }
        if(getBounds().intersects(Main.getGame().player.getBounds())){
            destroy();
        }
    }

    public void destroy() {
        Main.getGame().player.getInventory().add(this.type);
        getLocation().getWorld().unregisterEntity(this);
    }

    public void draw() {
        Engine.render(getLocation(), height, width, this.getSkin());
    }

    public String toString() {
        String s = "";
        s += "DropEntity";
        s += ":" + this.type;
        s += ":"+ getLocation();
        return s;
    }
}
