package com.freedsuniverse.sidecraft.input;

public class Key {
    
    public static Key W = new Key(), A = new Key(), S = new Key() ,D = new Key(), B = new Key(), I = new Key(), F5 = new Key(), SPACE = new Key(),
        ONE = new Key(), TWO = new Key(), THREE = new Key(), FOUR = new Key(), FIVE = new Key();
    
    boolean down, old, current;
    
    public Key(){
        down = false;
        old = false;
        current = false;
    }
    
    public void toggle(boolean pressed) {
        if (pressed != down) {
            down = pressed;
        }
    }
    
    public void update(){
        old = current;
        current = isDown();
    }
    
    public boolean toggled(){
        return old == false && current == true;
    }
    
    public boolean isDown(){
        return down;
    }
}
