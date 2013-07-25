package com.freedsuniverse.sidecraft.entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.freedsuniverse.sidecraft.engine.Light;
import com.freedsuniverse.sidecraft.world.Block;

public class LightSource extends Entity{    
    private int radius;
    private Light l;
   // private Light l2;
    
    public LightSource(Color c, int intensity, int r) {
        setLight(c, intensity, r);
        setSkin(getDefaultSprite());
    }
    
    public void setLight(Color c, int intensity, int r) {
        Color cnew = new Color(c.getRed(), c.getGreen(), c.getBlue(), intensity);
        setLight(new Light(cnew), r);
        //l2 = new Light(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
    }
    
    public void setLight(Light l, int r) {
        this.l = l;
        //this.l2 = new Light(l.getColor(), 100);
        radius = r;
    }
    
    private BufferedImage getDefaultSprite() {
        BufferedImage i = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics g = i.getGraphics();
        
        g.setColor(Color.white);
        g.fillOval(0, 0, 32, 32);
        
        return i;
    }
    
    public void update() {    
        if(getLocation() == null) return;
        
        ArrayList<Block> bs = getLocation().getWorld().getNearbyBlocks(getLocation(), radius);
        
        for(Block b:bs) { 
            b.light(l);
        }
    }
}
