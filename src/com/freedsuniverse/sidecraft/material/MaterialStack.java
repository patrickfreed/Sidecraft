package com.freedsuniverse.sidecraft.material;

import java.awt.Color;
import java.awt.Rectangle;

import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.engine.RenderQueueItem;

public class MaterialStack extends Item {
    private final static int Y_OFFSET = 30;
    private final static int X_OFFSET = 20;

    public MaterialStack(Material t, int am) {
        super(t, am);
    }

    public void draw(Rectangle main) {
        super.draw(main, false);
        Engine.renderString(String.valueOf(getAmount()), main.x + X_OFFSET, main.y + Y_OFFSET, Color.WHITE);
    }

    public void draw(int x, int y, boolean queue) {
        RenderQueueItem i = new RenderQueueItem(x, y, getType().getImage());
        if (queue) {
            Engine.addQueueItem(i);
        } else {
            super.draw(new Rectangle(x, y), false);
            Engine.renderString(String.valueOf(getAmount()), x + X_OFFSET, Y_OFFSET + y, Color.WHITE);
        }
    }

    public void draw(Rectangle box, boolean queue) {
        RenderQueueItem texture = new RenderQueueItem(getImage(), box);
        RenderQueueItem text = new RenderQueueItem(String.valueOf(getAmount()), box.x + X_OFFSET, box.y + Y_OFFSET, Color.white);
        if (queue) {
            Engine.addQueueItem(texture);
            Engine.addQueueItem(text);
        } else {
            super.draw(box, false);
            Engine.render(text);
        }
    }
}