package com.freedsuniverse.sidecraft.entity;

import java.awt.Rectangle;

import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.world.Location;

public class LivingEntity implements Entity{

    private static final double STEP_LENGTH = 3.0;
    private Location loc;
    private Rectangle rec;
    
    double spaceMoved = 0;
    
    public void damage(int d){
    }
    
    @Override
    public void update() {    
    }

    @Override
    public void draw() {  
    }

    @Override
    public void destroy() {
    }

    @Override
    public Location getLocation() {
        return loc;
    }

    public void updatePosition(int xDirection, int yDirection, int xSpeed, int ySpeed){
        double xDiff = (xDirection * xSpeed);
        double yDiff = (yDirection * ySpeed);

        getLocation().modifyX(xDiff / 32);
        getLocation().modifyY(yDiff / 32);
               
        spaceMoved += Math.abs(xDiff);
        if(getLocation().modify(0, -1).getBlockAt().getType().isSolid()){
            if(spaceMoved > STEP_LENGTH * 32){
                Material.getSound(getLocation().getBlockAt().getTypeId()).play();
                spaceMoved = 0;
            }
        }
    }
    
    @Override
    public Rectangle getBounds() {
        return rec;
    }

    public void setBounds(Rectangle rec) {
        this.rec = rec;
    }
    
    public void setLocation(Location loc) {
        this.loc = loc;
    }
}
