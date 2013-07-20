package com.freedsuniverse.sidecraft.world;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.freedsuniverse.sidecraft.Main;
import com.freedsuniverse.sidecraft.Settings;
import com.freedsuniverse.sidecraft.Sidecraft;
import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.engine.Light;
import com.freedsuniverse.sidecraft.entity.Entity;
import com.freedsuniverse.sidecraft.entity.Sun;
import com.freedsuniverse.sidecraft.entity.animal.Pig;
import com.freedsuniverse.sidecraft.input.Key;
import com.freedsuniverse.sidecraft.material.Material;

public class World {
    private HashMap<String, Chunk> chunks;
    private String name;
    
    // codename_B's noise gen
    private FlatNoiseGen gen;
    private HashMap<String, Block> blocks;
    private HashMap<String, ArrayList<Entity>> es;
    private int nextId;
    private boolean uLight;
    private BufferedImage lighting;

    public static World load(File f) throws IOException {
//        //requirements
//        String worldName = "";
//        int seed = 0;
//        HashMap<String, Chunk> chunks = new HashMap<String, Chunk>();
//        
//        File dataDir = new File(f.getCanonicalFile() + File.separator + "data" + File.separator);
//        File main = new File(f.getCanonicalFile() + File.separator + "level.txt"); 
//        
//        String[] mainData = Main.read(main).split(":");
//        seed = Integer.valueOf(mainData[1]);
//        worldName = mainData[0];
//        
//        for(File ch:dataDir.listFiles()) {
//            String[] chunkData = Main.read(ch).split("\n");
//            String[] blocks = chunkData[0].split(";");
//            String[] entities = chunkData[1].split(";");
//            String chunkName = ch.getName();
//            
//            Chunk c = new Chunk(Integer.valueOf(chunkName.charAt(0)), Integer.valueOf(chunkName.charAt(2)));
//            
//            for(String block:blocks) {
//                String[] parts = block.split(":");
//                Material type = Material.valueOf(parts[Block.TYPE]);
//                Location loc = new Location(Double.valueOf(parts[Block.X]), Double.valueOf(parts[Block.Y]), parts[Block.WORLD]);
//                
//                Block b = new Block(type);
//                b.setLocation(loc);
//                c.add(b);
//            }
//            
//            for (String entity:entities) {
//                String[] e0 = entity.split(":");
//                
//                //TODO: move to separate method, Entity.valuOf(String n) perhaps
//                
//                if(e0[0].equals("DropEntity")) {
//                    DropEntity de = new DropEntity(Material.valueOf(e0[1]), new Location(Double.valueOf(e0[2]), Double.valueOf(e0[3]), e0[4]));
//                    c.addEntity(de);
//                }
//            }
//            chunks.put(chunkName, c);
//        }
//        return new World(worldName, chunks, new FlatNoiseGen(seed));
        return null;
    }
    
    public World(String n) {
        this(n, new HashMap<String, Block>(), new HashMap<String, ArrayList<Entity>>(), new FlatNoiseGen());
    }
    
    public World(String n, HashMap<String, Block> blocks, HashMap<String, ArrayList<Entity>> es, FlatNoiseGen g) {
        this.name = n;
        this.es = es;
        this.blocks = blocks;
        this.gen = g;
        this.nextId = -1;
        Sun s = new Sun();
        s.setLocation(new Location(0, 0, this.name));
        registerEntity(s);
    }
     
    public int getSeed(){
        return gen.seed;
    }
    
    public String getName() {
        return name;
    }

    public void setBlockAt(Location loc, Block b){
        int x = (int)Math.floor(loc.getX());
        int y = (int)Math.ceil(loc.getY());

        b.setLocation(loc);
        blocks.put(x + "," + y, b);
    }
    
    public Block getBlockAt(double x, double y) {
        return getBlockAt(new Location(x, y, this.getName()));
    }
    
    public Block getBlockAt(Location l) {
        if(l == null) return null;
        
        int x = (int) Math.floor(l.getX());     
        int y = (int) l.getY();
        
        String id = l.getId();
        
        Block b = blocks.get(id);
        
        if(b == null) {
            setBlockAt(l, new Block(gen.getBlock(this, x, y)));
        }
        
        return blocks.get(id);
    }

    protected int oreAmountAround(int x, int y) {
        int oreCount = 0;
        Block left = getBlockAt(x - 1, y);
        if (left != null && isOre(left.getType())) oreCount++;
        Block right =getBlockAt(x + 1, y);
        if (right != null && isOre(right.getType())) oreCount++;;
        Block top = getBlockAt(x, y + 1);
        if (top != null && isOre(top.getType())) oreCount++;
        Block bottom = getBlockAt(x, y - 1);
        if (bottom != null && isOre(bottom.getType())) oreCount++;
        return oreCount;
    }

    public void lightUpdate() {
        uLight = true;
    }
    
    private boolean isOre(Material mat) {
        return mat == Material.COAL_ORE || mat == Material.SILVER_ORE || mat == Material.GOLD_ORE;
    }

    public void update() {
        if(Key.B.toggled()) {
            new Pig().spawn(Main.getGame().player.getLocation().modify(1, 0));
        }
        
        updateBlocks();
        updateEntities();
    }

    private void updateBlocks() {
        if(!Main.getGame().player.getLocation().getWorld().getName().equals(this.getName())) return;

        for(int x = 0; x < 32; x++) {
            for(int y = 0; y < 32; y++) {
                getBlockAt((int) (Main.getGame().player.getLocation().getX() - 16) + x, (int) (Main.getGame().player.getLocation().getY() - 16) + y).update();
            }
        }
        
    }

