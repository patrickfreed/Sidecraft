package com.freedsuniverse.sidecraft.inventory;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.freedsuniverse.sidecraft.Main;
import com.freedsuniverse.sidecraft.engine.Engine;
import com.freedsuniverse.sidecraft.engine.RenderQueueItem;
import com.freedsuniverse.sidecraft.material.Item;

public class Toolbar {
    private int currentIndex;

    private BufferedImage tile;

    private int x = 315, y = 400;

    final int Y = 407, WIDTH = 29, HEIGHT = 29;

    private Rectangle[] boxes;

    private Inventory i;

    public Toolbar(Inventory i) {
        this.i = i;
        this.currentIndex = 0;
        x = Main.getPaneWidth() / 2 - Main.toolbarTile.getWidth() / 2;
        y = Main.getPaneHeight() - 100;
        boxes = new Rectangle[5];
        createRectangles();
        tile = Main.toolbarSelectionTile;
    }

    private void createRectangles() {
        for (int x = 0; x < 5; x++) {
            boxes[x] = new Rectangle(this.x + 7 + (38 * x), this.y + 7, WIDTH, HEIGHT);
        }
    }

    public Item getSelectedObj() {
        return i.getAt(currentIndex, 0);
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void addToCurrentIndex(int index) {
        if (index == 1) {
            if (currentIndex <= 3) {
                currentIndex += 1;
            } else {
                currentIndex = 0;
            }
        } else {
            if (currentIndex > 0) {
                currentIndex -= 1;
            } else {
                currentIndex = 4;
            }
        }
    }

    public void setCurrentIndex(int newIndex) {
        currentIndex = newIndex % 5;
    }

    public void draw() {
        Engine.addQueueItem(new RenderQueueItem(x, y, Main.toolbarTile));

        for (int x = 0; x < boxes.length; x++) {
            Item item = i.getAt(x, 0);

            if (item != null) {
                Engine.addQueueItem(new RenderQueueItem(item.getType().getImage(), boxes[x]));

                if (x == currentIndex) {
                    Engine.addQueueItem(new RenderQueueItem(boxes[x].x - 3, boxes[x].y - 3, this.tile));
                }

                if (item.getAmount() > 0)
                    Engine.addQueueItem(new RenderQueueItem(String.valueOf(i.getAt(x, 0).getAmount()), (int) boxes[x].getCenterX() + 5, (int) boxes[x].getCenterY() - 1, Color.WHITE));
            } else {
                if (x == currentIndex) {
                    Engine.addQueueItem(new RenderQueueItem(boxes[x].x - 3, boxes[x].y - 3, this.tile));
                }
            }
        }
    }
}
