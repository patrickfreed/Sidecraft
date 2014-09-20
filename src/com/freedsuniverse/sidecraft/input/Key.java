package com.freedsuniverse.sidecraft.input;

public class Key {
    public static Key W = new Key(), A = new Key(), S = new Key(), D = new Key(), B = new Key(), I = new Key(), F5 = new Key(), SPACE = new Key(), ESCAPE = new Key(), ONE = new Key(), TWO = new Key(), THREE = new Key(), FOUR = new Key(), FIVE = new Key(), F = new Key(),
            M = new Key();

    boolean down, old, current;

    public static void releaseAll() {
        W.toggle(false);
        A.toggle(false);
        S.toggle(false);
        D.toggle(false);
        B.toggle(false);
        F.toggle(false);
        F5.toggle(false);
        SPACE.toggle(false);
        I.toggle(false);
        ONE.toggle(false);
        TWO.toggle(false);
        THREE.toggle(false);
        FOUR.toggle(false);
        FIVE.toggle(false);
        ESCAPE.toggle(false);
    }

    public Key() {
        down = false;
        old = false;
        current = false;
    }

    public void toggle(boolean pressed) {
        if (pressed != down) {
            down = pressed;
        }
    }

    public void update() {
        old = current;
        current = isDown();
    }

    public boolean toggled() {
        return !old && current;
    }

    public boolean isDown() {
        return down;
    }
}
