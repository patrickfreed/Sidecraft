package com.freedsuniverse.sidecraft;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.freedsuniverse.sidecraft.entity.Player;
import com.freedsuniverse.sidecraft.input.InputListener;
import com.freedsuniverse.sidecraft.inventory.EntityInventory;
import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.material.MaterialStack;
import com.freedsuniverse.sidecraft.material.Tool;
import com.freedsuniverse.sidecraft.screen.Menu;
import com.freedsuniverse.sidecraft.screen.Paused;
import com.freedsuniverse.sidecraft.screen.SaveLoader;
import com.freedsuniverse.sidecraft.world.Location;
import com.freedsuniverse.sidecraft.world.World;

public class Main extends Applet{
    private static final long serialVersionUID = 1L;
    public static JPanel contentPane;   
    private static Sidecraft s;
    private static int width, height;
    @SuppressWarnings("unused")
    private static Sound music; 
    public static ArrayList<BufferedImage> textures;
    public static BufferedImage toolbarSelectionTile, playerTile, toolbarTile, selectionTile, inventoryTile, workbenchTile;
    
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    JFrame frame = new JFrame();
                    
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setBounds(100, 100, 800, 400);
                    frame.setResizable(false);
                    
                    setup(frame);
                    
                    width = 800;
                    height = 400;
                    frame.setContentPane(contentPane);
                    
                    frame.setTitle("Sidecraft " + Settings.VERSION);                  
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public static void setScreen(String string) {  
        ((CardLayout) contentPane.getLayout()).show(contentPane, string);
        contentPane.getComponent(0).requestFocus();
    }
    
    public static int getPaneWidth() {
        return width;
    }
    
    public static int getPaneHeight() {
        return height;
    }

    private static void loadTextures() {
        int length = 14;
  
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
    
    private static void setup(Container c) {
        textures = new ArrayList<BufferedImage>();
        loadTextures();
        music = new Sound("/audio/music.ogg");
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        contentPane.setLayout(new CardLayout());
        contentPane.setBounds(c.getBounds());
        
        contentPane.add(new Menu(), Menu.class.getName());
        contentPane.add(new SaveLoader(), SaveLoader.class.getName());
        contentPane.add(new Paused(), Paused.class.getName());
        s = new Sidecraft();
        contentPane.add(s, Sidecraft.class.getName());
        
        for(Component t:contentPane.getComponents()) {
            t.addKeyListener(InputListener.i);
            t.addMouseListener(InputListener.i);
        }       
        
        setScreen(Menu.class.getName());
    }
    
    public void init() {
        this.setLayout(new BorderLayout());
        setup(this);
        this.add(contentPane);
        this.setVisible(true);
    }
    
    public static BufferedImage getImage(String file) {
        try {
            return ImageIO.read(Sidecraft.class.getResourceAsStream(file));
        } catch (IOException e) {
            return null;
        }
    }
    
    public static void loadGame(String name) {
        try { 
            File dir = new File(Settings.defaultDirectory() + name + File.separator);
            
            ArrayList<World> worlds = new ArrayList<World>();
            ArrayList<World> ws = new ArrayList<World>();
            String player = "";
            
            for(File f:dir.listFiles()) {
                if(f.isDirectory() && f.getName().contains("world_")){
                    worlds.add(World.load(f));
                }else if(f.isFile() && f.getName().equals("player.txt")){
                    player = read(f);
                }
            }                   
            
            String[] partsOfPlayer = player.split("\n");
            String[] loc = partsOfPlayer[0].split(":");
            
            Player p = new Player();
            
            Location playerLoc = new Location(Double.valueOf(loc[0]), Double.valueOf(loc[1]), loc[2]);
            EntityInventory inv = new EntityInventory(p);
            
            String[] i = partsOfPlayer[1].split(";");
            
            for(String stack:i) {
                String[] partsOfStack = stack.split(":");
                
                if(partsOfStack.length == 5) {
                    inv.setAt(Integer.valueOf(partsOfStack[0]), Integer.valueOf(partsOfStack[1]), Tool.valueOf(partsOfStack[2], partsOfStack[4]));
                }else {
                    inv.setAt(Integer.valueOf(partsOfStack[0]), Integer.valueOf(partsOfStack[1]), new MaterialStack(Material.valueOf(partsOfStack[2]), Integer.valueOf(partsOfStack[3])));
                }
            }
            
            p.load(playerLoc, inv);
            
//            for(String s:worlds) {
//                HashMap<String, Entity> e = new HashMap<String, Entity>();
//                HashMap<String, Block> b = new HashMap<String, Block>();
//                String[] parts = s.split("\n");
//                String n = parts[0];
//                String[] es = parts[1].split(";");
//                String[] blocks = parts[2].split(";");
//                int seed = Integer.valueOf(parts[parts.length - 1]);               
//                
//                for(String bl:blocks) { 
//                    String[] b0 = bl.split(":");
//
//                    Block b1 = new Block(Material.valueOf(b0[0]));
//                    Location l1 = new Location(Double.valueOf(b0[1]), Double.valueOf(b0[2]), b0[3]);
//                    b1.setLocation(l1);
//                    
//                    String names = (int)Math.floor(l1.getX()) + "," + (int)Math.ceil(l1.getY());
//                    
//                    b.put(names, b1);
//                }
//                
//                //World w = World.load(f)
//               // World w = new World(n, b, e, new FlatNoiseGen(seed));
//                
//                for(String entity:es) {
//                    String[] e0 = entity.split(":");
//                    
//                    //TODO: move to separate method, Entity.valuOf(String n) perhaps
//                    
//                    if(e0[0].equals("DropEntity")) {
//                        DropEntity de = new DropEntity(Material.valueOf(e0[1]), new Location(Double.valueOf(e0[2]), Double.valueOf(e0[3]), e0[4]));
//                        
//                    }
//                }
//                
//            }
            s.load(name, ws, p);
        }catch (Exception e) {
            e.printStackTrace();
        }    
    }
    
    public static boolean saveGame(String name) {        
        try {  
            File dir = new File(Settings.defaultDirectory() + name + File.separator);
            if(!dir.exists()) {
                dir.mkdir();
            }
            
            for(World w:s.worlds.values()) {
                w.save();
            }      
            
            File p = new File(dir.getCanonicalFile() + File.separator + "player.txt");
            if(!write(p, s.player.toString())) return false;
        }catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static Sidecraft getGame() {
        return s;
    }
    
    public static String read(File f) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            
            while(line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            
            br.close();
            return sb.toString();
        } catch (Exception  e) {
            return null;
        } 
        
    }
    
    public static boolean write(File f, String c) {
        try {
            if(!f.exists()) f.createNewFile();
            
            FileWriter fileWriter = new FileWriter(f);
            BufferedWriter bWriter = new BufferedWriter (fileWriter);
            bWriter.write(c);
            bWriter.flush();
            bWriter.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static JPanel getContentPane() {
        return contentPane;
    }

    public static BufferedImage getTint(BufferedImage source, Color tint, int intensity) {
        BufferedImage l = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        for(int x = 0; x < source.getWidth(); x++) {
            for(int y = 0; y < source.getHeight(); y++) {
                int pixel = source.getRGB(x, y);
                
                if((pixel>>24) == 0x00) {
                    l.setRGB(x, y, pixel);
                }else {
                    l.setRGB(x, y, tint.getRGB());
                }
            }
        }
        
        return l;
    }
}
