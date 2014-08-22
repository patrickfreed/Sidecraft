package com.freedsuniverse.sidecraft.world;

import java.awt.Rectangle;
import java.text.DecimalFormat;

import com.freedsuniverse.sidecraft.Main;
import com.freedsuniverse.sidecraft.Settings;
import com.freedsuniverse.sidecraft.Sidecraft;

public class Location {
    private double x, y;
    public String world;
    
    public Location(double x, double y) {
        this.x = x;
        this.y = y;
        this.world = Main.getGame().playerLoc.getWorldName();
    }

    public Location(double x, double y, String world) {
        this.x = x;
        this.y = y;
        this.world = world;
    }

    public static Location valueOf(Rectangle rect) {
        return valueOf(rect.getX(), rect.getY());
    }   

    public int[] toArray() {
        if(this.getWorld().getPlayer() == null) return null;
        
        int x = (int) ((this.x - Main.getGame().playerLoc.getX()) * Settings.BLOCK_SIZE + Sidecraft.getCenterBound().getX() + Settings.BLOCK_SIZE / 2.0);
        int y = (int) (Sidecraft.getCenterBound().getY() + Settings.BLOCK_SIZE / 2.0 - ((this.y - getWorld().getPlayer().getLocation().getY()) * Settings.BLOCK_SIZE));

        return new int[]{x ,y};
    }

    public Rectangle toRectangle(int width, int height) {
        if(this.getWorld().getPlayer() == null) return null;
        
        int[] i = toArray();

        return new Rectangle(i[0] - Settings.BLOCK_SIZE / 2, i[1] - Settings.BLOCK_SIZE / 2, width, height);
    }

    public String toString() {
        DecimalFormat fmt = new DecimalFormat("0.#");
        return fmt.format(x) + ":" + fmt.format(y) + ":" + this.world;
    }

    public Location modify(double x, double y) {
        return new Location(this.x + x, this.y + y, this.world);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void modifyX(double amount) {
        this.x =  x + amount;
    }

    public void modifyY(double amount) {
        this.y += amount;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Location l) {
        return l.getX() == this.x && l.getY() == this.y && l.getWorldName().equalsIgnoreCase(this.world);
    }
    
    public boolean intersects(Location otherLocation) {
        if(otherLocation.getWorld().getName().equals(world)){
            return Math.floor(this.getX()) - Math.floor(otherLocation.getX()) == 0 && Math.floor(this.getY()) - Math.floor(otherLocation.getY()) == 0;
        }
        return false;
    }

    public Block getBlockAt(){
        return getWorld().getBlockAt(this);
    }
    
    public String getWorldName() {
        return world;
    }
    
    public GameWorld getWorld() {
        return Main.getGame().worlds.get(world);
    }
    
    public String getId() {
        return (int) Math.round(x) + "," + (int) Math.round(y);
    }
    
    public Location clone() {
        return new Location(x, y, world);
    }
    
    public static Location valueOf(double d, double e) {
        double x = (d - Sidecraft.getCenterBound().getX()) / Settings.BLOCK_SIZE + Main.getGame().playerLoc.getX();
        double y = (e - Sidecraft.getCenterBound().getY()) / -Settings.BLOCK_SIZE + Main.getGame().playerLoc.getY();

        return new Location(x, y);
    }

    public double getDistance(Location location) {
        return Math.sqrt(Math.pow(x - location.getX(), 2) + Math.pow(y - location.getY(), 2));
    } 
}
