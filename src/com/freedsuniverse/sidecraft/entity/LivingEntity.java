package com.freedsuniverse.sidecraft.entity;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.FixtureDef;

import com.freedsuniverse.sidecraft.Main;
import com.freedsuniverse.sidecraft.Settings;
import com.freedsuniverse.sidecraft.engine.Animation;
import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.inventory.Inventory;
import com.freedsuniverse.sidecraft.material.Item;
import com.freedsuniverse.sidecraft.material.MaterialStack;
import com.freedsuniverse.sidecraft.world.CollisionListener;
import com.freedsuniverse.sidecraft.world.Location;

public class LivingEntity extends Entity {
    private static final long DAMAGE_TIME = 500;
    private static final long RECENT_DAMAGE_TIME = 4000;
    
    protected String id;

    public static final int STATIONARY = 0, WALKING = 1;

    private Animation[] anime;
    
    private int health;
    private int recentdmg;
    private int currentAnimation;
    protected int footContacts;
    
    private long dmg;
    
    private Inventory inv;
    
    private boolean damaged;
    private boolean facingLeft;

    public LivingEntity(String id, int w, int h, int health, float density) {
        super();

        footContacts = 0;

        anime = new Animation[2];

        anime[STATIONARY] = Animation.read("/entity/" + id + "/stationary.png", w, h, 10);
        anime[WALKING] = Animation.read("/entity/" + id + "/walking.png", w, h, 10);

        PolygonShape ps = new PolygonShape();
        ps.setAsBox(anime[0].getSlide().getWidth() / ((float) Settings.BLOCK_SIZE * 2.0f) - 0.1f, anime[0].getSlide().getHeight() / ((float) Settings.BLOCK_SIZE * 2.0f) - 0.05f);

        fd.shape = ps;
        bd.fixedRotation = true;
        fd.friction = 0.3f;
        bd.allowSleep = false;
        fd.density = density;

        inv = new Inventory();
        this.id = id;
        setHealth(health);
        facingLeft = false;
        damaged = false;
        dmg = -1;
    }

    public void draw() {
        if (damaged) {
            if (System.currentTimeMillis() - dmg <= DAMAGE_TIME) {

                Engine.render(getLocation(), Main.getTint(anime[0].getSlide(), Color.red, 0));
            } else {
                Engine.render(getLocation(), anime[0].getSlide());
                damaged = false;
            }
        } else {
            Engine.render(this);
        }

        if (recentdmg > 0) {
            Engine.renderString("" + recentdmg, getLocation().modify(-this.getWidth() / 2.0f, this.getHeight() / 2.0f + 0.25f), Color.white);

            if (System.currentTimeMillis() - dmg >= RECENT_DAMAGE_TIME) {
                recentdmg = 0;
                System.out.println("has been more than " + (RECENT_DAMAGE_TIME / 1000) + "s since last damage");
            }
        }

        String health = "";

        int c = 0;
        while (c < getHealth()) {
            health += "-";
            c++;
        }

        Engine.renderString(health, getLocation().modify(-this.getWidth() / 2.0f, this.getHeight() / 2.0f + 0.1f), Color.green);
        // Engine.renderString(String.valueOf(this.footContacts),
        // getLocation().modify(0, 0.5), Color.white);
    }

    @Override
    public BufferedImage getSkin() {
        return anime[currentAnimation].getSlide(facingLeft);
    }

    public void update() {
        super.update();

        if (footContacts > 0 && Math.abs(this.getBody().getLinearVelocity().x) > 0) {
            currentAnimation = WALKING;
        } else {
            currentAnimation = STATIONARY;
        }

        if (this.getBody().getLinearVelocity().x < 0) {
            facingLeft = true;
        } else if (this.getBody().getLinearVelocity().x > 0) {
            facingLeft = false;
        }

        anime[currentAnimation].update();
    }

    public void setHealth(int h) {
        health = h;
    }

    public void damage(int d) {
        health -= d;
        recentdmg += d;
        dmg = System.currentTimeMillis();
        damaged = true;

        if (health <= 0) {
            destroy();
        }
    }

    @Override
    public void destroy() {
        if (isDestroyed())
            return;

        getLocation().getWorld().unregisterEntity(this);

        Item[][] contents = getInventory().getContents();

        for (int x = 0; x < contents.length; x++) {
            for (int y = 0; y < contents[0].length; y++) {
                if (contents[x][y] != null && contents[x][y] instanceof MaterialStack) {
                    MaterialStack t = (MaterialStack) contents[x][y];
                    new DropEntity(t.getType()).spawn(getLocation());
                }
            }
        }

        destroyed = true;
    }
  
    public int getHealth() {
        return health;
    }
    
    public int getFootContacts() {
    	return footContacts;
    }
    
    public void setFootContacts(int n) {
    	if (n >= 0 && n <= 100) {
    		footContacts = n;
    	}
    }

    public Inventory getInventory() {
        return inv;
    }

    @Override
    public String toString() {
        return getLocation() + "\n" + getInventory();
    }

    @Override
    public void spawn(Location l) {
        super.spawn(l);
        FixtureDef fd = new FixtureDef();

        fd.userData = CollisionListener.FOOT_SENSOR;

        fd.isSensor = true;
        PolygonShape ps = new PolygonShape();
        ps.setAsBox(this.getWidth() / 2.0f - 0.2f, 0.1f, new Vec2(0, -(this.getHeight() / 2.0f)), 0);
        fd.shape = ps;

        this.getBody().createFixture(fd);
    }
}