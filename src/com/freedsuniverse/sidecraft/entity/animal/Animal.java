package com.freedsuniverse.sidecraft.entity.animal;

import java.util.Random;

import com.freedsuniverse.sidecraft.entity.LivingEntity;

public class Animal extends LivingEntity {
    private final long DECISION_TIME = 4000;

    private final float WALKING_SPEED;
    private float speed;

    private long lastDecision = 0;

    public Animal(String id, int w, int h, int hp, float mass) {
        super(id, w, h, hp, mass);
        WALKING_SPEED = 3.0f;
    }

    public void update() {
        super.update();

        if (this.getBody().getLinearVelocity().y == 0) {
            if (System.currentTimeMillis() - lastDecision >= DECISION_TIME) {
                System.out.println(makeNewDecision());
            } else {
                this.b.getLinearVelocity().x = speed;
            }
        }
    }

    public String makeNewDecision() {
        Random rnd = new Random();
        lastDecision = System.currentTimeMillis();

        switch (rnd.nextInt(3)) {
        case 0: {
            this.b.getLinearVelocity().x = 0;
            this.speed = 0;
            return "stand still";
        }
        case 1: {
            this.b.getLinearVelocity().x = WALKING_SPEED;
            this.speed = WALKING_SPEED;
            return "walk to the right";
        }
        default: {
            this.b.getLinearVelocity().x = -WALKING_SPEED;
            this.speed = -WALKING_SPEED;
            return "walk to the left";
        }
        }
    }
}
