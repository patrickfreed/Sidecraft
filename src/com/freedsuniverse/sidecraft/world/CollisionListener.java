package com.freedsuniverse.sidecraft.world;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import com.freedsuniverse.sidecraft.entity.LivingEntity;

public class CollisionListener implements ContactListener {

    // private final int DAMAGE_THRESHOLD = 150;
    public static final int FOOT_SENSOR = 1234;

    @Override
    public void beginContact(Contact c) {
        Fixture a = c.m_fixtureA;
        Fixture b = c.m_fixtureB;

        if (a.getUserData() != null && a.getUserData().equals(FOOT_SENSOR)) {
            // System.out.println("found it");
        }

        if (a.m_isSensor && a.m_userData != null && a.getUserData().equals(FOOT_SENSOR)) {
            LivingEntity e = (LivingEntity) a.getBody().getUserData();
            e.footContacts += 1;
        }

        if (b.m_isSensor && b.m_userData != null && b.m_userData.equals(FOOT_SENSOR)) {
            LivingEntity e = (LivingEntity) b.getBody().getUserData();
            e.footContacts += 1;
        }
    }

    @Override
    public void endContact(Contact c) {
        Fixture a = c.m_fixtureA;
        Fixture b = c.m_fixtureB;

        if (a.m_isSensor && a.m_userData != null && a.m_userData.equals(FOOT_SENSOR)) {
            LivingEntity e = (LivingEntity) a.getBody().getUserData();
            e.footContacts -= 1;
        }

        if (b.m_isSensor && b.m_userData != null && b.m_userData.equals(FOOT_SENSOR)) {
            LivingEntity e = (LivingEntity) b.getBody().getUserData();
            e.footContacts -= 1;
        }
    }

    // TODO: implement
    @Override
    public void postSolve(Contact c, ContactImpulse ci) {
        if (c.getFixtureA().getBody().getUserData() instanceof LivingEntity) {
            // LivingEntity e = (LivingEntity)
            // c.getFixtureA().getBody().getUserData();

            // int magnitude = (int) ci.normalImpulses[0];

            // if(magnitude > DAMAGE_THRESHOLD) e.damage(magnitude /
            // DAMAGE_THRESHOLD);
        }

        if (c.getFixtureB().getBody().getUserData() instanceof LivingEntity) {
            // LivingEntity e = (LivingEntity)
            // c.getFixtureB().getBody().getUserData();

            // int magnitude = (int) ci.normalImpulses[0];

            // if(magnitude > DAMAGE_THRESHOLD) e.damage(magnitude /
            // DAMAGE_THRESHOLD);
        }
    }

    @Override
    public void preSolve(Contact arg0, Manifold arg1) {
    }
}
