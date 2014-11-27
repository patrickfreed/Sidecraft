package com.freedsuniverse.sidecraft.world;

import java.util.ArrayList;

import com.freedsuniverse.sidecraft.entity.Entity;

public class Chunk {
    public final static int SIZE = 32;
    
    private final int xmod;
    private final int ymod;
    
    private Block[][] bData;
    
    private ArrayList<Entity> eData;

    public Chunk(String xy) {
        this(Integer.valueOf(xy.charAt(0)), Integer.valueOf(xy.charAt(2)));
    }

    public Chunk(int x, int y) {
        this(new Block[SIZE][SIZE], new ArrayList<Entity>(), x, y);
    }

    public Chunk(Block[][] b, ArrayList<Entity> e, int x, int y) {
        bData = b;
        eData = e;
        xmod = x;
        ymod = y;
    }

    public void draw() {
        for (int x = 0; x < bData.length; x++) {
            for (int y = 0; y < bData[0].length; y++) {
                bData[x][y].draw();
            }
        }

        for (Entity e1 : eData) {
            e1.draw();
        }
    }

    public void update() {
        for (int x = 0; x < bData.length; x++) {
            for (int y = 0; y < bData[0].length; y++) {
                bData[x][y].update();
            }
        }

        for (Entity e1 : eData) {
            e1.update();
        }
    }

    public void addEntity(Entity e) {
        eData.add(e);
    }

    public void removeEntity(Entity e) {
        eData.add(e);
    }

    public void add(Block b) {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (bData[x][y] == null) {
                    bData[x][y] = b;
                }
            }
        }
    }

    public Block getAt(int x, int y) {
        return bData[x][y];
    }

    public void setAt(int x, int y, Block b) {
        int x1 = Math.abs(x % SIZE);
        int y1 = Math.abs(y % SIZE);

        bData[x1][y1] = b;
    }

    public Entity[] getEntities() {
        return eData.toArray(new Entity[0]);
    }

    public Block[] getBlocks() {
        Block[] r = new Block[SIZE * SIZE];
        int x = 0;

        for (Block[] b1 : bData) {
            for (Block b : b1) {
                r[x] = b;
                x++;
            }
        }

        return r;
    }

    public String saveData() {
        String s = "";

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                s += bData[x][y];
            }
        }

        s += "\n";

        for (Entity e : this.eData) {
            s += e;
        }

        return s;
    }

    public int getY() {
        return ymod;
    }

    public int getX() {
        return xmod;
    }
}
