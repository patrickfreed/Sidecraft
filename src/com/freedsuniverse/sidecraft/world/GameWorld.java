package com.freedsuniverse.sidecraft.world;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import com.freedsuniverse.sidecraft.Main;
import com.freedsuniverse.sidecraft.Settings;
import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.engine.RenderQueueItem;
import com.freedsuniverse.sidecraft.entity.Entity;
import com.freedsuniverse.sidecraft.entity.Player;
import com.freedsuniverse.sidecraft.entity.debug.DebugEntity;
import com.freedsuniverse.sidecraft.input.Key;
import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.world.gen.FlatNoiseGen;
import com.freedsuniverse.sidecraft.world.gen.WorldGen;

public class GameWorld extends World {
    private final static int LIGHTMAP_DIMENSION = 32 * 60;
    private final static int UPDATE_RANGE = 32;
    private final static long DAY_LENGTH = 48000;

    private HashMap<String, Chunk> chunks;

    private String name;

    // codename_B's noise gen
    private WorldGen gen;
    private HashMap<String, Block> blocks;
    private HashMap<String, ArrayList<Entity>> es;

    private Location lightLoc;

    @SuppressWarnings("unused")
    private int nextId;
    private int stage;
    private int day;

    private BufferedImage lightMap;
    private BufferedImage map;

    private boolean uLight;
    private boolean drawMap;
    private boolean renderLight;

    private long lastMorning;

    private double[] astages;

    private ArrayList<Location> path;
    private ArrayList<Entity> removeQueue;
    private ArrayList<Entity> toRegister;
    private ArrayList<Entity> toCreate;

    private Player p;

    public static GameWorld load(File f) throws IOException {
        return null;
    }

    public GameWorld(String n) {
        this(n, new HashMap<String, Block>(), new HashMap<String, ArrayList<Entity>>(), new FlatNoiseGen());
    }

    public GameWorld(String n, HashMap<String, Block> blocks, HashMap<String, ArrayList<Entity>> es, WorldGen g) {
        super(new Vec2(0.0f, -10.0f));
        this.name = n;
        this.es = es;
        this.blocks = blocks;
        this.gen = g;
        this.nextId = -1;
        this.astages = new double[] { 0.25, 0.3, 0.45, 0.7, 1.0, 1.0, 1.0, 1.0, 1.0, 0.8, 0.6, 0.4, 0.2, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1 };
        this.stage = 0;
        this.lightLoc = new Location(-20, 12, this.getName());
        this.lightMap = getBlankLightMap();
        this.uLight = true;
        this.renderLight = false;
        this.lastMorning = System.currentTimeMillis();
        this.day = 1;
        this.drawMap = false;
        this.removeQueue = new ArrayList<Entity>();
        this.toRegister = new ArrayList<Entity>();
        this.toCreate = new ArrayList<Entity>();
        this.setContactListener(new CollisionListener());
        System.out.println("Day " + day + " dawns.");
    }

    public void update() {       
        if (p == null) {
            System.err.println("World  '" + this.getName() + "' is being updated without a player.");
            return;
        }
        
        Location l = p.getLocation();

        long time = System.currentTimeMillis() - lastMorning;
        int stage1 = stage;
        stage = (int) (((double) time / (double) GameWorld.DAY_LENGTH) * astages.length);

        if (stage >= astages.length) {
            lastMorning = System.currentTimeMillis();
            day++;
            System.out.println("Day " + day + " dawns.");
            stage = 0;
        }

        if (stage1 != stage) {
            lightUpdate();
        }

        if (Key.M.toggled()) {
            drawMap = !drawMap;

            if (drawMap) {
                map = getMap();
            }
        }

        if (Key.B.toggled()) {
            new DebugEntity(true, 45).spawn(l.modify(1, 4));
            // new Pig().spawn(p.getLocation().modify(1, 2));
            // lightUpdate();
            // path = this.getPath(p.getLocation(),
            // Location.valueOf(Mouse.getX(), Mouse.getY()));
        }

        if (Key.F.toggled()) {
            renderLight = !renderLight;
        }

        if (!lighting()) {
            double xdist = Math.abs(l.getX() - lightLoc.getX());
            double ydist = Math.abs(l.getY() - lightLoc.getY());

            if (xdist * Settings.BLOCK_SIZE >= LIGHTMAP_DIMENSION - Main.getPaneWidth() || xdist <= 13 || ydist <= 13 || ydist * Settings.BLOCK_SIZE >= LIGHTMAP_DIMENSION - Main.getPaneWidth()) {
                lightUpdate();
            }
        }
        
        generateWorld((int) l.getX(), (int) l.getY());
        step(1.0f / Settings.REFRESH_RATE, 3, 3);
        updateBlocks();
        updateEntities();
        uLight = false;
    }

