package com.freedsuniverse.sidecraft.world;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.freedsuniverse.sidecraft.Settings;

public class LightMap {
    private final static int LIGHTMAP_DIMENSION = 32 * 60;

    private static final double[] astages = new double[] { 0.25, 0.3, 0.45, 0.7, 1.0, 1.0, 1.0, 1.0, 1.0, 0.8, 0.6, 0.4, 0.2, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1 };

    public static BufferedImage getBlankLightMap(GameWorld w, Location lightLoc, int stage) {
        int[] falloff = new int[] { 0, 20, 40, 80, 160, 255 };

        Location ub = new Location((int) lightLoc.getX(), 20, w.getName());

        Color t = new Color(0, 0, 0, (int) (astages[stage] * 255));

        int alpha = t.getAlpha() - 100;
        if (alpha < 0) {
            alpha = 0;
        }

        int ydiff = (int) (lightLoc.getY() - ub.getY());

        BufferedImage i = new BufferedImage(LIGHTMAP_DIMENSION, LIGHTMAP_DIMENSION, BufferedImage.TYPE_INT_ARGB);
        Graphics g = i.getGraphics();
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.black);
        g2.fillRect(0, 0, i.getWidth(), i.getHeight());

        g2.setComposite(AlphaComposite.DstOut);

        int start = falloff.length - 1;
        while (start >= 1 && t.getAlpha() - falloff[start] < 0) {
            start--;
        }

        int r = LIGHTMAP_DIMENSION / Settings.BLOCK_SIZE;
        ArrayList<String> good = new ArrayList<String>();

        for (int x = 0; x < r; x++) {
            int ground = 0;
            int c = 0;

            for (int y = 0; y < r; y++) {
                Location l = ub.modify(x, -y);
                int x1 = x * Settings.BLOCK_SIZE;
                int y1 = (y + ydiff) * Settings.BLOCK_SIZE;
                Color black = new Color(0, 0, 0, 0);

                if (good.contains(l.getId()))
                    continue;

                if (ground == 1) {
                    GradientPaint p = new GradientPaint(x1, y1, t, (x + 1) * Settings.BLOCK_SIZE, (y + ydiff + 6) * Settings.BLOCK_SIZE, black);

                    for (int count = 0; count < 7; count++) {
                        Location l1 = l.modify(0, -count);
                        good.add(l1.getId());
                    }

                    // g2.setColor(new Color(0, 0, 0, t.getAlpha() -
                    // falloff[c]));
                    // g2.fillRect(x * Settings.BLOCK_SIZE, (y + ydiff) *
                    // Settings.BLOCK_SIZE, Settings.BLOCK_SIZE,
                    // Settings.BLOCK_SIZE);
                    g2.setPaint(p);
                    Point2D p1 = p.getPoint1();

                    g2.fillRect((int) p1.getX(), (int) p1.getY(), Settings.BLOCK_SIZE, Settings.BLOCK_SIZE * 6);

                    ground = 2;

                    if (c > start) {
                        c = start;
                    }
                } else if (ground == 2) {
                    // if(good.contains(l.modify(1, 0).getId())) {
                    // GradientPaint p = new GradientPaint(x1, y1 +
                    // (Settings.BLOCK_SIZE / 2), t, (x + 1) *
                    // Settings.BLOCK_SIZE, y1 + (Settings.BLOCK_SIZE / 2),
                    // black);
                    // g2.setPaint(p);
                    // g2.fillRect(x1, y1, Settings.BLOCK_SIZE,
                    // Settings.BLOCK_SIZE);
                    // }else if(good.contains(l.modify(-1, 0).getId())) {
                    // GradientPaint p = new GradientPaint(x1, y1 +
                    // (Settings.BLOCK_SIZE / 2), t, (x + 1) *
                    // Settings.BLOCK_SIZE, y1 + (Settings.BLOCK_SIZE / 2),
                    // black);
                    // g2.setPaint(p);
                    // g2.fillRect(x1, (y + ydiff) * Settings.BLOCK_SIZE,
                    // Settings.BLOCK_SIZE, Settings.BLOCK_SIZE);
                    // }else {
                    g2.setColor(new Color(0, 0, 0, 0));
                    g2.fillRect(x1, (y + ydiff) * Settings.BLOCK_SIZE, Settings.BLOCK_SIZE, Settings.BLOCK_SIZE);
                    // }
                } else if (w.getBlockAt(l).getType().isSolid()) {
                    ground = 1;
                    g2.setColor(t);
                    g2.fillRect(x1, (y + ydiff) * Settings.BLOCK_SIZE, Settings.BLOCK_SIZE, Settings.BLOCK_SIZE);
                    // g2.setColor(n);
                    // g2.fillRect(x * Settings.BLOCK_SIZE +
                    // Settings.BLOCK_SIZE, (y + ydiff) * Settings.BLOCK_SIZE,
                    // Settings.BLOCK_SIZE, Settings.BLOCK_SIZE);
                } else {
                    g2.setColor(t);
                    g2.fillRect(x1, (y + ydiff) * Settings.BLOCK_SIZE, Settings.BLOCK_SIZE, Settings.BLOCK_SIZE);
                    good.add(l.getId());
                    // g2.setColor(n);
                    // g2.fillRect(x * Settings.BLOCK_SIZE +
                    // Settings.BLOCK_SIZE, (y + ydiff) * Settings.BLOCK_SIZE,
                    // Settings.BLOCK_SIZE, Settings.BLOCK_SIZE);
                    // g2.fillRect(x * Settings.BLOCK_SIZE -
                    // Settings.BLOCK_SIZE, (y + ydiff) * Settings.BLOCK_SIZE,
                    // Settings.BLOCK_SIZE, Settings.BLOCK_SIZE);
                }

            }
        }
        return i;
    }
}
