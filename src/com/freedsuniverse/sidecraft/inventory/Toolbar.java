package com.freedsuniverse.sidecraft.inventory;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.freedsuniverse.sidecraft.Engine;
import com.freedsuniverse.sidecraft.Sidecraft;
import com.freedsuniverse.sidecraft.material.MaterialStack;

public class Toolbar {
    private int currentIndex;

    private BufferedImage tile;
    
    private int x = 315, y = 400;

    final int Y = 407, WIDTH = 29, HEIGHT = 29;

    private Rectangle[] boxes;

    public Toolbar() {
        this.currentIndex = 0;
        x = Sidecraft.width / 2 - Sidecraft.toolbarTile.getWidth() / 2;
        y = Sidecraft.height - 60;
        //boxes = new Rectangle[]{BOX_ONE, BOX_TWO, BOX_THREE, BOX_FOUR, BOX_FIVE};
        boxes = new Rectangle[5];
        createRectangles();
        tile = Sidecraft.toolbarSelectionTile;
    }

    private void createRectangles(){
        for (int x = 0; x < 5; x++){
            boxes[x] = new Rectangle(this.x + 7 + (38 * x), this.y + 7, WIDTH, HEIGHT);
        }
    }
    
    public MaterialStack getSelectedObj() {
        return Sidecraft.player.getInventory().getAt(currentIndex, 0);
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int index) {
        if (index == 1) {
            if (currentIndex <= 3) {
                currentIndex += 1;
            }
            else {
                currentIndex = 0;
            }
        }
        else {
            if (currentIndex > 0) {
                currentIndex -= 1;
            }else{
                currentIndex = 4;
            }
        }
    }

    public void Draw() {
        Engine.render(x, y, Sidecraft.toolbarTile);

        for (int x = 0; x < boxes.length; x++) {
            MaterialStack item = Sidecraft.player.getInventory().getAt(x, 0);

            if (item != null) {
                Engine.render(boxes[x], item.getType().getImage());

                if (x == currentIndex) {
                    Engine.render(boxes[x].x - 3, boxes[x].y - 3, this.tile);
                }

                if (item.getAmount() > 0)
                    Engine.renderString(String.valueOf(Sidecraft.player.getInventory().getAt(x, 0).getAmount()), (int)boxes[x].getCenterX() + 5, (int)boxes[x].getCenterY() - 1, Color.WHITE);
            }
            else {
                if (x == currentIndex) {
                    Engine.render(boxes[x].x - 3, boxes[x].y - 3, this.tile);
                }
            }
        }
    }
}