    public void lightUpdate() {
        uLight = true;
        lightLoc = p.getLocation().modify(-LIGHTMAP_DIMENSION / (Settings.BLOCK_SIZE * 2), LIGHTMAP_DIMENSION / (Settings.BLOCK_SIZE * 4));
        lightLoc.setX(Math.floor(lightLoc.getX()));
        lightLoc.setY(Math.ceil(lightLoc.getY()));
        // lightMap = this.getBlankLightMap();
    }

    private void updateBlocks() {
        if (!p.getLocation().getWorld().getName().equals(this.getName()))
            return;

        for (int x = 0; x < UPDATE_RANGE; x++) {
            for (int y = 0; y < UPDATE_RANGE; y++) {
                getBlockAt((int) (p.getLocation().getX() - UPDATE_RANGE / 2) + x, (int) (p.getLocation().getY() - UPDATE_RANGE / 2) + y).update();
            }
        }
    }

    public void updateEntities() {
        ArrayList<Entity> changed = new ArrayList<Entity>();

        for (Entity e : toRegister) {
            registerEntity(e);
        }

        // no longer necessary for blocks, will be necessary for entities later. 
        // preRegister is going to need some major tweaking
        for (Entity e : toCreate) {
            Body body = this.createBody(e.getBd());

            FixtureDef fd = e.getFd();

            body.createFixture(fd);
            e.setBody(body);
            body.setUserData(e);
            System.out.println(body);
        }

        toCreate.clear();
        toRegister.clear();

        for (String s : es.keySet()) {
            for (int x = 0; x < es.get(s).size(); x++) {
                Entity e = es.get(s).get(x);
                e.update();
                if (!e.getLocation().getId().equals(s)) {
                    changed.add(e);
                    es.get(s).remove(x);
                }
            }
        }

        for (int x = 0; x < changed.size(); x++) {
            Entity e = changed.get(x);
            String id = e.getLocation().getId();

            if (!es.containsKey(id))
                es.put(id, new ArrayList<Entity>());

            es.get(e.getLocation().getId()).add(e);
        }

        for (Entity e : removeQueue) {
            removeEntity(e);
        }

        removeQueue.clear();
    }

    public void draw() {
        Iterator<Entity> i2 = getNearbyEntities(p.getLocation(), 15).iterator();

        ArrayList<Block> bs = getNearbyBlocks(p.getLocation().modify(0, 0), 16);

        for (Block b : bs) {
            b.draw();
        }

        while (i2.hasNext()) {
            Entity toDraw = i2.next();
            toDraw.draw();
        }

        if (path != null) {
            for (int c = 0; c < path.size(); c++) {
                if (c == 0) {
                    Engine.render(path.get(c), Material.GOLD_ORE.getImage());
                } else if (c == path.size() - 1) {
                    Engine.render(path.get(c), Material.IRON_ORE.getImage());
                } else {
                    Engine.render(path.get(c), Material.OBSIDIAN.getImage());
                }
            }
        }

        if (renderLight) {
            Engine.addQueueItem(new RenderQueueItem(lightLoc, lightMap));
        }

        if (drawMap) {
            Engine.addQueueItem(new RenderQueueItem(new Rectangle(0, 0, Main.getPaneWidth(), Main.getPaneHeight()), Color.blue));
            Engine.addQueueItem(new RenderQueueItem(0, 0, map));
        }
    }

