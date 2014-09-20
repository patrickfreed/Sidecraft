package com.freedsuniverse.sidecraft;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
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

import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.entity.Player;
import com.freedsuniverse.sidecraft.input.InputListener;
import com.freedsuniverse.sidecraft.inventory.EntityInventory;
import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.material.MaterialStack;
import com.freedsuniverse.sidecraft.material.Tool;
import com.freedsuniverse.sidecraft.screen.Menu;
import com.freedsuniverse.sidecraft.screen.Paused;
import com.freedsuniverse.sidecraft.screen.SaveLoader;
import com.freedsuniverse.sidecraft.screen.SettingsMenu;
import com.freedsuniverse.sidecraft.world.GameWorld;
import com.freedsuniverse.sidecraft.world.Location;

public class Main extends Applet {
    private static final long serialVersionUID = 1L;

    public static JPanel contentPane;
    private static JFrame frame;

    private static Sidecraft s;

    private static ArrayList<BufferedImage> textures;

    public static BufferedImage toolbarSelectionTile;
    public static BufferedImage playerTile;
    public static BufferedImage toolbarTile;
    public static BufferedImage selectionTile;
    public static BufferedImage inventoryTile;
    public static BufferedImage workbenchTile;

    public static void main(String[] args) {
        frame = new JFrame();

        int width = 32 * Settings.DISPLAYED_BLOCKS;
        int height = width * 9 / 16;

        setResolution(width, height);
        setScreen(Menu.class.getName());
    }

    public static void setScreen(String string) {
        ((CardLayout) contentPane.getLayout()).show(contentPane, string);
        contentPane.getComponent(0).requestFocus();
    }

    public static int getPaneWidth() {
        return contentPane.getWidth();
    }

    public static int getPaneHeight() {
        return contentPane.getHeight();
    }

    public static void loadTextures() {
        textures = new ArrayList<BufferedImage>();

        int length = 15;

        for (int x = 0; x < length; x++) {
            textures.add(getImage("/material/" + x + ".png", Settings.BLOCK_SIZE, Settings.BLOCK_SIZE));
        }

        toolbarSelectionTile = getImage("/misc/toolbar_selection.png");
        playerTile = getImage("/player/texture/steve.png");
        toolbarTile = getImage("/misc/toolbar.png");
        selectionTile = getImage("/misc/selection.png");
        inventoryTile = getImage("/misc/inventory.png");
        workbenchTile = getImage("/misc/workbench_interior.png");
    }

