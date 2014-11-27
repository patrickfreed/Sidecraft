package com.freedsuniverse.sidecraft.material.tools;

import java.awt.Color;

public enum Quality {
    STONE(4, Color.DARK_GRAY), 
    IRON(2, Color.GRAY), 
    SILVER(1, Color.LIGHT_GRAY);

    private int d;
    private Color c;

    private Quality(int dmg, Color col) {
        d = dmg;
        c = col;
    }

    public int getDamageModifier() {
        return d;
    }

    public Color getColor() {
        return c;
    }

    public int compare(Quality q) {
        if (q.ordinal() == this.ordinal())
            return 0;
        else if (q.ordinal() < this.ordinal())
            return -1;
        else
            return 1;
    }
}
