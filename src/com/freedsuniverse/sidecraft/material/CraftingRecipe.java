package com.freedsuniverse.sidecraft.material;

import java.util.LinkedList;

public class CraftingRecipe {
    public static final LinkedList<CraftingRecipe> recipes = new LinkedList<CraftingRecipe>();

    private int[] recipe;
    
    private Material outcome;
    
    private int amount;

    public static MaterialStack getMatch(int[] toCompare) {
        for (int x = 0; x < recipes.size(); x++) {
            if (recipes.get(x).equals(toCompare)) {
                return recipes.get(x).getOutcome();
            }
        }
        return null;
    }

    public CraftingRecipe(int[] recipe, Material outcome, int amount) {
        this.recipe = recipe;
        this.outcome = outcome;
        this.amount = amount;
    }

    public int[] getData() {
        return recipe;
    }

    public boolean equals(CraftingRecipe r) {
        return equals(r.getData());
    }

    public MaterialStack getOutcome() {
        return new MaterialStack(outcome, amount);
    }

    public boolean equals(int[] toCompare) {
        if (toCompare.length != 9) {
            int[] temp = new int[9];

            for (int x = 0; x < temp.length; x++) {
                temp[x] = -1;
            }
            temp[0] = toCompare[0];
            temp[1] = toCompare[1];
            temp[3] = toCompare[2];
            temp[4] = toCompare[3];
            toCompare = temp;
        }

        int x = 0;
        while (x < 2) {
            if (toCompare[0] == -1 && toCompare[1] == -1 && toCompare[2] == -1) {
                for (int y = 0; y < recipe.length - 3; y++) {
                    toCompare[y] = toCompare[y + 3];
                }
                toCompare[6] = -1;
                toCompare[7] = -1;
                toCompare[8] = -1;
            }
            x++;
        }

        x = 0;

        while (x < 2) {
            if (toCompare[0] == -1 && toCompare[3] == -1 && toCompare[6] == -1) {
                for (int y = 0; y < recipe.length - 1; y++) {
                    toCompare[y] = toCompare[y + 1];
                }
            }
            x++;
        }

        for (int z = 0; z < recipe.length; z++) {
            if (recipe[z] != toCompare[z]) {
                return false;
            }
        }
        return true;
    }

}
