package com.freedsuniverse.sidecraft.entity;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.engine.Light;
import com.freedsuniverse.sidecraft.world.Location;

public class Entity{
    
    private Location loc;

    private BufferedImage img;
    private Light l;
    
    public Entity() {
        l = new Light();
    }
    
    public void update() {    
        l.setColor(null);
    }

    public void draw() {
        Engine.render(loc, getSkin());
    }

    public void destroy() {
        loc.getWorld().unregisterEntity(this);
    }
    
    public void setLocation(Location l) {
       loc = l; 
    }
    
    public void setBounds(Rectangle r) {
        this.loc = Location.valueOf(r);
    }
    
    public void setSkin(BufferedImage i) {
        img = i;
    }
    
    public BufferedImage getSkin() {
        return img;
    }
    
    public Location getLocation() {
        return loc;
    }

    public Rectangle getBounds() {
        return loc.toRectangle(this.getSkin().getWidth(), this.getSkin().getHeight());
    }

    public Entity[] getNearbyEntites() {
        return null;
    }
    
    public Light getLight() {
        return l;
    }
    
    public void light(Light l) {
        light(l.getColor());
    }
    
    public void light(Color c) {
        if(l.getColor() == null) {
            l.setColor(c);
        }else {
            l.setColor(Light.mix(l.getColor(), c));
        }
    }
    
    public String toString() {
        return loc.toString();
    }
}
