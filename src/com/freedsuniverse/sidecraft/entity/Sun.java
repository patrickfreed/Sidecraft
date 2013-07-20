package com.freedsuniverse.sidecraft.entity;

import java.awt.Color;

import com.freedsuniverse.sidecraft.Sidecraft;
import com.freedsuniverse.sidecraft.engine.Light;

public class Sun extends LightSource {
    private final static Color MIDDAY = new Color(0, 0, 0, 255);
    private final static long DAY_LENGTH = 10000;
    private final static Light[] stages = {new Light(MIDDAY), new Light(MIDDAY), new Light(MIDDAY), new Light(Color.yellow, 200), new Light(Color.orange, 200), new Light(Color.red, 200), 
        new Light(Color.darkGray, 150), new Light(Color.black, 100), new Light(Color.black, 100), new Light(Color.black, 100), new Light(Color.darkGray, 150), new Light(Color.orange, 200)}; 
    
    private long time, lasttime;
    private int day, stage, increase;
    
    public Sun() {
        super(MIDDAY, 255, 25);
        time = 0;
        lasttime = System.currentTimeMillis();
        day = 1;
        stage = 0;
        increase = (int) (DAY_LENGTH / stages.length);
    }
    
    public void update() {
        setLocation(Sidecraft.playerLoc.modify(0, 10));
        
        time += System.currentTimeMillis() - lasttime;
        lasttime = System.currentTimeMillis();

        int oldstage = stage;
        stage = (int) (time / increase);
        
        if(stage >= stages.length) {
            getLocation().getWorld().lightUpdate();
            day++;
            time = 0;
            System.out.println("Day " + day);
        }else if(stage != oldstage) {
            setLight(stages[stage], 25);
            getLocation().getWorld().lightUpdate();
        }
        
        super.update();
    }
}
