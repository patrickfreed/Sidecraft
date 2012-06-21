package com.freedsuniverse.sidecraft.inventory;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import com.freedsuniverse.sidecraft.Engine;
import com.freedsuniverse.sidecraft.Sidecraft;
import com.freedsuniverse.sidecraft.entity.DropEntity;
import com.freedsuniverse.sidecraft.input.Mouse;
import com.freedsuniverse.sidecraft.material.MaterialStack;

public class PlayerInventory extends Inventory{
    MaterialStack onMouse;

    private int x, y;
    
    private boolean isOnMouse = false, mouse1Down = false, oldMouse1Down = false;
    
    Rectangle mRect;

    final int X_DIFF = 13;
    final int Y_DIFF = 254;

    private boolean oldMouse3Down;

    private boolean mouse3Down;

    public PlayerInventory(){   
        x = Sidecraft.width / 2 - Sidecraft.inventoryTile.getWidth() / 2;
        y = Sidecraft.height / 2 - Sidecraft.inventoryTile.getHeight() / 2;
    }
    
    public void update() {   
        oldMouse1Down = mouse1Down;
        mouse1Down = Mouse.isDown(MouseEvent.BUTTON1);
        
        oldMouse3Down = mouse3Down;
        mouse3Down = Mouse.isDown(MouseEvent.BUTTON3);
        
        this.mRect = new Rectangle(Mouse.getX(), Mouse.getY(), 30, 30);
            
        if (mouse1Down && !oldMouse1Down) {
            int[] selected = getSelectedBox();
            if (selected[0] != -1) {
                if (!isOnMouse) {
                    if (getAt(selected[0], selected[1]) != null) {
                        onMouse = getAt(selected[0], selected[1]);
                        setAt(selected[0], selected[1], null);
                        isOnMouse = true;
                    }
                }
                else {
                    if (getAt(selected[0], selected[1]) == null) {
                        setAt(selected[0], selected[1], onMouse);
                        onMouse = null;
                        isOnMouse = false;
                    }
                    else {
                        MaterialStack temp = getAt(selected[0], selected[1]);
                        if (temp.getType().getId() == onMouse.getType().getId()) {
                            if (temp.getAmount() < temp.getType().getMaxStackSize()) {
                                int amountToAdd = temp.getType().getMaxStackSize() - temp.getAmount();
                                if (amountToAdd > onMouse.getAmount()) {
                                    temp.modifyAmount(onMouse.getAmount());
                                    onMouse = null;
                                    isOnMouse = false;
                                    setAt(selected[0], selected[1], temp);
                                }
                                else {
                                    temp.modifyAmount(amountToAdd);
                                    onMouse.modifyAmount(-amountToAdd);
                                }
                                return;
                            }
                        }
                        else {
                            setAt(selected[0], selected[1], onMouse);
                            onMouse = temp;
                        }
                    }
                }
            }
        }
        else if (!oldMouse3Down && mouse3Down) {
            int[] s = getSelectedBox();
            int x = s[0];
            int y = s[1];

            if (x != -1) {
                if (isOnMouse) {
                    if (getAt(x, y) == null) {
                        setAt(x, y, new MaterialStack(onMouse.getType(), 1));
                        onMouse.modifyAmount(-1);
                    }
                    else if (getAt(x, y).getType().getId() == onMouse.getType().getId()) {
                        getAt(x, y).modifyAmount(1);
                        onMouse.modifyAmount(-1);
                    }
                    else {
                        return;
                    }
                    if (onMouse.getAmount() <= 0) {
                        onMouse = null;
                        isOnMouse = false;
                    }
                }
                else {
                    if (getAt(x, y) != null) {
                        if(getAt(x, y).getAmount() == 1){
                            onMouse = getAt(x, y);
                            isOnMouse = true;
                            setAt(x,y, null);
                        }else{
                            int amount = (int)Math.floor((double)getAt(x, y).getAmount() / 2);
                            onMouse = new MaterialStack(getAt(x, y).getType(), amount);
                            getAt(x, y).modifyAmount(-amount);
                            isOnMouse = true;
                        }
                    }
                }
            }
        }
    }

    private int[] getSelectedBox() {
        Rectangle mouse = new Rectangle(Mouse.getX(), Mouse.getY(), 3, 3);

        for (int y = 0; y < this.getContents()[0].length; y++) {
            for (int x = 0; x < this.getContents().length; x++) {
                Rectangle box = new Rectangle(this.x + X_DIFF + (38 * x), this.y + Y_DIFF - (37 * y), 30, 30);
                if (mouse.intersects(box)) {
                    return new int[] { x, y };
                }
            }
        }
        return new int[] { -1, -1 };
    }

    public void draw() {           
        Engine.render(x, y, Sidecraft.inventoryTile);
        Engine.render(new Rectangle(x + 25, y + 25, 90, 90), Sidecraft.player.getSkin());

        int xDiff = 276;
        int yDiff = 305;

        for (int y = 0; y < this.getContents()[0].length; y++) {
            for (int x = 0; x < this.getContents().length; x++) {
                if (getContents()[x][y] != null) {
                    Rectangle box = new Rectangle(x + xDiff + (37 * x), y + yDiff - (38 * y), 30, 30);
                    Engine.render(box, getContents()[x][y].getType().getImage());
                    Engine.renderString(String.valueOf(getAt(x, y).getAmount()), (int)box.getCenterX() + 5, (int)box.getCenterY() - 1, Color.WHITE);
                }
            }
        }
        if (isOnMouse) {
            onMouse.draw(mRect);
        }
    }

    public void close() {
        if (isOnMouse) {
            for (int x = 0; x < onMouse.getAmount(); x++) {
                new DropEntity(onMouse.getType(), Sidecraft.player.getLocation());
            }
            onMouse = null;
            isOnMouse = false;
        }
    }
}
