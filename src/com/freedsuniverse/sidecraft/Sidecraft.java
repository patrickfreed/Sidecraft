package com.freedsuniverse.sidecraft;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import com.freedsuniverse.sidecraft.entity.Player;
import com.freedsuniverse.sidecraft.input.InputListener;
import com.freedsuniverse.sidecraft.input.Key;
import com.freedsuniverse.sidecraft.screen.Menu;
import com.freedsuniverse.sidecraft.screen.Screen;
import com.freedsuniverse.sidecraft.world.Location;
import com.freedsuniverse.sidecraft.world.World;

public class Sidecraft extends Applet implements Runnable{
    private static final long serialVersionUID = 1L;
    
    public static int width, height;   

    public final String newLine = System.getProperty("line.separator");
    
    public static Player player;
    
    public static boolean isRunning = false, isScreen = true;

    public static Image stoneTile2;
    public static BufferedImage selectionTile, toolbarTile, inventoryTile, playerTile, toolbarSelectionTile;

    public static LinkedList<BufferedImage> textures;
    
    public static HashMap<String, World> worlds = new HashMap<String, World>();

    private Image dbImage;
    private Graphics dbg;

    public Screen[] screens;
    
    public void init(){
        width = this.getWidth();
        height = this.getHeight();
        
        textures = new LinkedList<BufferedImage>();
        screens = new Screen[1];
        screens[0] = new Menu();
        screens[0].show();
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
                      
        Thread th = new Thread(this);
        loadContent();
        th.start();
    }

    public void loadContent() {    
        loadTextures();

        player = new Player();

        World world = new World("world");
        worlds.put(world.getName(), world);
        player.world = world.getName();
        
        update();
    }

    public void UnloadContent() {
    }

    public void update() {
        if(isScreen){
            for (Screen s:screens){
                if(s.isVisible()){
                    s.update();
                }
            }
        }else{
            if (Key.F5.isDown()) {
                Settings.DEBUG = !Settings.DEBUG;
            }
            player.getWorld().update();
            player.update();     
        }
    }

    
    public void paint(Graphics g) {     
        Engine.graphics = g;
        
        if(isScreen){
            for (Screen s:screens){
                if(s.isVisible()){
                    s.draw();
                }
            }
        }else{
            player.getWorld().draw();
            player.draw();

            if (Settings.DEBUG) {
                //Screen.renderString(Sidecraft.player.getLocation().toString() + newLine + mouseCoords.toString() + newLine + "Mouse position" + mouseCoords.toVector2().ToString(), new Vector2(10, 10), Color.Black);
                Engine.renderString(player.getLocation().toString(), 90, 110, Color.blue);
                Engine.renderString("0", new Location(0, 0), Color.blue);
            }
        }
    }

    private void loadTextures() {
        try {      
            textures.add(ImageIO.read(Sidecraft.class.getResourceAsStream("resources/air.png")));
            textures.add(ImageIO.read(Sidecraft.class.getResourceAsStream("resources/grass.png")));
            textures.add(ImageIO.read(Sidecraft.class.getResourceAsStream("resources/dirt.png")));
            textures.add(ImageIO.read(Sidecraft.class.getResourceAsStream("resources/stone.png"))); 
            textures.add(ImageIO.read(Sidecraft.class.getResourceAsStream("resources/iron_ore.png")));
            textures.add(ImageIO.read(Sidecraft.class.getResourceAsStream("resources/obsidian.png")));
            textures.add(ImageIO.read(Sidecraft.class.getResourceAsStream("resources/sand.png")));
            textures.add(ImageIO.read(Sidecraft.class.getResourceAsStream("resources/coal_ore.png")));
            textures.add(ImageIO.read(Sidecraft.class.getResourceAsStream("resources/silver_ore.png")));
            textures.add(ImageIO.read(Sidecraft.class.getResourceAsStream("resources/gold_ore.png")));
            textures.add(ImageIO.read(Sidecraft.class.getResourceAsStream("resources/water.png")));
            textures.add(ImageIO.read(Sidecraft.class.getResourceAsStream("resources/tnt.png")));

            toolbarSelectionTile = ImageIO.read(Sidecraft.class.getResourceAsStream("resources/UIContent/toolbar_selection.png"));
            playerTile = ImageIO.read(Sidecraft.class.getResourceAsStream("resources/steve.png"));
            toolbarTile = ImageIO.read(Sidecraft.class.getResourceAsStream("resources/UIContent/toolbar.png"));
            selectionTile = ImageIO.read(Sidecraft.class.getResourceAsStream("resources/Mouse/selection.png"));
            inventoryTile = ImageIO.read(Sidecraft.class.getResourceAsStream("resources/inventory.png"));
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    public void stop(){
        isRunning = false;
    }
    
    public void update(Graphics g){
        if (dbImage == null)
        {
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
        
        
        System.out.println("running");
        
        while(isRunning){
            update();
            repaint();
            
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
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
        mainFrame.setTitle("Sidecraft");
        mainFrame.setResizable(false);
        sideCraft.init();
        sideCraft.start();
    }
}
