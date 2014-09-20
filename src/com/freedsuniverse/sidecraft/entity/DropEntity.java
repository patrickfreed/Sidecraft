package com.freedsuniverse.sidecraft.entity;

import java.util.Random;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.ContactEdge;

import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.material.Item;
import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.material.MaterialStack;
import com.freedsuniverse.sidecraft.world.Location;

public class DropEntity extends Entity {
    final static double REACH_DISTANCE = 1.8;

    private final Item type;

    public DropEntity(Item t) {
        super();

        PolygonShape ps = new PolygonShape();
        ps.setAsBox(0.25f, 0.25f);
        fd.shape = ps;
        fd.friction = 1.35f; // These values are completely arbitrary at this
                             // point
        fd.density = 1.0f;
        bd.fixedRotation = false;

        type = t;
        setSkin(Engine.scale(t.getType().getImage(), 16, 16));
    }

    public DropEntity(Material t) {
        this(new MaterialStack(t, 1));
    }

    @Override
    public void update() {
        super.update();

        boolean found = false;
        ContactEdge list = this.b.getContactList();

        if (list != null) {
            do {
                if (list.other.getUserData() instanceof Player) {
                    found = true;
                }

                list = list.next;
            } while (list != null && !found);
        }

        if (found) {
            getLocation().getWorld().getPlayer().getInventory().add(this.type);
            destroy();
        }
    }

    public void spawn(Location loc) {
        setLocation(loc);

        this.b = loc.getWorld().preRegisterEntity(this);

        Random rnd = new Random();
        float xd = 2.0f * (rnd.nextInt() % 2 == 0 ? 1.0f : -1.0f);
        float yd = 1.0f;

        this.b.setLinearVelocity(new Vec2(xd, yd));
    }

    public String toString() {
        return "DropEntity" + ":" + this.type + ":" + getLocation();
    }
}