    private static void setup(Container c) {
        loadTextures();

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        contentPane.setLayout(new CardLayout());
        contentPane.setBounds(c.getBounds());

        contentPane.add(new Menu(), Menu.class.getName());
        contentPane.add(new SaveLoader(), SaveLoader.class.getName());
        contentPane.add(new Paused(), Paused.class.getName());
        contentPane.add(new SettingsMenu(), SettingsMenu.class.getName());

        s = new Sidecraft(contentPane.getWidth(), contentPane.getHeight());
        contentPane.add(s, Sidecraft.class.getName());

        for (Component t : contentPane.getComponents()) {
            t.addKeyListener(InputListener.i);
            t.addMouseListener(InputListener.i);
        }

        frame.setContentPane(contentPane);

        frame.setIconImage(Main.getImage("/misc/menu/icon.png"));

        frame.setTitle("Sidecraft " + Settings.VERSION);
        frame.setVisible(true);
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

    public static BufferedImage getImage(String file, int width, int height) {
        BufferedImage i = getImage(file);

        if (i.getWidth() != width && i.getHeight() != height) {
            i = Engine.scale(i, width, height);
        }

        return i;
    }

    public static void loadGame(String name) {
        try {
            File dir = new File(Settings.defaultDirectory() + name + File.separator);

            ArrayList<GameWorld> worlds = new ArrayList<GameWorld>();
            ArrayList<GameWorld> ws = new ArrayList<GameWorld>();
            String player = "";

            for (File f : dir.listFiles()) {
                if (f.isDirectory() && f.getName().contains("world_")) {
                    worlds.add(GameWorld.load(f));
                } else if (f.isFile() && f.getName().equals("player.txt")) {
                    player = read(f);
                }
            }

            String[] partsOfPlayer = player.split("\n");
            String[] loc = partsOfPlayer[0].split(":");

            Player p = new Player();

            Location playerLoc = new Location(Double.valueOf(loc[0]), Double.valueOf(loc[1]), loc[2]);
            EntityInventory inv = new EntityInventory(p);

            String[] i = partsOfPlayer[1].split(";");

            for (String stack : i) {
                String[] partsOfStack = stack.split(":");

                if (partsOfStack.length == 5) {
                    inv.setAt(Integer.valueOf(partsOfStack[0]), Integer.valueOf(partsOfStack[1]), Tool.valueOf(partsOfStack[2], partsOfStack[4]));
                } else {
                    inv.setAt(Integer.valueOf(partsOfStack[0]), Integer.valueOf(partsOfStack[1]), new MaterialStack(Material.valueOf(partsOfStack[2]), Integer.valueOf(partsOfStack[3])));
                }
            }

            p.spawn(playerLoc, inv);

            // for(String s:worlds) {
            // HashMap<String, Entity> e = new HashMap<String, Entity>();
            // HashMap<String, Block> b = new HashMap<String, Block>();
            // String[] parts = s.split("\n");
            // String n = parts[0];
            // String[] es = parts[1].split(";");
            // String[] blocks = parts[2].split(";");
            // int seed = Integer.valueOf(parts[parts.length - 1]);
            //
            // for(String bl:blocks) {
            // String[] b0 = bl.split(":");
            //
            // Block b1 = new Block(Material.valueOf(b0[0]));
            // Location l1 = new Location(Double.valueOf(b0[1]),
            // Double.valueOf(b0[2]), b0[3]);
            // b1.setLocation(l1);
            //
            // String names = (int)Math.floor(l1.getX()) + "," +
            // (int)Math.ceil(l1.getY());
            //
            // b.put(names, b1);
            // }
            //
            // //World w = World.load(f)
            // // World w = new World(n, b, e, new FlatNoiseGen(seed));
            //
            // for(String entity:es) {
            // String[] e0 = entity.split(":");
            //
            // //TODO: move to separate method, Entity.valuOf(String n) perhaps
            //
            // if(e0[0].equals("DropEntity")) {
            // DropEntity de = new DropEntity(Material.valueOf(e0[1]), new
            // Location(Double.valueOf(e0[2]), Double.valueOf(e0[3]), e0[4]));
            //
            // }
            // }
            //
            // }
            s.load(name, ws, p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean saveGame(String name) {
        // TODO: fix

        try {
            File dir = new File(Settings.defaultDirectory() + name + File.separator);
            if (!dir.exists()) {
                dir.mkdir();
            }

            for (GameWorld w : s.getWorlds().values()) {
                w.save();
            }

            // File p = new File(dir.getCanonicalFile() + File.separator +
            // "player.txt");
            // if(!write(p, s.player.toString())) return false;
        } catch (Exception e) {
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

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }

            br.close();
            return sb.toString();
        } catch (Exception e) {
            return null;
        }

    }

    public static boolean write(File f, String c) {
        try {
            if (!f.exists())
                f.createNewFile();

            FileWriter fileWriter = new FileWriter(f);
            BufferedWriter bWriter = new BufferedWriter(fileWriter);
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

        for (int x = 0; x < source.getWidth(); x++) {
            for (int y = 0; y < source.getHeight(); y++) {
                int pixel = source.getRGB(x, y);

                if ((pixel >> 24) == 0x00) {
                    l.setRGB(x, y, pixel);
                } else {
                    l.setRGB(x, y, tint.getRGB());
                }
            }
        }

        return l;
    }

    public static void setResolution(int width, int height) {
        Settings.BLOCK_SIZE = width / Settings.DISPLAYED_BLOCKS;

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(100, 100, width, height);
        frame.setResizable(false);

        loadTextures();

        setup(frame);
    }

    public static BufferedImage getTexture(int id) {
        if (id < 0 || id >= textures.size())
            return textures.get(0);

        return textures.get(id);
    }
}