    public void setBlockAt(Location loc, Block b) {
        if (b == null || loc == null || !loc.getWorldName().equals(this.getName()))
            return;

        int x = (int) Math.round(loc.getX());
        int y = (int) Math.round(loc.getY());
        String key = x + "," + y;

        if (blocks.get(key) != null) {
            Body body = blocks.get(key).getBody();
            this.destroyBody(body);
        }

        b.setLocation(loc);
        blocks.put(key, b);

        BodyDef bd = b.getBd();
        bd.position = new Vec2(x, y);

        Body body = this.createBody(bd);

        FixtureDef fd = b.getFd();

        // World is locked if body is null
        if (body == null) {
            toCreate.add(b);
        } else {
            body.createFixture(fd);
            b.setBody(body);
            body.setUserData(b);
        }
    }

    private BufferedImage getBlankLightMap() {
        return LightMap.getBlankLightMap(this, lightLoc, stage);
    }

    public int getSeed() {
        return gen.getSeed();
    }

    public String getName() {
        return name;
    }

    public BufferedImage getLightMap() {
        return lightMap;
    }

    public Block getBlockAt(double x, double y) {
        return getBlockAt(new Location(x, y, this.getName()));
    }

    public Block getBlockAt(Location l) {
        if (l == null)
            return null;

        int x = (int) Math.round(l.getX());
        int y = (int) Math.round(l.getY());

        String id = l.getId();

        Block b = blocks.get(id);

        if (b == null) {
            setBlockAt(l, new Block(gen.getBlock(this, x, y)));
        }

        return blocks.get(id);
    }

    public int oreAmountAround(int x, int y) {
        int oreCount = 0;
        Block left = getBlockAt(x - 1, y);
        if (left != null && Material.isOre(left.getType()))
            oreCount++;
        Block right = getBlockAt(x + 1, y);
        if (right != null && Material.isOre(right.getType()))
            oreCount++;
        ;
        Block top = getBlockAt(x, y + 1);
        if (top != null && Material.isOre(top.getType()))
            oreCount++;
        Block bottom = getBlockAt(x, y - 1);
        if (bottom != null && Material.isOre(bottom.getType()))
            oreCount++;
        return oreCount;
    }

    public void generateWorld(int x0, int y0) {
        for (int y = -UPDATE_RANGE; y < UPDATE_RANGE; y++) {
            for (int x = -UPDATE_RANGE; x < UPDATE_RANGE; x++) {
                getBlockAt(x0 + x, y0 + y);
            }
        }
    }

    public void unregisterEntity(Entity e) {
        removeQueue.add(e);
    }

    public void removeEntity(Entity e) {
        try {
            this.destroyBody(e.getBody());
            es.get(e.getLocation().getId()).remove(e);
        } catch (Exception est) {
            est.printStackTrace();
            System.out.println("--------");
            System.out.println(e.getLocation().getId());
        }
    }

    public Body preRegisterEntity(Entity e) {
        Body b = this.createBody(e.getBd());
        b.createFixture(e.getFd());

        toRegister.add(e);
        b.setUserData(e);

        if (e instanceof Player)
            this.p = (Player) e;

        return b;
    }

    public void registerEntity(Entity e) {
        String id = e.getLocation().getId();

        if (!es.containsKey(id))
            es.put(id, new ArrayList<Entity>());

        es.get(e.getLocation().getId()).add(e);
    }

