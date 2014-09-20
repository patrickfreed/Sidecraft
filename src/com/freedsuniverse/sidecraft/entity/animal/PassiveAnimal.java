package com.freedsuniverse.sidecraft.entity.animal;

import java.util.ArrayList;

import com.freedsuniverse.sidecraft.entity.Entity;
import com.freedsuniverse.sidecraft.entity.LivingEntity;

public class PassiveAnimal extends Animal {

    private LivingEntity target;
    private final int COMFORT_ZONE = 5;

    public PassiveAnimal(String id, int w, int h, int hp) {
        super(id, w, h, hp, 5.0f);
    }

    public void update() {
        ArrayList<Entity> es = getLocation().getWorld().getNearbyEntities(getLocation(), 3);

        if (target != null) {
            if (target.getLocation().getDistance(getLocation()) > COMFORT_ZONE) {
                target = null;
            }
        }

        if (target == null)
            for (Entity e : es) {
                if (e instanceof AggressiveAnimal) {
                    target = (LivingEntity) e;
                    break;
                }
            }
    }
}
