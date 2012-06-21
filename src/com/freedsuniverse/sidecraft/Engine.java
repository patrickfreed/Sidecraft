package com.freedsuniverse.sidecraft;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.freedsuniverse.sidecraft.world.Location;

public class Engine {
    public static Graphics graphics;
    
    public static void render(Rectangle rec, BufferedImage img){
        render(rec.x, rec.y, rec.height, rec.width, img);
    }
    public static void render(int x, int y, int length, int width, BufferedImage img){
        graphics.drawImage(scaleImage(img, width, length), x ,y, null);
    }
    
    public static void render(Location loc, int length, int width, BufferedImage img){
        Rectangle rec = loc.toRectangle(length, width);
        render(rec, img);
    }
    
    public static void render(int x, int y, BufferedImage img){
        graphics.drawImage(img, x, y, null);
    }
    
    public static void renderString(String str, int x, int y, Color c){
        graphics.setColor(c);
        graphics.drawString(str, x, y);
    }
    
    public static void renderString(String str, Location loc, Color c){
        Rectangle r = loc.toRectangle(1, 1);
        renderString(str, r.x, r.y, c);
    }
    
    public static Image scaleImage(BufferedImage original, int newWidth, int newHeight){
        return original.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_FAST);
    }
        
}
