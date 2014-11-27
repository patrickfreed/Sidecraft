package com.freedsuniverse.sidecraft;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;

public class Settings {
    
	// Modify-able settings
    public static int BLOCK_SIZE = 32;
    public static boolean DEBUG = false;
    public static boolean SOUND = true;
    public static int DISPLAYED_BLOCKS = 30;

    // controls
    // public static Key INVENTORY_TOGGLE = Keys.I;

    // Misc.
    public final static String VERSION = "Indev 0.3";
    public final static float REFRESH_RATE = getRefreshRate();

    // Files
    public final static String SAVE_DIR = System.getenv("USERPROFILE") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + ".sidecraft" + File.separator;
    public final static String TEXTURE_KEYWORD = "texture/";
    public final static String MAIN_DIR = "/";
    public final static String PLAYER_DIR = "/player/";
    public final static String ENTITY_DIR = "/entity/";
    public final static String MATERIAL_DIR = "/material/";
    public static final String MENU_BACKGROUND = "/misc/menu/background.png";
    public static final String MENU_BACKGROUND_TILE = "/misc/menu/backgroundTile.png";
    public static final String PAUSED_BACKGROUND = "/misc/pause/backgroundTile.png";
    private static String DEFAULT_DIR = "-1";

    private static float getRefreshRate() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();

        return gs[0].getDisplayMode().getRefreshRate();
    }
    
    public static String defaultDirectory() {
        if (DEFAULT_DIR.equals("-1")) {
            String OS = System.getProperty("os.name").toUpperCase();
            if (OS.contains("WIN"))
                DEFAULT_DIR = System.getenv("APPDATA") + File.separator + ".sidecraft" + File.separator;
            else if (OS.contains("MAC"))
                DEFAULT_DIR = System.getProperty("user.home") + "/Library/Application " + "Support" + File.separator + ".sidecraft" + File.separator;
            else if (OS.contains("NUX"))
                DEFAULT_DIR = System.getProperty("user.home") + File.separator + ".sidecraft" + File.separator;
            else
                DEFAULT_DIR = System.getProperty("user.dir") + File.separator + ".sidecraft" + File.separator;
        }
        return DEFAULT_DIR;
    }
}