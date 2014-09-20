package com.freedsuniverse.sidecraft.entity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyType;

import com.freedsuniverse.sidecraft.world.Location;

public class LightSource extends Entity {

    private int intensity, radius;

    // TODO: add colored lighting

    public LightSource(double i, int r) {
        super();

        fd.density = 0;
        PolygonShape ps = new PolygonShape();
        ps.setAsBox(0, 0);
        fd.shape = ps;
        bd.type = BodyType.STATIC;
        bd.active = false;

        intensity = (int) (i * 255.0);
        radius = r;
    }

    public void update() {
        if (getLocation() != null) {
            if (getLocation().getWorld().lighting()) {
                light();
            }
        }
    }

    public void draw() {
        return;
    }

    public Rectangle getBounds() {
        return getLocation().toRectangle(0, 0);
    }

    public void setRadius(int r) {
        this.radius = r;
        getLocation().getWorld().lightUpdate();
    }

    public int getRadius() {
        return radius;
    }

    public void destroy() {
        super.destroy();
        getLocation().getWorld().lightUpdate();
    }

    public void setIntensity(double d) {
        if (d < 0) {
            intensity = 0;
        } else if (d > 1) {
            intensity = 255;
        } else {
            intensity = (int) (d * 255);
        }

        getLocation().getWorld().lightUpdate();
    }

    public int getIntensity() {
        return intensity;
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

        float[] dist = { 0f, 1f };
        Color[] colors = { new Color(100, 0, 0, intensity), new Color(50, 0, 0, 0) };

        RadialGradientPaint rgp = new RadialGradientPaint(center, radius, focus, dist, colors, CycleMethod.NO_CYCLE);
        g2.setPaint(rgp);

        g2.fillOval(xDiff - radius, yDiff - radius, radius * 2, radius * 2);
    }

    public void spawn(Location location) {
        super.spawn(location);
        location.getWorld().lightUpdate();
    }
}
