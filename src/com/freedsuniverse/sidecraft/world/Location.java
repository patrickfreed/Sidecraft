package com.freedsuniverse.sidecraft.world;

import java.awt.Rectangle;

import com.freedsuniverse.sidecraft.Settings;
import com.freedsuniverse.sidecraft.Sidecraft;

public class Location {
    private double x, y;
    private String world;

    public Location(double x, double y) {
        this.x = x;
        this.y = y;
        this.world = Sidecraft.player.getWorld().getName();
    }

    public Location(double x, double y, String world) {
        this.x = x;
        this.y = y;
        this.world = world;
    }

//    public static Location valueOf(Vector2 position){
//        return valueOf(position.X, position.Y);
//    }

    public static Location valueOf(Rectangle rect) {
        return valueOf(rect.getX(), rect.getY());
    }

    public static Location valueOf(double d, double e) {
        double x = (d - Sidecraft.player.ScreenPosition.getX()) / Settings.BLOCK_SIZE + Sidecraft.player.coordinates.getX();
        double y = (e - Sidecraft.player.ScreenPosition.getY()) / -Settings.BLOCK_SIZE + Sidecraft.player.coordinates.getY();

        return new Location(x, y);
    }

    public int[] toArray() {
        int x = (int) ((this.x - Sidecraft.player.coordinates.getX()) * Settings.BLOCK_SIZE + Sidecraft.player.ScreenPosition.getX());
        int y = (int) (Sidecraft.player.ScreenPosition.getY() - ((this.y - Sidecraft.player.coordinates.getY()) * Settings.BLOCK_SIZE));

        return new int[]{x,y};
    }

    public Rectangle toRectangle(int width, int height) {
        int[] i = toArray();

        return new Rectangle(i[0], i[1], width, height);
    }

    public String toString() {
        return "{X:" + x + "" + " Y:" + y + "}";
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

    public boolean intersects(Location otherLocation) {
        if(otherLocation.getWorld().getName().equals(world)){
            return Math.floor(this.getX()) - Math.floor(otherLocation.getX()) == 0 && Math.floor(this.getY()) - Math.floor(otherLocation.getY()) == 0;
        }
        return false;
    }

    public World getWorld() {
        return Sidecraft.worlds.get(world);
    }
}
