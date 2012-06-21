package com.freedsuniverse.sidecraft.material;

import java.awt.Color;
import java.awt.Rectangle;

import com.freedsuniverse.sidecraft.Engine;

public class MaterialStack {
    public final static int DEFAULT_STACK_SIZE = 64;

    private Material type;

    private int amount;

    public MaterialStack(Material t, int am) {
        this.type = t;
        this.amount = am;
    }

    public Material getType() {
        return this.type;
    }

    public int getAmount() {
        return this.amount;
    }

    public void modifyAmount(int newAmount) {
        this.amount += newAmount;
    }

    public void draw(Rectangle main) {
        Engine.render(main, getType().getImage());
        Engine.renderString(String.valueOf(getAmount()), main.x + 5, main.y - 1, Color.WHITE);
    }
}
