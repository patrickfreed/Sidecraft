package com.freedsuniverse.sidecraft;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.entity.Player;
import com.freedsuniverse.sidecraft.input.InputListener;
import com.freedsuniverse.sidecraft.input.Key;
import com.freedsuniverse.sidecraft.input.Mouse;
import com.freedsuniverse.sidecraft.material.CraftingRecipe;
import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.screen.Button;
import com.freedsuniverse.sidecraft.screen.Menu;
import com.freedsuniverse.sidecraft.screen.Paused;
import com.freedsuniverse.sidecraft.screen.Screen;
import com.freedsuniverse.sidecraft.world.Location;
import com.freedsuniverse.sidecraft.world.World;

public class Sidecraft extends Applet implements Runnable{
    private static final long serialVersionUID = 1L;
    
    public static int width, height;   

    public final String newLine = System.getProperty("line.separator");
    
    public static Player player;
    
    private int frames, lastFps;

    public static long now, lastTime;
    
    public static Rectangle window;
    
    public static Sound music;
    
    public static boolean isRunning = false, isPaused = true, hasStarted = false;

    public static Image stoneTile2;
    public static BufferedImage selectionTile, toolbarTile, inventoryTile, playerTile, toolbarSelectionTile, workbenchTile;
    
    public static LinkedList<BufferedImage> textures;
    
    public static HashMap<String, World> worlds = new HashMap<String, World>();

    public static Screen currentScreen;
    
    private Image dbImage;
    private Graphics dbg;
    
    public void init(){
        width = this.getWidth();
        height = this.getHeight();       
        window = new Rectangle(0, 0, width, height);
        
        Button.DEFAULT_TILE = getImage("/misc/menu/buttonTile.png");
        
        textures = new LinkedList<BufferedImage>();
        music = new Sound("/audio/music.wav");

        currentScreen = new Menu();
        currentScreen.show();
        setBackground(Color.blue);
            
        InputListener i = new InputListener();
        this.addKeyListener(i);
        this.addMouseListener(i);
        this.addMouseMotionListener(i);
        this.addMouseWheelListener(i);    
    }
    
    public void start() {
        Engine.graphics = getGraphics();
        isRunning = true;
        loadTextures();              
        Thread th = new Thread(this);
        lastTime = 0;
        CraftingRecipe.recipes.add(new CraftingRecipe(new int[]{2, 2, -1, -1, -1, -1, -1, -1, -1}, Material.IRON_ORE, 16));
        th.start();
    }

    public void startNewGame() {                        
        music.play();
        
        player = new Player();

        World world = new World("world");
        worlds.put(world.getName(), world);
        player.world = world.getName();
        
        update();
    }

    public void update() {
        if(hasStarted && !this.hasFocus()){
            isPaused = true;
            currentScreen = new Paused();
            currentScreen.show();
        }
        
        if(Key.ESCAPE.toggled() && hasStarted) pause();
        
        updateKeys();
        Mouse.update();
        if(isPaused){
            if(currentScreen != null){
                currentScreen.update();
            }
        }else{
            if(!hasStarted){
                hasStarted = true;
                startNewGame();
            }            
            
            if (Key.F5.toggled()) Settings.DEBUG = !Settings.DEBUG;
            now = System.currentTimeMillis();
            frames++;
            
            if(now - lastTime > 1000){
                System.out.println(frames + " fps");
                lastFps = frames;
                frames = 0;
                lastTime = now;
            }
            player.getLocation().getWorld().update();
            player.update();     
        }
    }

    private void pause(){
        if(isPaused){
            currentScreen.hide();
            isPaused = false;
        }else {
            currentScreen = new Paused();
            currentScreen.show();
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
    
    public void paint(Graphics g) {     
        Engine.graphics = g;       
        
        if(hasStarted){
            player.getLocation().getWorld().draw();
            player.draw();
            Engine.drawQueue();
            drawMisc();   
        }
        
        if(currentScreen != null && currentScreen.isVisible()){
            currentScreen.draw();
        }
    }

    private void drawMisc(){
        Engine.renderString(Settings.VERSION, 0, 10, Color.WHITE);
        if (Settings.DEBUG) {            
            Engine.renderString(player.getLocation().toString(), 0, 25, Color.white);
            Engine.renderString(String.valueOf(lastFps + " fps"), 0, 35, Color.white);
            Engine.renderString("0", new Location(0.5, -1), Color.white);
        }
    }
    
    private void loadTextures() {
        int length = 13;
  
        for(int x = 0; x < length; x++){
            textures.add(getImage("/material/" + x + ".png"));
        }

        toolbarSelectionTile = getImage("/misc/toolbar_selection.png");
        playerTile = getImage("/player/texture/steve.png");
        toolbarTile = getImage("/misc/toolbar.png");
        selectionTile = getImage("/misc/selection.png");
        inventoryTile = getImage("/misc/inventory.png");
        workbenchTile = getImage("/misc/workbench_interior.png");
    }
    
    public void stop(){
        isRunning = false;
    }
    
    public void update(Graphics g){
        if (dbImage == null){
            dbImage = createImage (this.getSize().width, this.getSize().height);
            dbg = dbImage.getGraphics ();
        }

        dbg.setColor (getBackground ());
        dbg.fillRect (0, 0, this.getSize().width, this.getSize().height);

        dbg.setColor (getForeground());
        paint (dbg);
        
        g.drawImage (dbImage, 0, 0, this);
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
    
    public static void main(String[] args) {
        final Frame mainFrame = new Frame();
        mainFrame.setSize(800, 400);
        final Sidecraft sideCraft = new Sidecraft();
        
        mainFrame.add(sideCraft);
        mainFrame.setVisible(true);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                mainFrame.dispose();
            }
            public void windowClosed(WindowEvent event) {
                sideCraft.stop();
                System.exit(0);
            }
        });
        mainFrame.setTitle("Sidecraft " + Settings.VERSION);
        mainFrame.setResizable(false);
        sideCraft.init();
        sideCraft.start();
    }

    public static BufferedImage getImage(String file) {
        try {
            return ImageIO.read(Sidecraft.class.getResourceAsStream(file));
        } catch (IOException e) {
            return null;
        }
    }
}
