package com.freedsuniverse.sidecraft.input;

public class Key {
    
    public static Key W = new Key(), A = new Key(), S = new Key() ,D = new Key(), B = new Key(), I = new Key(), F5 = new Key(), SPACE = new Key();
    
    boolean down;
    
    public Key(){
        down = false;
    }
    
    public void toggle(boolean pressed) {
        if (pressed != down) {
            down = pressed;
        }
    }
    
    public boolean isDown(){
        return down;
    }
}
