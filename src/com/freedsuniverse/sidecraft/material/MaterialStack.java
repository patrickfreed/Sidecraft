package com.freedsuniverse.sidecraft.material;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.engine.RenderQueueItem;

public class MaterialStack {
    public final static int DEFAULT_STACK_SIZE = 64, Y_OFFSET = 30, X_OFFSET = 20;

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
        Engine.renderString(String.valueOf(getAmount()), main.x + X_OFFSET, main.y + Y_OFFSET, Color.WHITE);
    }

    public void draw(int x, int y, boolean queue) {
        RenderQueueItem i = new RenderQueueItem(x, y, getType().getImage());
        if(queue){
            Engine.addQueueItem(i);
        }else{
            Engine.render(i);
            Engine.renderString(String.valueOf(getAmount()), x + X_OFFSET, Y_OFFSET + y, Color.WHITE);
        }
    }
    
    public void draw(Point point, boolean queue) {
        draw(point.x, point.y, queue);
    }

    public void draw(Rectangle box, boolean queue) {
        RenderQueueItem texture = new RenderQueueItem(getType().getImage(), box);
        RenderQueueItem text = new RenderQueueItem(String.valueOf(getAmount()), box.x + X_OFFSET, box.y + Y_OFFSET, Color.white);
        if(queue){
            Engine.addQueueItem(texture);
            Engine.addQueueItem(text);
        }else{
            Engine.render(texture);
            Engine.render(text);
        } 
    }
}
