package com.freedsuniverse.sidecraft.entity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import com.freedsuniverse.sidecraft.world.Location;

public class Sun extends LightSource {

    private final static int RADIUS = 32 * 45;

    public Sun() {
        super(1.0, RADIUS);
    }

    public void light() {
        BufferedImage i = getLocation().getWorld().getLightMap();
        Graphics2D g2 = (Graphics2D) i.getGraphics();
        Location loc = getLocation().getWorld().getLightMapLocation();

        int[] coords = getLocation().toArray();
        int[] map = loc.toArray(); // TODO: put all this crap into
                                   // LightMap.class
        int xDiff = coords[0] - map[0];
        int yDiff = coords[1] - map[1];

        g2.setComposite(AlphaComposite.DstOut);
        Point2D center = new Point2D.Float(xDiff, yDiff);

        Point2D focus = new Point2D.Float(xDiff, yDiff);

        float[] dist = { 0.5f, 1f };
        Color[] colors = { new Color(100, 0, 0, getIntensity()), new Color(50, 0, 0, 0) };

        RadialGradientPaint rgp = new RadialGradientPaint(center, getRadius(), focus, dist, colors, CycleMethod.NO_CYCLE);
        g2.setPaint(rgp);

        g2.fillOval(xDiff - getRadius(), yDiff - getRadius(), getRadius() * 2, getRadius() * 4);
    }
}
