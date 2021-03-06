package com.freedsuniverse.sidecraft.engine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import org.jbox2d.dynamics.Body;

import com.freedsuniverse.sidecraft.Sidecraft;
import com.freedsuniverse.sidecraft.entity.Entity;
import com.freedsuniverse.sidecraft.world.Location;

public class Engine {
    private static LinkedList<RenderQueueItem> queue = new LinkedList<RenderQueueItem>();
    
    public static void render(Entity e) {
        if (!e.isRegistered())
            return;

        Graphics2D g2 = (Graphics2D) getGraphics();
        Body b = e.getBody();
        int[] loc = e.getLocation().toArray();
        BufferedImage skin = e.getSkin();
        AffineTransform oldT = g2.getTransform();

        g2.rotate(-b.getAngle(), loc[0], loc[1]);
        g2.drawImage(skin, loc[0] - skin.getWidth() / 2, loc[1] - skin.getHeight() / 2, null);
        g2.setTransform(oldT);
    }

    public static void renderRectangle(Rectangle rec, Color c) {
        renderRectangle(rec.x, rec.y, rec.width, rec.height, c);
    }

    public static void renderRectangle(int x, int y, int width, int height, Color c) {
        getGraphics().setColor(c);
        getGraphics().fillRect(x, y, width, height);
    }

    public static void render(Rectangle rec, BufferedImage img) {
        render(rec.x, rec.y, rec.height, rec.width, img);
    }

    public static void render(int x, int y, int length, int width, BufferedImage img) {
        getGraphics().drawImage(scaleImage(img, width, length), x, y, null);
    }

    public static void render(Location loc, int width, int height, BufferedImage img) {
        int[] pix = loc.toArray();
        BufferedImage img1 = scale(img, width, height);
        getGraphics().drawImage(img1, pix[0] - img1.getWidth() / 2, pix[1] - img1.getHeight() / 2, null);
    }

    public static void render(Location loc, BufferedImage img) {
        int[] pix = loc.toArray();

        getGraphics().drawImage(img, pix[0] - img.getWidth() / 2, pix[1] - img.getHeight() / 2, null);
    }

    public static void render(int x, int y, BufferedImage img) {
        getGraphics().drawImage(img, x, y, null);
    }

    public static void renderString(String str, int x, int y, Color c) {
        getGraphics().setColor(c);
        getGraphics().drawString(str, x, y);
    }

    public static void renderString(String str, Location loc, Color c) {
        int[] r = loc.toArray();
        renderString(str, r[0], r[1], c);
    }

    public static void drawQueue() {
        for (int x = 0; x < queue.size(); x++) {
            queue.get(x).draw();
        }
        queue.clear();
    }

    public static void addQueueItem(RenderQueueItem i) {
        queue.add(i);
    }

    public static BufferedImage scale(BufferedImage before, int newWidth, int newHeight) {
        int w = before.getWidth();
        int h = before.getHeight();

        BufferedImage after = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(newWidth / (double) w, newHeight / (double) h);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        after = scaleOp.filter(before, after);

        return after;
    }

    public static Image scaleImage(BufferedImage original, int newWidth, int newHeight) {
        return original.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_FAST);
    }

    public static void render(RenderQueueItem i) {
        i.draw();
    }
    
    public static Graphics getGraphics() {
    	return Sidecraft.graphics();
    }
}