package com.freedsuniverse.sidecraft;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.entity.Player;
import com.freedsuniverse.sidecraft.input.InputListener;
import com.freedsuniverse.sidecraft.input.Key;
import com.freedsuniverse.sidecraft.input.Mouse;
import com.freedsuniverse.sidecraft.material.CraftingRecipe;
import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.screen.Paused;
import com.freedsuniverse.sidecraft.world.Location;
import com.freedsuniverse.sidecraft.world.World;

public class Sidecraft extends Canvas implements Runnable{
    private final int SKY_COLOR = 220;
    
    private static final long serialVersionUID = 1L;
    
    public int width, height;   
    
    public Player player;
    
    public static Location playerLoc;
    
    private int frames, lastFps;

    public long now, lastTime;
    
    public Sound music;
    
    private boolean isRunning = false, isPaused = true, hasStarted = false;
    
    public HashMap<String, World> worlds = new HashMap<String, World>();
    
    private Image dbImage;
    private Graphics dbg;
    
    public Sidecraft() {
        width = 800;
        height = 400;
        this.setBounds(0, 0, width, height);      
        
        isPaused = false;
        
        setBackground(Color.DARK_GRAY);           
        InputListener i = new InputListener();
        this.addKeyListener(i);
        this.addMouseListener(i);
        this.addMouseMotionListener(i);
        this.addMouseWheelListener(i);   
        this.requestFocus();
    }
    
    public void load(String name, ArrayList<World> ws, Player p) {
        this.setName(name);
        
        for(World w:ws) {
            this.worlds.put(w.getName(), w);
        }
        player = p;     
        update();
        
        hasStarted = true;
    }   
    
    public void start() {
        this.requestFocus();
        Engine.graphics = getGraphics();
        isRunning = true;         
        Thread th = new Thread(this);
        lastTime = 0;
        CraftingRecipe.recipes.add(new CraftingRecipe(new int[]{2, 2, -1, -1, -1, -1, -1, -1, -1}, Material.IRON_ORE, 16));
        th.start();
    }

    public void startNewGame(String name) {                        
        setName(name);
        
        //music.play();        
        
        World world = new World(name);
        world.generateWorld();
        worlds.put(world.getName(), world);    
        
        player = new Player();
        player.load(name);
        player.world = world.getName();
        playerLoc = player.getLocation();
        hasStarted = true;       
        
        update();
    }

    public void update() {
        if(hasStarted && !this.hasFocus()){
            //pause();
        }
        
        if(Key.ESCAPE.toggled()) pause();
        
        updateKeys();
        Mouse.update();
        if(!isPaused){                       
            if (Key.F5.toggled()) Settings.DEBUG = !Settings.DEBUG;
            
            player.update();
            player.getLocation().getWorld().update();   
        }
        
        now = System.currentTimeMillis();
        frames++;
        
        if(now - lastTime > 1000){
            lastFps = frames;
            frames = 0;
            lastTime = now;
        }
    }

    public void pause() {         
        if(isPaused){
            isPaused = false;
            Main.setScreen(Sidecraft.class.getName());    
            this.requestFocus();
        }else {
            Key.releaseAll();
            Main.setScreen(Paused.class.getName());
            isPaused = true;
        }
    }
    
    private void updateKeys(){
        Key.W.update();
        Key.A.update();
        Key.S.update();
        Key.D.update();
        Key.B.update();
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
        Engine.graphics = g;           
        if(hasStarted){
            player.getLocation().getWorld().draw();
            player.draw();
            Engine.drawQueue();
            drawMisc(); 
        }
    }

    private void drawMisc(){
        Engine.renderString(Settings.VERSION, 0, 10, Color.WHITE);
        if (Settings.DEBUG) {            
            Engine.renderString(player.getLocation().toString(), 0, 25, Color.white);
            Engine.renderString(String.valueOf(lastFps + " fps"), 0, 35, Color.white);
            Engine.renderString(Location.valueOf(Mouse.getPoint()).toString(), 0, 45, Color.white);
            Engine.renderString("0", new Location(0.5, -1), Color.white);
        }
    }
        
    public void stop(){
        isPaused = false;
        isRunning = false;
    }
    
    public void update(Graphics graphics){        
        if (dbImage == null){
            dbImage = createImage (this.getSize().width, this.getSize().height);
            dbg = dbImage.getGraphics ();
        }
        
        dbg.setColor (getBackground ());
       
        int y = 0;
        double increment = Double.valueOf(SKY_COLOR) / this.getHeight();
        double color = 0;
        do {
            Color c = new Color((int)color, (int)color, 255);
            dbg.setColor(c);
            dbg.fillRect (0, y, this.getSize().width, 1);
            if(color < SKY_COLOR) {
                color += increment;
            }
            y++;
        }while(y < this.getHeight());                       

        paint (dbg);
        
        graphics.drawImage (dbImage, 0, 0, this);
    }
    
    public void run() {  
        while(isRunning){
            update();
            repaint();
            
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }  
    }

    public int getFps() {
        return lastFps;
    }
    
    public static BufferedImage getImage(String file) {
        try {
            return ImageIO.read(Sidecraft.class.getResourceAsStream(file));
        } catch (IOException e) {
            return null;
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

}