    public void save() throws IOException {
        File dir = new File(Settings.defaultDirectory() + Main.getGame().getName() + File.separator);
        File worldDir = new File(dir.getCanonicalFile() + File.separator + "world_" + getName() + File.separator);
        File dataDir = new File(worldDir.getCanonicalFile() + File.separator + "data" + File.separator);
        File main = new File(dir.getCanonicalFile() + File.separator + "level.txt");

        if (!dir.exists()) {
            dir.mkdir();
        }

        if (!worldDir.exists()) {
            worldDir.mkdir();
        }

        if (!dataDir.exists()) {
            dataDir.mkdir();
        }

        for (String s : chunks.keySet()) {
            File c = new File(dataDir.getCanonicalFile() + File.separator + s + ".txt");
            Chunk chunk = chunks.get(s);
            Main.write(c, chunk.saveData());
        }

        Main.write(main, getName() + ":" + String.valueOf(gen.getSeed()));
    }

    public String toString() {
        return "World:{" + getName() + "," + getSeed() + "}";
    }

    public ArrayList<Block> getNearbyBlocks(Location loc, int radius) {
        ArrayList<Block> val = new ArrayList<Block>();

        int x1 = (int) Math.floor(loc.getX());
        int y1 = (int) Math.ceil(loc.getY());

        for (int x = 0; x < radius * 2 + 1; x++) {
            for (int y = 0; y < radius * 2 + 1; y++) {
                Location l = new Location(x1 - radius + x, (y1 - radius + y));
                val.add(getBlockAt(l));
            }
        }

        return val;
    }

    public ArrayList<Entity> getNearbyEntities(Location location, int radius) {
        ArrayList<Entity> val = new ArrayList<Entity>();

        int x1 = (int) Math.floor(location.getX());
        int y1 = (int) Math.ceil(location.getY());

        for (int x = 0; x < radius * 2; x++) {
            for (int y = 0; y < radius * 2; y++) {
                String loc = (x1 - radius + x) + "," + (y1 - radius + y);
                if (es.containsKey(loc)) {
                    ArrayList<Entity> e = es.get(loc);
                    for (Entity e1 : e) {
                        val.add(e1);
                    }
                }
            }
        }
        return val;
    }

    public boolean lighting() {
        return uLight;
    }

    public double getLightLevel(int x, int y) {
        int[] map = lightLoc.toArray();
        int xd = x - map[0];
        int yd = y - map[1];

        int alpha = lightMap.getRGB(xd, yd) >> 24 & 0xFF;

        return 1 - (double) alpha / 255;
    }

    public double getLightLevel(Location l) {
        // TODO: implement LightMap.class

        int[] coords = l.toArray();

        return getLightLevel(coords[0], coords[1]);
    }

    public BufferedImage getMap() {
        BufferedImage i = new BufferedImage(Main.getPaneWidth(), Main.getPaneHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = i.getGraphics();
        int r = 100;

        int size = i.getWidth() / r;
        Location l = p.getLocation().modify(-r / 2, r / 4);
        l.setX(Math.floor(l.getX()));
        l.setY(Math.ceil(l.getY()));
        
        HashMap<Material, Image> scaled = new HashMap<Material, Image>();
        Material[] ms = Material.values();
        
        for (Material m : ms) {
            scaled.put(m, Engine.scaleImage(m.getImage(), size, size));
        }

        for (int x = 0; x < r; x++) {
            for (int y = 0; y < r; y++) {
                Location l2 = l.modify(x, -y);
                Block b = getBlockAt(l2);

                g.drawImage(scaled.get(b.getType()), x * size, y * size, null);
            }
        }

        g.setColor(Color.red);
        g.fillRect(r / 2 * size, r / 4 * size - size, size, size * 2);

        return i;
    }

    public ArrayList<Location> getPath(Location s, Location gl) {
        Location upperBound;

        Location s1 = getBlockAt(s).getLocation();

        int size = 75;
        int left = 0, top = 0;

        if (s1.getX() < gl.getX()) {
            left = (int) s1.getX() - 15;
        } else {
            left = (int) gl.getX() - 15;
        }

        if (s1.getY() < gl.getY()) {
            top = (int) (Math.ceil(gl.getY()) + 15);
        } else {
            top = (int) ((s1.getY()) + 15);
        }

        upperBound = new Location(left, top, this.getName());

        int start = (int) (s1.getX() - left) + (int) (Math.abs((s1.getY()) - top)) * size;
        int goal = (int) (gl.getX() - left) + (int) (Math.abs((gl.getY()) - top)) * size;

        ArrayList<Integer> open = new ArrayList<Integer>();
        ArrayList<Integer> closed = new ArrayList<Integer>();

        int[] m = new int[size * size];
        int[] g = new int[size * size];
        int[] f = new int[size * size];
        int[] p = new int[size * size];

        for (int c = 0; c < p.length; c++) {
            p[c] = -1;
        }

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Location l = upperBound.modify(x, -y);
                int index = (int) (l.getX() - left) + (int) (Math.abs((l.getY()) - top)) * size;

                if (index == start) {
                    m[index] = 3;
                } else if (index == goal) {
                    m[index] = 2;
                } else if (getBlockAt(l).getType().isSolid()) {
                    m[index] = 1;
                } else {
                    m[index] = 0;
                }
            }
        }

