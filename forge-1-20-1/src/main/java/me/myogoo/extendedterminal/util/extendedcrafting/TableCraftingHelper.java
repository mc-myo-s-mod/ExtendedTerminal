package me.myogoo.extendedterminal.util.extendedcrafting;

import me.myogoo.extendedterminal.api.adapter.recipe.ITableRecipeAdapter;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;

public final class TableCraftingHelper {
    public static int getCraftingGridWidth(ITableRecipeAdapter<?> recipe) {
        return recipe.tier() * 2 + 1;
    }

    public static int getCraftingGridHeight(ITableRecipeAdapter<?> recipe) {
        return recipe.tier() * 2 + 1;
    }

    public static int getCraftingGridSize(ITableRecipeAdapter<?> recipe) {
        int dim = getCraftingGridWidth(recipe);
        return dim * dim;
    }

    public static NonNullList<Ingredient> makeNxNIngredients(ITableRecipeAdapter<?> recipe) {
        return NonNullList.withSize(getCraftingGridSize(recipe), Ingredient.EMPTY);
    }

    public static int getCraftingGridSideLength(int gridSize) {
        int width = (int) Math.sqrt(gridSize);
        if (width * width < gridSize) {
            // Partially filled grids still need a complete final row.
            width++;
        }
        return width;
    }

    public static GridCoordinate indexToCoordinate(int gridSize,int width, int height) {
        return new GridCoordinate(getCraftingGridSideLength(gridSize), width, height);
    }

    /** @deprecated Use {@link me.myogoo.extendedterminal.util.TableCraftingHelper.GridCoordinate}. */
    @Deprecated(forRemoval = true)
    public static class GridCoordinate extends me.myogoo.extendedterminal.util.TableCraftingHelper.GridCoordinate {
        public GridCoordinate(int sideLength, int width, int height) {
            super(sideLength, width, height);
        }

        @Override
        public boolean test(int index) {
            return super.test(index);
        }
    }
}
