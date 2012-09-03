package com.freedsuniverse.sidecraft.material;

import java.awt.image.BufferedImage;

import com.freedsuniverse.sidecraft.Sidecraft;

public enum Material{
    AIR(0, 0, 0, 0, 0, false),
    GRASS(2),
    DIRT(2),
    STONE(3, 45),
    IRON_ORE(4, 45),
    OBSIDIAN(5, 100),
    SAND(6, 10),
    COAL_ORE(7, 40),
    SILVER_ORE(8, 60),
    GOLD_ORE(9, 45),
    WATER(0, 0, 0, 0, 0, false),
    TNT(0),
    WORKBENCH(12, 45);
    
    private int id, stack, damage, durability, dropamount, dropType;
    private BufferedImage img;
    private boolean solidity;
    
    private Material(int drpType){
        this(drpType, 15);
    }
    
    private Material(int drpType, int dur){
        this(drpType, dur, 64, 1, 1, true);
    }
    
    private Material(int drpType, int dur, int stack, int dmg, int drp, boolean solid){
        this.id = this.ordinal();
        this.img = Sidecraft.textures.get(id);
        this.stack = stack;
        this.damage = dmg;
        this.durability = dur;
        this.dropamount = drp;
        this.dropType = drpType;
        this.solidity = solid;
    }
    
    public int getId(){
        return id;
    }
    
    public int getMaxStackSize(){
        return stack;
    }
    
    public int getDamage(){
        return damage;
    }
    
    public int getDurability(){
        return durability;
    }
    
    public int getDropAmount(){
        return dropamount;
    }
    
    public Material getDropType(){
        return Material.values()[dropType];
    }
    
    public BufferedImage getImage(){
        return img;
    }
    
    public boolean isSolid(){
        return solidity;
    }
}