        open.add(start);
        g[start] = 0;
        f[start] = g[start] + hEstimate(start, goal, size);

        while (open.size() != 0) {
            int current = 0;
            int low = Integer.MAX_VALUE;

            for (int node : open) {
                if (node < low) {
                    low = node;
                }
            }
            current = low;

            if (current == goal) {
                return reconstructPath(start, goal, p, upperBound);
            }

            open.remove(new Integer(current));
            closed.add(new Integer(current));

            for (int n : getNeighbors(current, m)) {
                int gscore = g[current] + distance(current, n, size);

                if (closed.contains(n) && gscore >= g[n]) {
                    continue;
                }

                if (!open.contains(n) || gscore < g[n]) {
                    p[n] = current;
                    g[n] = gscore;
                    f[n] = g[n] + hEstimate(n, goal, size);

                    if (!open.contains(n)) {
                        open.add(n);
                    }
                }
            }
        }

        return null;
    }

    private Location convertLocation(int i, int size, Location ub) {
        int x = (int) (i % size + ub.getX());
        int y = (int) ((ub.getY()) - (i / size));

        return new Location(x, y, this.getName());
    }

    private ArrayList<Location> reconstructPath(int start, int end, int[] p, Location u) {
        ArrayList<Location> locs = new ArrayList<Location>();
        int size = (int) Math.sqrt(p.length);
        int cnode = end;

        do {
            locs.add(convertLocation(p[cnode], size, u));
            cnode = p[cnode];
        } while (cnode != start);

        return locs;
    }

    private int distance(int p1, int p2, int size) {
        int diff = Math.abs(p1 - p2);

        if (diff == size + 1 || diff == size - 1) {
            return 14;
        } else if (diff == 1 || diff == size) {
            return 10;
        } else {
            return 0;
        }
    }

    private ArrayList<Integer> getNeighbors(int current, int[] m) {
        ArrayList<Integer> poss = new ArrayList<Integer>();
        ArrayList<Integer> good = new ArrayList<Integer>();

        int size = (int) Math.sqrt(m.length);
        int x = (int) (current % size);

        if (x != size - 1) {
            poss.add(current + 1);
        }
        if (x != 0) {
            poss.add(current - 1);
        }
        poss.add(current - size);
        poss.add(current + size);

        for (int c = 0; c < poss.size(); c++) {
            int t = poss.get(c);

            if (t >= 0 && t < m.length) {
                if (m[t] != 1) {
                    good.add(t);
                }

            }
        }

        return good;
    }

    private int hEstimate(int start, int goal, int i) {
        int x1 = start % i;
        int y1 = start / i;

        int x2 = goal % i;
        int y2 = goal / i;

        int h = Math.abs(((x2 - x1) + (y2 - y1)) * 10);

        return h;
    }

    public Location getLightMapLocation() {
        return lightLoc;
    }

    public Player getPlayer() {
        return p;
    }
}