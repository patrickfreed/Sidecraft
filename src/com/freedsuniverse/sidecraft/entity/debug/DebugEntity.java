package com.freedsuniverse.sidecraft.entity.debug;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;

import com.freedsuniverse.sidecraft.entity.Entity;

public class DebugEntity extends Entity {

    private float r;
    private boolean b;

    public DebugEntity(boolean box, float r) {
        super();

        b = box;
        this.r = r;

        fd.friction = 1.35f;
        fd.density = 1.0f;

        if (box) {
            PolygonShape ps = new PolygonShape();
            ps.setAsBox(r / 64.0f, r / 64.0f);
            fd.shape = ps;
        } else {
            CircleShape s = new CircleShape();
            s.m_radius = r / 64.0f;

            fd.shape = s;
        }
    }

    public BufferedImage getSkin() {
        int r1 = (int) r;

        BufferedImage i = new BufferedImage(r1, r1, BufferedImage.TYPE_INT_ARGB);
        Graphics g = i.getGraphics();

        g.setColor(Color.red);

        if (b) {
            g.fillRect(0, 0, r1, r1);
        } else {
            g.fillOval(0, 0, r1, r1);
        }

        return i;
    }
}
