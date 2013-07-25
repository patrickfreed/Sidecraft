package com.freedsuniverse.sidecraft.inventory;

import com.freedsuniverse.sidecraft.material.Item;
import com.freedsuniverse.sidecraft.material.MaterialStack;
import com.freedsuniverse.sidecraft.material.tools.Pickaxe;
import com.freedsuniverse.sidecraft.material.tools.Quality;

public class Inventory {
    private Item[][] contents;
    
    public final static int ROWS = 4;
    public final static int COLUMNS = 5;
    
    public Inventory() {
        contents = new Item[ROWS][COLUMNS];
        contents[0][0] = new Pickaxe(Quality.STONE);
        contents[0][1] = new Pickaxe(Quality.IRON);
        contents[0][2] = new Pickaxe(Quality.SILVER);
    }
    
    public void add(Item type) {
        for(int y = 0; y < contents.length; y++){
            for (int x = 0; x < contents[0].length; x++) {
                if (contents[y][x] != null) {
                    if (contents[y][x].getType() == type.getType()) {
                        if (contents[y][x].getAmount() < contents[y][x].getType().getMaxStackSize()) {
                            int amount = contents[y][x].getType().getMaxStackSize() - contents[y][x].getAmount() + 1;

                            if (amount > type.getAmount()) {
                                contents[y][x].modifyAmount(type.getAmount());
                                type.modifyAmount(-1 * type.getAmount());
                            }else {
                                contents[y][x].setAmount(amount);
                                type.modifyAmount(-1 * amount);
                            }

                            if (type.getAmount() == 0) {
                                return;
                            }
                        }
                    }
                }
            }
        }

        if (type.getAmount() > 0) {
            for (int y = 0; y < contents.length; y++) {
                for (int x = 0; x < contents[0].length; x++) {
                    if (contents[y][x]  == null) {
                        setAt(x, y, type);
                        return;
                    }
                }
            }
        }
    }

    public Item getAt(int column, int row) {
        return getContents()[row][column];
    }

    public void setAt(int column, int row, Item handled) {
        if (row < contents.length && column < contents[0].length && column > -1 && row > -1) {
            contents[row][column] = handled;
        }
    }

    public Item[][] getContents() {
        return this.contents;
    }

    public void setContents(MaterialStack[][] newc) {
        if (newc.length == contents.length && newc[0].length == contents[0].length) {
            this.contents = newc;
        }
    }

    public int[] getIndex(MaterialStack stack) {
        for(int y = 0; y < contents.length; y++){
            for (int x = 0; x < contents[0].length; x++) {
                if (contents[y][x] != null) {
                    if (contents[y][x].getType().getId() == stack.getType().getId()) {
                        return new int[] {x, y};
                    }
                }
            }
        }
        return new int[]{-1, -1};
    }
}
