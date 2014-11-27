package com.freedsuniverse.sidecraft.entity;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import com.freedsuniverse.sidecraft.Settings;
import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.world.Location;

public class Entity {

    private Location loc;

    private BufferedImage img;

    protected boolean destroyed;

    // these fields do not need to be protected, just too lazy to fix
    protected BodyDef bd;
    protected FixtureDef fd;
    protected Body b;

    public Entity() {
        fd = new FixtureDef();
        bd = new BodyDef();

        destroyed = false;

        bd.type = BodyType.DYNAMIC;
        bd.angle = 0;
    }

    public void update() {
        if (isRegistered()) {
            loc.setCoordinates(b.getPosition().x, b.getPosition().y);
        }
    }

    public void draw() {
        Engine.render(this);
    }

    public void destroy() {
        if (isDestroyed())
            return;

        loc.getWorld().unregisterEntity(this);
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setLocation(Location l) {
        float x = (float) l.getX();
        float y = (float) l.getY();

        loc = l;

        if (!this.isRegistered()) {
            this.bd.position.x = x;
            this.bd.position.y = y;
        } else {
            this.b.getPosition().x = (float) l.getX();
            this.b.getPosition().y = (float) l.getY();
        }
    }

    public void spawn(Location l) {
        if (isRegistered())
            return;

        setLocation(l);
        bd.position = new Vec2((float) l.getX(), (float) l.getY());
        b = l.getWorld().preRegisterEntity(this);
    }

    public void setBounds(Rectangle r) {
        this.loc = Location.valueOf(r);
    }

    public void setSkin(BufferedImage i, int width, int height) {
        img = i;
        PolygonShape sd = new PolygonShape();

        float w1 = (float) (width) / 64.0f;
        float h1 = (float) (height) / 64.0f;

        sd.setAsBox(w1, h1);
        fd.shape = sd;
    }

    public void setGhostSkin(BufferedImage i) {
        float w = i.getWidth() / 64.0f;
        float h = i.getHeight() / 64.0f;

        ChainShape es = new ChainShape();

        Vec2[] vs = new Vec2[4];
        vs[0] = new Vec2(-w, -h);
        vs[1] = new Vec2(w, -h);
        vs[2] = new Vec2(w, h);
        vs[3] = new Vec2(-w, h);

        es.createLoop(vs, 4);

        fd.shape = es;

        img = i;
    }

    public void setSkin(BufferedImage i) {
        img = i;
        PolygonShape sd = new PolygonShape();
        sd.setAsBox(i.getWidth() / 64.0f, i.getHeight() / 64.0f);
        fd.shape = sd;
    }

    public boolean isRegistered() {
        return b != null;
    }

    public BufferedImage getSkin() {
        return img;
    }

    public Location getLocation() {
        return loc;
    }

    public void modifyLocation(float xmod, float ymod) {
        loc = loc.modify(xmod, ymod);
        b.getPosition().x = b.getPosition().x + xmod;
        b.getPosition().y = b.getPosition().y + ymod;
    }

    public Rectangle getBounds() {
        return loc.toRectangle(this.getSkin().getWidth(), this.getSkin().getHeight());
    }

    public Entity[] getNearbyEntites() {
        return null;
    }

    public BodyDef getBd() {
        return bd;
    }

    public FixtureDef getFd() {
        return fd;
    }

    public float getHeight() {
        return this.getSkin().getHeight() / (Settings.BLOCK_SIZE * 1.0f);
    }

    public float getWidth() {
        return this.getSkin().getWidth() / (Settings.BLOCK_SIZE * 1.0f);
    }

    public Body getBody() {
        return b;
    }

    public void setBody(Body body) {
        if (body != null) {
        	b = body;
        }
    }
}
