package com.freedsuniverse.sidecraft.entity;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.freedsuniverse.sidecraft.Main;
import com.freedsuniverse.sidecraft.Settings;
import com.freedsuniverse.sidecraft.engine.Animation;
import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.inventory.Inventory;
import com.freedsuniverse.sidecraft.material.Item;
import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.material.MaterialStack;
import com.freedsuniverse.sidecraft.world.Location;

public class LivingEntity extends Entity{

    private final double SPACE = 0.25;
    
    protected int yDirection, xDirection, ySpeed, xSpeed;
    
    private static final double STEP_LENGTH = 3.0;
    private static final long DAMAGE_TIME = 500, RECENT_DAMAGE_TIME = 4000;
    protected String id;
    
    private Animation anime;
    private int health, recentdmg;
    private Inventory inv;
    private double spaceMoved = 0;   
    private boolean blocked = false, damaged = false;
    private long dmg = -1;
    private BufferedImage red;
    
    public LivingEntity(String id, int w, int h, int health) {
        anime = new Animation(Animation.read("/entity/" + id + ".png", w, h), 10);
        inv = new Inventory();
        this.id = id;
        setHealth(health);
    }
     
    public void draw() { 
        if(damaged) {
            if(System.currentTimeMillis() - dmg <= DAMAGE_TIME) {
                
                Engine.render(getLocation(), Main.getTint(anime.getSlide(), Color.red, 0));
            }else {
                Engine.render(getLocation(), anime.getSlide());
                damaged = false;      
            }
        }else {
            Engine.render(getLocation(), anime.getSlide());
        }
        
        if(recentdmg > 0) {
            Engine.renderString("" + recentdmg, getLocation().modify(0, 0.4), Color.white);
            
            if(System.currentTimeMillis() - dmg >= RECENT_DAMAGE_TIME) {
                recentdmg = 0;
                System.out.println("has been more than " + (RECENT_DAMAGE_TIME / 1000) + "s since last damage");
            }
        }
        
        String health = "";
        
        int c = 0;
        while(c < getHealth()) {
            health += "-";
            c++;
        }
        
        Engine.renderString(health, getLocation().modify(0, 0.25), Color.green);
    }
    
    @Override
    public BufferedImage getSkin() {
        return anime.getSlide();
    }
    
    private void updateCollision() {
        int width = getBounds().width;
        double yMod = -getBounds().height / 32;
        ArrayList<Material> check = new ArrayList<Material>();
        
        for(double x = 0.0; x * 32.0 < width; x += 0.5) {
            check.add(getLocation().getWorld().getBlockAt(getLocation().modify(x, yMod)).getType());
        }
        
        for(Material m:check) {
            if(!m.isSolid()) {
                yDirection = -1;
                ySpeed = 1;
            }else {
                ySpeed = 0;
            }
        }
        
        if(xSpeed > 0) {
            check = new ArrayList<Material>();
            if(xDirection == Settings.RIGHT) {
                double xMod = getBounds().width / 32.0 + SPACE;
                
                for(double x = 0.0; x * 32.0 < getBounds().height; x += 0.5) {
                    check.add(getLocation().getWorld().getBlockAt(getLocation().modify(xMod, -x)).getType());
                }
            }else {
                for(double x = 0.0; x * 32.0 < getBounds().height; x += 0.5) {
                    check.add(getLocation().getWorld().getBlockAt(getLocation().modify(-SPACE, -x)).getType());
                } 
            }
            for(Material m:check) {
                if(m.isSolid()) {
                    xSpeed = 0;
                    blocked = true;
                }else {
                    blocked = false;
                }
            }
        }
    }
    
    public boolean isBlocked() {
        return blocked;
    }
    
    public void update() {
        updateCollision();
        updatePosition(xDirection, yDirection, xSpeed, ySpeed);
        anime.update();
    }
   
    public void setHealth(int h) {
        health = h;
    }
    
    public void damage(int d){
        health -= d;
        recentdmg += d;
        dmg = System.currentTimeMillis();
        damaged = true;
        if(health <= 0) {
            destroy();
        }
    }

    public void spawn(Location l) {
        setLocation(l);
        id += l.getWorld().registerEntity(this);
    }
    
    @Override
    public void destroy() {
        getLocation().getWorld().unregisterEntity(this);
        
        Item[][] contents = getInventory().getContents();
        
        for(int x = 0; x < contents.length; x++) {
            for(int y = 0; y < contents[0].length; y++) {
                if(contents[x][y] != null && contents[x][y] instanceof MaterialStack) {
                    MaterialStack t = (MaterialStack) contents[x][y];
                    DropEntity e = new DropEntity(t.getType(), getLocation());
                    e.spawn();
                }
            }
        }
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

    public int getHealth() {
        return health;
    }

    public Inventory getInventory() {
        return inv;
    }
    
    @Override
    public String toString() {
        return getLocation() + "\n" + getInventory();  
    }
}
