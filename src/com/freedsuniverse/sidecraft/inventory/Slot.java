package com.freedsuniverse.sidecraft.inventory;

import java.awt.Rectangle;

import com.freedsuniverse.sidecraft.material.Item;
import com.freedsuniverse.sidecraft.material.MaterialStack;

public class Slot {
    private Item content;
    
    private Rectangle box;

    public Slot(MaterialStack stack, Rectangle box) {
        content = stack;
        this.box = box;
    }

    public Item getContent() {
        return content;
    }

    public void setContent(Item outcome) {
        content = outcome;
    }

    public Rectangle getBounds() {
        return box;
    }
}
