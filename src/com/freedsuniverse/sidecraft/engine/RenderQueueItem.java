package com.freedsuniverse.sidecraft.engine;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.freedsuniverse.sidecraft.world.Location;

public class RenderQueueItem {
	private static final int RECTANGLE = 0;
    private static final int IMAGE = 1;
    private static final int STRING = 2;
	
	private BufferedImage img;
    
    private String message;
    
    private Rectangle rec;
    
    private int x;
    private int y;
    private int mode;
    
    private Color color;

    public RenderQueueItem(Rectangle r, Color c) {
        this.rec = r;
        mode = RECTANGLE;
        color = c;
    }

    public RenderQueueItem(String msg, int x, int y, Color c) {
        this.x = x;
        this.y = y;
        message = msg;
        color = c;
        mode = STRING;
    }

    public RenderQueueItem(int x, int y, BufferedImage img) {
        this(img, new Rectangle(x, y, img.getWidth(), img.getHeight()));
    }

    public RenderQueueItem(BufferedImage img, int x, int y, int width, int height) {
        this.rec = new Rectangle(x, y, width, height);
        this.img = img;
        mode = IMAGE;
    }

    public RenderQueueItem(BufferedImage img, Rectangle rec) {
        this.rec = rec;
        this.img = img;
        mode = IMAGE;
    }

    public RenderQueueItem(Location location, BufferedImage i) {
        this(i, location.toRectangle(i.getWidth(), i.getHeight()));
    }

    public void draw() {
        if (mode == RECTANGLE) {
            Engine.renderRectangle(rec, color);
        } else if (mode == STRING) {
            Engine.renderString(message, x, y, this.color);
        } else {
            Engine.render(rec, img);
        }
    }
}
