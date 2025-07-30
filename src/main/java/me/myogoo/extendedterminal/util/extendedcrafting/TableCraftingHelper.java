package me.myogoo.extendedterminal.util.extendedcrafting;

import me.myogoo.extendedterminal.api.adapter.recipe.ITableRecipeAdapter;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;

public final class TableCraftingHelper {
    public static int getCraftingGridWidth(ITableRecipeAdapter recipe) {
        return recipe.tier() * 2 + 1;
    }

    public static int getCraftingGridHeight(ITableRecipeAdapter recipe) {
        return recipe.tier() * 2 + 1;
    }

    public static int getCraftingGridSize(ITableRecipeAdapter recipe) {
        int dim = getCraftingGridWidth(recipe);
        return dim * dim;
    }

    public static NonNullList<Ingredient> makeNxNIngredients(ITableRecipeAdapter recipe) {
        return NonNullList.withSize(getCraftingGridSize(recipe), Ingredient.EMPTY);
    }

    public static int getCraftingGridSideLength(int gridSize) {
        int width = (int) Math.sqrt(gridSize);
        if (width * width < gridSize) {
            // may not invalid?
            width++;
        }
        return width;
    }

    public static GridCoordinate indexToCoordinate(int gridSize,int width, int height) {
        return new GridCoordinate(getCraftingGridSideLength(gridSize), width, height);
    }

    public static class GridCoordinate {
        private final int sideLength;
        private final int[][] matrix;

        public GridCoordinate(int sideLength, int width, int height) {
            this.sideLength = sideLength;

            matrix = new int[sideLength][sideLength];
            int offsetX = Math.floorDiv(sideLength - width,2);
            int offsetY = Math.floorDiv(sideLength - height,2);
            for(int y = offsetY; y < offsetY + height; y++) {
                for(int x = offsetX; x < offsetX + width; x++) {
                    matrix[x][y] = 1;
                }
            }
        }

        public boolean test(int index) {
            if(index < 0 || index > sideLength * sideLength) { return false; }
            return matrix[index % sideLength][index / sideLength] == 1;
        }
    }
}