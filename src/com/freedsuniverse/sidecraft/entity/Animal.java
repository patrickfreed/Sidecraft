package com.freedsuniverse.sidecraft.entity;

import java.util.Random;

import com.freedsuniverse.sidecraft.Settings;

public class Animal extends LivingEntity{  
    private final long DECISION_TIME = 4000;
    private long lastDecision = 0;
    
    public Animal(String id, int w, int h, int hp) {
        super(id, w, h, hp);
    }
    
    public void update() {  
        super.update();
        
        if(System.currentTimeMillis() - lastDecision >= DECISION_TIME) {           
            System.out.println(id + " has made a new decision to " + makeNewDecision() + "!");
        }     
    }

    public String makeNewDecision() {
        Random rnd = new Random();
        lastDecision = System.currentTimeMillis();
        
        switch(rnd.nextInt(3)) {
            case 0: {
                xSpeed = 0;
                return "stand still";
            }
            case 1: {
                xSpeed = 1;
                xDirection = Settings.RIGHT;
                return "walk to the right";
            }
            default: {
                xSpeed = 1;
                xDirection = Settings.LEFT;
                return "walk to the left";
            }
        }       
    }
}
