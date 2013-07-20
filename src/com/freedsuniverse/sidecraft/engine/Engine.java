package com.freedsuniverse.sidecraft.engine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import com.freedsuniverse.sidecraft.world.Location;

public class Engine {
    public static Graphics graphics;
    
    private static LinkedList<RenderQueueItem> queue = new LinkedList<RenderQueueItem>();
    
    public static void renderRectangle(Rectangle rec, Color c){
        renderRectangle(rec.x, rec.y, rec.width, rec.height, c);
    }
    
    public static void renderRectangle(int x, int y, int width, int height, Color c){
        graphics.setColor(c);
        graphics.fillRect(x, y, width, height);
    }
    
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
    
    public static void render(Location loc, BufferedImage img) {
        render(loc.toRectangle(img.getWidth(), img.getHeight()), img);
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
    
    public static void drawQueue(){     
        for(int x = 0; x < queue.size(); x++){
            queue.get(x).draw();           
        }
        queue.clear();
    }
    
    public static void addQueueItem(RenderQueueItem i){
        queue.add(i);
    }
    
    public static Image scaleImage(BufferedImage original, int newWidth, int newHeight){
        return original.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_FAST);
    }
    public static void render(RenderQueueItem i) {
        i.draw();       
    }        
}
