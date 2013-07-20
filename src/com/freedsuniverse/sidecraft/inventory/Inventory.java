package com.freedsuniverse.sidecraft.inventory;

import com.freedsuniverse.sidecraft.material.Item;
import com.freedsuniverse.sidecraft.material.MaterialStack;
import com.freedsuniverse.sidecraft.material.tools.Pickaxe;
import com.freedsuniverse.sidecraft.material.tools.Quality;

public class Inventory {
    private Item[][] contents;
    
    public final static int COLUMNS = 5;
    public final static int ROWS = 4;
    
    public Inventory() {
        contents = new Item[COLUMNS][ROWS];
        contents[0][0] = new Pickaxe(Quality.STONE);
        contents[1][0] = new Pickaxe(Quality.IRON);
        contents[2][0] = new Pickaxe(Quality.SILVER);
    }
    
    public void add(Item type) {
        for(int y = 0; y < contents[0].length; y++){
            for (int x = 0; x < contents.length; x++) {
                if (contents[x][y] != null) {
                    if (contents[x][y].getType() == type.getType()) {
                        if (contents[x][y].getAmount() < contents[x][y].getType().getMaxStackSize()) {
                            int amount = contents[x][y].getType().getMaxStackSize() - contents[x][y].getAmount();

                            if (amount > type.getAmount()) {
                                contents[x][y].modifyAmount(type.getAmount());
                                type.modifyAmount(-1 * type.getAmount());
                            }
                            else {
                                contents[x][y].setAmount(amount);
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
                    if (contents[x][ y]  == null) {
                        setAt(x, y, type);
                        return;
                    }
                }
            }
        }
    }

    public Item getAt(int column, int row) {
        return getContents()[column][ row];
    }

    public void setAt(int column, int row, Item handled) {
        if (column < contents.length && row < contents[0].length && row > -1 && column > -1) {
            contents[column][ row] = handled;
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
        for(int x = 0; x < contents.length; x++){
            for (int y = 0; y < contents[0].length; y++) {
                if (contents[x][ y] != null) {
                    if (contents[x][ y].getType().getId() == stack.getType().getId()) {
                        return new int[] { x, y };
                    }
                }
            }
        }
        return new int[]{-1, -1};
    }
}
