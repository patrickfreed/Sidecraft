package com.freedsuniverse.sidecraft.material;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

import com.freedsuniverse.sidecraft.Main;
import com.freedsuniverse.sidecraft.Sound;

public enum Material {
    AIR(0, 0, 0, 0, 0, false), 
    GRASS(2), 
    DIRT(2), 
    STONE(3, 40), 
    IRON_ORE(4, 45), 
    OBSIDIAN(5, 100), 
    SAND(6, 10), 
    COAL_ORE(7, 40), 
    SILVER_ORE(8, 60), 
    GOLD_ORE(9, 45), 
    WATER(0, 0, 0, 0, 0, false), 
    TNT(0, 1), 
    WORKBENCH(12), 
    PICKAXE(13), 
    TORCH(14, 1, false);

    public static final int LIQUID = 0;
    public static final int SEDIMENT = 1;
    public static final int ROCK = 2;

    private int id;
    private int stack;
    private int damage;
    private int durability;
    private int dropamount;
    private int dropType;

    private boolean solidity;

    public static LinkedList<Integer> rock, sediment, glass;

    public static Sound getSound(int id) {
        if (rock == null) {
            Material[] values = Material.values();
            rock = sediment = glass = new LinkedList<Integer>();

            for (int x = 0; x < values.length; x++) {
                if (values[x].toString().contains("ORE") || values[x].toString().contains("STONE")) {
                    Material.rock.add(values[x].getId());
                } else {
                    Material.sediment.add(values[x].getId());
                }
            }
        }

        if (rock.contains(id)) {
            return Sound.sedimentWalk;
        } else if (sediment.contains(id)) {
            return Sound.sedimentWalk;
        } else if (glass.contains(id)) {

        }
        return Sound.sedimentWalk;
    }

    public static boolean isOre(Material mat) {
        return mat == Material.COAL_ORE || mat == Material.SILVER_ORE || mat == Material.GOLD_ORE;
    }

    private Material(int drpType) {
        this(drpType, 4);
    }

    private Material(int drpType, int dur) {
        this(drpType, dur, 64, 1, 1, true);
    }

    private Material(int drpType, int dur, boolean solid) {
        this(drpType, dur, 64, 1, 1, solid);
    }

    private Material(int drpType, int dur, int stack, int dmg, int drp, boolean solid) {
        this.id = this.ordinal();
        this.stack = stack;
        this.damage = dmg;
        this.durability = dur;
        this.dropamount = drp;
        this.dropType = drpType;
        this.solidity = solid;
    }

    public int getId() {
        return id;
    }

    public int getMaxStackSize() {
        return stack;
    }

    public int getDamage() {
        return damage;
    }

    public int getDurability() {
        return durability;
    }

    public int getMaterialType() {
        if (durability >= Material.STONE.getDurability()) {
            return ROCK;
        } else if (durability > 0) {
            return SEDIMENT;
        } else {
            return LIQUID;
        }
    }

    public int getDropAmount() {
        return dropamount;
    }

    public Material getDropType() {
        return Material.values()[dropType];
    }

    public BufferedImage getImage() {
        return Main.getTexture(id);
    }

    public Sound getDamageSound() {
        return Sound.blockDamage;
    }

    public boolean isSolid() {
        return solidity;
    }
}
