package com.freedsuniverse.sidecraft.inventory;

import com.freedsuniverse.sidecraft.material.Material;
import com.freedsuniverse.sidecraft.material.MaterialStack;

public class Inventory {
    private MaterialStack[][] contents;

    final int COLUMNS = 5;
    final int ROWS = 5;

    public Inventory() {
        contents = new MaterialStack[COLUMNS][ROWS];
        contents[0][0] = new MaterialStack(Material.SILVER_ORE, 5);
        contents[4][0] = new MaterialStack(Material.TNT, 10);
    }

    public void add(MaterialStack stack) {
        for(int y = 0; y < contents[0].length; y++){
            for (int x = 0; x < contents.length; x++) {
                if (contents[x][y] != null) {
                    if (contents[x][y].getType() == stack.getType()) {
                        if (contents[x][y].getAmount() < contents[x][y].getType().getMaxStackSize()) {
                            int amount = contents[x][y].getType().getMaxStackSize() - contents[x][y].getAmount();

                            if (amount > stack.getAmount()) {
                                contents[x][ y].modifyAmount(stack.getAmount());
                                stack.modifyAmount(-1 * stack.getAmount());
                            }
                            else {
                                contents[x][ y].modifyAmount(amount);
                                stack.modifyAmount(-1 * amount);
                            }

                            if (stack.getAmount() == 0) {
                                return;
                            }
                        }
                    }
                }
            }
        }

        if (stack.getAmount() > 0) {
            for (int y = 0; y < contents.length; y++) {
                for (int x = 0; x < contents[0].length; x++) {
                    if (contents[x][ y]  == null) {
                        setAt(x, y, stack);
                        return;
                    }
                }
            }
        }
    }

    public MaterialStack getAt(int column, int row) {
        return getContents()[column][ row];
    }

    public void setAt(int column, int row, MaterialStack stack) {
        if (column < contents.length && row < contents[0].length && row > -1 && column > -1) {
            contents[column][ row] = stack;
        }
    }

    public MaterialStack[][] getContents() {
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