    public void generateWorld(){   
        for(int y = 0; y < 50; y++) {
            for(int x = 0; x < 50; x++) {
                Block b = new Block(gen.getBlock(this, x, y));
                b.setLocation(new Location(x, y, this.getName()));
                blocks.put(x + "," + y, b);
            }
        }
    }
    
    public void updateEntities() {
        ArrayList<Entity> changed = new ArrayList<Entity>();
        
        for(String s:es.keySet()) {
            for(int x = 0; x < es.get(s).size(); x++) {
                Entity e = es.get(s).get(x);
                e.update();
                if(!e.getLocation().getId().equals(s)) {
                    //System.out.println("changed from " + s + " to " + e.getLocation().getId());
                    changed.add(e);
                    es.get(s).remove(x);
                }
            }
        }
        
        for(int x = 0; x < changed.size(); x++) {
            Entity e = changed.get(x);
            String id = e.getLocation().getId();
            
            if(!es.containsKey(id)) es.put(id, new ArrayList<Entity>());
            
            es.get(e.getLocation().getId()).add(e);
        }
    }

    public void draw() {             
        Iterator<Block> i1 = blocks.values().iterator();
        Iterator<ArrayList<Entity>> i2 = es.values().iterator();
        
        while(i1.hasNext()) {
            Block b = i1.next();
            b.draw();
        }
        
        while(i2.hasNext()) {
           
                ArrayList<Entity> toDraw = i2.next();
                for(Entity e:toDraw) {
                    e.draw();
                }
            
        }
        
        if(uLight) {
            drawLighting();
            uLight = false;
        }
        Engine.render(0, 0, lighting);
    }

    private void drawLighting() {
        BufferedImage i = new BufferedImage(Main.getPaneWidth(), Main.getPaneHeight(), BufferedImage.TYPE_INT_ARGB);
        
        int r = 25;
        
        ArrayList<Block> bs = getNearbyBlocks(Sidecraft.playerLoc, r);

        Graphics g = i.getGraphics();
        
        for(Block b:bs) {
            Light l = b.getLight();
            Color ls = l.getColor();
            Color c3 = null;
            
            if(ls == null) {
                c3 = Color.black;
            }else {
                c3 = new Color((ls.getRed() + Light.DARK.getRed()) / 2, (ls.getGreen() + Light.DARK.getGreen()) / 2, (ls.getBlue() + Light.DARK.getBlue()) / 2, 255 - ls.getAlpha());
            }
            Rectangle r1 = b.getBounds();

            g.setColor(c3);
            g.fillRect(r1.x, r1.y, 32, 32);
        }
        
        lighting = i;
    }
    
    public ArrayList<Entity> getNearbyEntities(Location l) {
        return null;
    }
    
    public void unregisterEntity(Entity e) {
        try{
            es.get(e.getLocation().getId()).remove(e);
        }catch(Exception est) {
            for(String s:es.keySet()) {
                System.out.println(s);
            }
            System.out.println("--------");
            System.out.println(e.getLocation().getId());
        }
    }

    public int registerEntity(Entity e) {
        nextId++;
        
        String id = e.getLocation().getId();
        
        if(!es.containsKey(id)) es.put(id, new ArrayList<Entity>());
        
        es.get(e.getLocation().getId()).add(e);
        
        return nextId;
    }
    
    public void save() throws IOException {
        File dir = new File(Settings.defaultDirectory() + Main.getGame().getName() + File.separator);
        File worldDir = new File(dir.getCanonicalFile() + File.separator + "world_" + getName() + File.separator);
        File dataDir = new File(worldDir.getCanonicalFile() + File.separator + "data" + File.separator);
        File main = new File(dir.getCanonicalFile() + File.separator + "level.txt"); 
        
        if(!dir.exists()) {
            dir.mkdir();
        }
        
        if(!worldDir.exists()) {
            worldDir.mkdir();
        }
        
        if(!dataDir.exists()) {
            dataDir.mkdir();
        }    
        
        for(String s:chunks.keySet()) {
            File c = new File(dataDir.getCanonicalFile() + File.separator + s + ".txt");
            Chunk chunk = chunks.get(s);
            Main.write(c, chunk.saveData());
        }
        
        Main.write(main, getName() + ":" + String.valueOf(gen.seed));
    }
    
    public String toString() {
        String s = "";
        
        s += "World:{" + getName() + "," + getSeed() + "}";
        
        return s;
    }

    public ArrayList<Block> getNearbyBlocks(Location loc, int radius){
        ArrayList<Block> val = new ArrayList<Block>();
        
        int x1 = (int)Math.floor(loc.getX());
        int y1 = (int)Math.ceil(loc.getY());
        
        for(int x = 0; x < radius * 2 + 1; x++) {
            for(int y = 0; y < radius * 2 + 1; y++) {
                Location l = new Location(x1 - radius + x, (y1 - radius + y));
                val.add(getBlockAt(l));
            }
        }
        
        return val;
    }
    
    public ArrayList<Entity> getNearbyEntities(Location location, int radius) {
        ArrayList<Entity> val = new ArrayList<Entity>();
        
        int x1 = (int)Math.floor(location.getX());
        int y1 = (int)Math.ceil(location.getY());
        
        
        for(int x = 0; x < radius * 2; x++) {
            for(int y = 0; y < radius * 2; y++) {
                String loc = (x1 - radius + x) + "," + (y1 - radius + y);
                if(es.containsKey(loc)) {
                    ArrayList<Entity> e = es.get(loc);
                    for(Entity e1:e) {
                        val.add(e1);
                    }
                }
            }
        }
        return val;
    }
}
