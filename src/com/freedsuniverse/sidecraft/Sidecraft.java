package com.freedsuniverse.sidecraft;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.entity.Player;
import com.freedsuniverse.sidecraft.input.Key;
import com.freedsuniverse.sidecraft.input.Mouse;
import com.freedsuniverse.sidecraft.material.CraftingRecipe;
import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.screen.Paused;
import com.freedsuniverse.sidecraft.screen.Screen;
import com.freedsuniverse.sidecraft.world.GameWorld;
import com.freedsuniverse.sidecraft.world.Location;

public class Sidecraft extends Screen {

    private static final long serialVersionUID = 1L;

    private static Rectangle center;

    private static Graphics graphics;

    private Player player;

    private int frames;
    private int ticks;
    private int lastFps;

    private long lastTime;
    private long lastTime1;

    private boolean isRunning;
    private boolean isPaused;
    private boolean hasStarted;

    private double unprocessed;

    private HashMap<String, GameWorld> worlds;

    private Image dbImage;
    private Graphics dbg;

    public Sidecraft(int width, int height) {
        this.setBounds(0, 0, width, height);

        center = new Rectangle(width / 2 + Settings.BLOCK_SIZE / 2, height / 2 + Settings.BLOCK_SIZE / 2, Settings.BLOCK_SIZE, Settings.BLOCK_SIZE);
        isPaused = false;
        worlds = new HashMap<String, GameWorld>();

        setBackground(Color.DARK_GRAY);
    }

    public void start() {
        if (isRunning) {
            return;
        }

        graphics = getGraphics();

        this.requestFocus();
        this.isRunning = true;
        this.lastTime = 0;

        CraftingRecipe.recipes.add(new CraftingRecipe(new int[] { 2, 2, -1, -1, -1, -1, -1, -1, -1 }, Material.IRON_ORE, 16));

        lastTime = System.nanoTime();
        lastTime1 = System.currentTimeMillis();
        
        Timer t = new Timer(2, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tick();
            }
        });
        
        this.requestFocus();
        t.start();
    }

    public void startNewGame(String name) {
        setName(name);

        // music.play();

        GameWorld world = new GameWorld(name);
        world.generateWorld(0, 0);
        worlds.put(world.getName(), world);

        player = new Player();
        player.spawn(name);
        Location.setPlayerLocation(player.getLocation());
        hasStarted = true;

        start();
    }

    public void load(String name, ArrayList<GameWorld> ws, Player p) {
        this.setName(name);

        for (GameWorld w : ws) {
            this.worlds.put(w.getName(), w);
        }

        player = p;
        update();

        hasStarted = true;
    }

    public void tick() {
        double nsPerTick = 1000000000.0 / Settings.REFRESH_RATE;
        long now = System.nanoTime();
        unprocessed += (now - lastTime) / nsPerTick;
        lastTime = now;

        boolean shouldRender = false;

        while (unprocessed >= 1) {
            ticks++;
            update();
            unprocessed -= 1;
            shouldRender = true;
        }

        if (shouldRender) {
            frames++;
            repaint();
        }

        if (System.currentTimeMillis() - lastTime1 > 1000) {
            lastTime1 = System.currentTimeMillis();

            if (Settings.DEBUG) {
                System.out.println(ticks + " ticks, " + frames + " fps");
                lastFps = frames;
            }

            frames = 0;
            ticks = 0;
        }
        lastTime = now;
    }

    public void update() {
        if (Key.ESCAPE.toggled()) {
            pause();
        }
        
        updateKeys();
        Mouse.update();

        if (!isPaused) {
            if (Key.F5.toggled()) {
                Settings.DEBUG = !Settings.DEBUG;
            }
            worlds.values().iterator().next().update();
        }
    }

    private void updateKeys() {
        // TODO: put into hashmap/array where index = VK code
        Key.W.update();
        Key.A.update();
        Key.S.update();
        Key.D.update();
        Key.B.update();
        Key.F.update();
        Key.M.update();
        Key.F5.update();
        Key.SPACE.update();
        Key.I.update();
        Key.ONE.update();
        Key.TWO.update();
        Key.THREE.update();
        Key.FOUR.update();
        Key.FIVE.update();
        Key.ESCAPE.update();
    }

    @Override
    public void paint(Graphics g) {
        if (dbImage == null) {
            dbImage = createImage(this.getWidth(), this.getHeight());
            dbg = dbImage.getGraphics();
        }

        int topOfScreen = (int) ((player.getLocation().getY() + 10.0) * Settings.BLOCK_SIZE);

        for (int y = 0; y < this.getHeight(); y++) {
            dbg.setColor(getSkyColor(topOfScreen - y));
            dbg.fillRect(0, y, this.getWidth(), 1);
        }

        Sidecraft.graphics = dbg;

        if (hasStarted) {
            player.getLocation().getWorld().draw();
            player.draw();
            Engine.drawQueue();
            drawMisc();
        }

        g.drawImage(dbImage, 0, 0, this);
    }

    private Color getSkyColor(int y) {
        double bound = Settings.BLOCK_SIZE * 8;

        if (y == 0) {
            return Color.WHITE;
        } else if (y > 0) {
            if (y >= bound) {
                return Color.BLUE;
            } else {
                return new Color(255 - (int) (y / bound * 255.0), 255 - (int) (y / bound * 255.0), 255);
            }
        } else {
            if (y <= -bound) {
                return Color.BLACK;
            } else {
                return new Color(255 + (int) (y / bound * 255.0), 255 + (int) (y / bound * 255.0), 255 + (int) (y / bound * 255.0));
            }
        }
    }

    private void drawMisc() {
        Engine.renderString(Settings.VERSION, 0, 10, Color.WHITE);

        if (Settings.DEBUG) {
            Engine.renderString(player.getLocation().toString(), 0, 25, Color.white);
            Engine.renderString(String.valueOf(lastFps + " fps"), 0, 35, Color.white);
            Engine.renderString("0", new Location(0, 0), Color.white);

            Location l = Mouse.getLocation();
            Location playerLoc = player.getLocation();
            double xd = playerLoc.getX() - l.getX();
            double yd = playerLoc.getY() - l.getY();

            Engine.renderString(xd + "," + yd, playerLoc.modify(1, 3), Color.black);
        }
    }

    public void pause() {
        if (isPaused) {
            isPaused = false;
            Main.setScreen(Sidecraft.class.getName());
            this.requestFocus();
        } else {
            Key.releaseAll();
            Main.setScreen(Paused.class.getName());
            isPaused = true;
        }
    }

    public void stop() {
        isPaused = false;
        isRunning = false;
    }

    public int getFps() {
        return lastFps;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public HashMap<String, GameWorld> getWorlds() {
        return worlds;
    }

    public static Rectangle getCenterBound() {
        return center;
    }

    public static Graphics graphics() {
        return Sidecraft.graphics;
    }

    public static BufferedImage getImage(String file) {
        try {
            return ImageIO.read(Sidecraft.class.getResourceAsStream(file));
        } catch (IOException e) {
            return null;
        }
    }

}