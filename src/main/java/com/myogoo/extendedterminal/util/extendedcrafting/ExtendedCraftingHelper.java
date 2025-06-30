package com.myogoo.extendedterminal.util.extendedcrafting;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;

public final class ExtendedCraftingHelper {
    public static int getCraftingGridWidth(ITableRecipe recipe) {
        return recipe.getTier() * 2 + 1;
    }

    public static int getCraftingGridHeight(ITableRecipe recipe) {
        return recipe.getTier() * 2 + 1;
    }

    public static int getCraftingGridSize(ITableRecipe recipe) {
        int dim = getCraftingGridWidth(recipe);
        return dim * dim;
    }

    public static NonNullList<Ingredient> makeNxNIngredients(ITableRecipe recipe) {
        return NonNullList.withSize(getCraftingGridSize(recipe), Ingredient.EMPTY);
    }

    public static int getCraftingGridSize(int gridSize) {
        int width = (int) Math.sqrt(gridSize);
        if (width * width < gridSize) {
            // not invalid
            width++;
        }
        return width;
    }

    public static GridCoordinate indexToCoordinate(int gridSize,int width, int height) {
        return new GridCoordinate(getCraftingGridSize(gridSize), width, height);
    }

    public static class GridCoordinate {
        private final int maxSize;
        private final int[][] matrix;
        public GridCoordinate(int gridSize, int width, int height) {
            this.maxSize = gridSize;

            matrix = new int[gridSize][gridSize];
            int offsetX = Math.floorDiv(gridSize - width,2);
            int offsetY = Math.floorDiv(gridSize - height,2);
            for(int y = offsetY; y < offsetY + height; y++) {
                for(int x = offsetX; x < offsetX + width; x++) {
                    matrix[x][y] = 1;
                }
            }
        }

        public boolean test(int index) {
            if(index < 0 || index > maxSize * maxSize) { return false; }
            return matrix[index % maxSize][index / maxSize] == 1;
        }
    }
}