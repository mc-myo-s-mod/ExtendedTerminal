package com.myogoo.extendedterminal.util.extendedcrafting;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;

public final class ExtendedCraftingHelper {
    public static int getCraftingMatrixWidth(ITableRecipe recipe) {
        return recipe.getTier() * 2 + 1;
    }

    public static int getCraftingMatrixHeight(ITableRecipe recipe) {
        return recipe.getTier() * 2 + 1;
    }

    public static int getCraftingMatrixSize(ITableRecipe recipe) {
        int dim = getCraftingMatrixWidth(recipe);
        return dim * dim;
    }

    public static NonNullList<Ingredient> makeNxNIngredients(ITableRecipe recipe) {
        return NonNullList.withSize(getCraftingMatrixSize(recipe), Ingredient.EMPTY);
    }

    public static int getCraftingMatrixSize(int matrixLength) {
        int width = (int) Math.sqrt(matrixLength);
        if (width * width < matrixLength) {
            // not invalid
            width++;
        }
        return width;
    }

    public static GridCoordinate indexToCoordinate(int matrixSize,int width, int height) {
        return new GridCoordinate(getCraftingMatrixSize(matrixSize), width, height);
    }

    public static class GridCoordinate {
        private final int width;
        private final int height;
        private final int maxSize;
        private final int[][] matrix;
        public GridCoordinate(int maxSize, int width, int height) {
            this.maxSize = maxSize;
            this.width = width;
            this.height = height;

            matrix = new int[maxSize][maxSize];
            int offsetX = Math.floorDiv(maxSize - width,2);
            int offsetY = Math.floorDiv(maxSize - height,2);
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