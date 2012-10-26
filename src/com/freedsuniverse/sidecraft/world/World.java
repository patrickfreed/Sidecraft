package com.freedsuniverse.sidecraft.world;

import java.util.HashMap;
import java.util.LinkedList;

import com.freedsuniverse.sidecraft.Settings;
import com.freedsuniverse.sidecraft.Sidecraft;
import com.freedsuniverse.sidecraft.entity.Entity;
import com.freedsuniverse.sidecraft.material.Material;

public class World {
    private HashMap<String, Block> blocks;

    private LinkedList<Entity> entities;
    
    private String name;

    private int xLength;
    private int yLength;
    
    // codename_B's noise gen
    private FlatNoiseGen gen = new FlatNoiseGen();

    public World(String n) {
        this.name = n;
        this.blocks = new HashMap<String, Block>();
        this.xLength = Sidecraft.width / Settings.BLOCK_SIZE;
        this.yLength = Sidecraft.height / Settings.BLOCK_SIZE;

        entities = new LinkedList<Entity>();
        update();
    }

    public int getSeed(){
        return gen.seed;
    }
    
    public String getName() {
        return name;
    }

    public HashMap<String, Block> getBlocks() {
        return blocks;
    }

    public void setBlockAt(Location loc, Block b){
        int x = (int)Math.floor(loc.getX());
        int y = (int)Math.ceil(loc.getY());

        
        blocks.put(x + "," + y, b);
        blocks.get(x + "," + y).setLocation(loc);
    }
    
    public Block getBlockAt(Location coordinates) {
        int x = (int)Math.floor(coordinates.getX());
        int y = (int)Math.ceil(coordinates.getY());
        
        if (!blocks.containsKey(x + "," + y)) {
            blocks.put(x + "," + y, new Block(gen.getBlock(this, x, y)));
            blocks.get(x + "," + y).setLocation(coordinates);
        }

        return blocks.get(x + "," + y);
    }

    protected int oreAmountAround(int x, int y) {
        int oreCount = 0;
        Block left = blocks.get((x - 1) + "," + y);
        if (left != null && isOre(left.getType())) oreCount++;
        Block right = blocks.get((x + 1) + "," + y);
        if (right != null && isOre(right.getType())) oreCount++;;
        Block top = blocks.get(x + "," + (y + 1));
        if (top != null && isOre(top.getType())) oreCount++;
        Block bottom = blocks.get(x + "," + (y - 1));
        if (bottom != null && isOre(bottom.getType())) oreCount++;
        return oreCount;
    }

    private boolean isOre(Material mat) {
        return mat == Material.COAL_ORE || mat == Material.SILVER_ORE || mat == Material.GOLD_ORE;
    }

    public void update() {
      updateEntities();
      updateBlocks();
    }

    private void updateBlocks() {
        Location upperExtreme = new Location(Math.floor(Sidecraft.player.getLocation().getX()) + xLength / 2 + 1, Math.ceil((Sidecraft.player.getLocation().getY() + yLength / 2) + 1), this.getName());
        Location lowerExtreme = new Location(Math.floor(Sidecraft.player.getLocation().getX()) - xLength / 2 - 1, Math.ceil((Sidecraft.player.getLocation().getY() - yLength / 2) - 1), this.getName());

        int xDistance = (int)Math.abs(upperExtreme.getX() - lowerExtreme.getX());
        int yDistance = (int)Math.abs(upperExtreme.getY() - lowerExtreme.getY());

        for (int x = 0; x <= xDistance; x++) {
            for (int y = 0; y <= yDistance; y++) {
                double xCoord = upperExtreme.getX() + increment(x, (int)upperExtreme.getX(), (int)lowerExtreme.getX());
                double yCoord = upperExtreme.getY() + increment(y, (int)upperExtreme.getY(), (int)lowerExtreme.getY());

                getBlockAt(new Location(xCoord, yCoord, getName())).update();
            }
        }
        
    }

    public void generateWorld(){
        Location upperExtreme = new Location(Math.floor(Sidecraft.player.getLocation().getX()) + xLength / 2 + 1, Math.ceil((Sidecraft.player.getLocation().getY() + yLength / 2) + 1), this.getName());
        Location lowerExtreme = new Location(Math.floor(Sidecraft.player.getLocation().getX()) - xLength / 2 - 1, Math.ceil((Sidecraft.player.getLocation().getY() - yLength / 2) - 1), this.getName());
        
        int xDistance = (int)Math.abs(upperExtreme.getX() - lowerExtreme.getX());
        int yDistance = (int)Math.abs(upperExtreme.getY() - lowerExtreme.getY());
        
        for (int x = 0; x <= xDistance; x++) {
            for (int y = 0; y <= yDistance; y++) {
                double xCoord = upperExtreme.getX() + increment(x, (int)upperExtreme.getX(), (int)lowerExtreme.getX());
                double yCoord = upperExtreme.getY() + increment(y, (int)upperExtreme.getY(), (int)lowerExtreme.getY());

                getBlockAt(new Location(xCoord, yCoord, getName()));
            }
        }
    }
    
    public void updateEntities() {
        for (int x = 0; x < entities.size(); x++) {
            entities.get(x).update();
        }
    }

    public void draw() {
        Location upperExtreme = new Location(Math.floor(Sidecraft.player.getLocation().getX()) + xLength / 2 + 1, Math.ceil((Sidecraft.player.getLocation().getY() + yLength / 2) + 1), this.getName());
        Location lowerExtreme = new Location(Math.floor(Sidecraft.player.getLocation().getX()) - xLength / 2 - 1, Math.ceil((Sidecraft.player.getLocation().getY() - yLength / 2) - 1), this.getName());

        int xDistance = (int)Math.abs(upperExtreme.getX() - lowerExtreme.getX());
        int yDistance = (int)Math.abs(upperExtreme.getY() - lowerExtreme.getY());

        for (int x = 0; x <= xDistance; x++) {
            for (int y = 0; y <= yDistance; y++) {
                double xCoord = upperExtreme.getX() + increment(x, (int)upperExtreme.getX(), (int)lowerExtreme.getX());
                double yCoord = upperExtreme.getY() + increment(y, (int)upperExtreme.getY(), (int)lowerExtreme.getY());

                getBlockAt(new Location(xCoord, yCoord, getName())).draw();
            }
        }

        for (int x = 0; x < entities.size(); x++) {
            entities.get(x).draw();
        }
    }
    
    private int increment(int x, int compare1, int compare2) {
        if (compare1 > compare2) {
            return -1 * x;
        }
        else {
            return x;
        }
    }

    public void unregisterEntity(Entity e) {
        if(entities.contains(e))
            entities.remove(e);
    }

    public void registerEntity(Entity e) {
        entities.add(e);
    }
}
