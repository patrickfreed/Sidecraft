package com.freedsuniverse.sidecraft.material;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.engine.RenderQueueItem;

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

    public void draw(int x, int y, boolean queue) {
        RenderQueueItem i = new RenderQueueItem(x, y, getType().getImage());
        if(queue){
            Engine.addQueueItem(i);
        }else{
            Engine.render(i);
            Engine.renderString(String.valueOf(getAmount()), x + 5, y + 30, Color.WHITE);
        }
    }
    
    public void draw(Point point, boolean queue) {
        draw(point.x, point.y, queue);
    }

    public void draw(Rectangle box, boolean queue) {
        RenderQueueItem i = new RenderQueueItem(getType().getImage(), box);
        if(queue){
            Engine.addQueueItem(i);
        }else{
            Engine.render(i);
            Engine.renderString(String.valueOf(getAmount()), box.x + 5, box.y + 30, Color.WHITE);
        } 
    }
}
