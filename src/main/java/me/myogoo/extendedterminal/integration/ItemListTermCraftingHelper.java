package me.myogoo.extendedterminal.integration;

import appeng.api.stacks.AEItemKey;
import appeng.integration.modules.itemlists.EncodingHelper;
import appeng.menu.me.common.GridInventoryEntry;
import me.myogoo.extendedterminal.api.adapter.recipe.IShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.api.adapter.recipe.IShapelessTableRecipeAdapter;
import me.myogoo.extendedterminal.api.adapter.recipe.ITableRecipeAdapter;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.util.TableCraftingHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.*;

import static me.myogoo.extendedterminal.network.serverbound.FillTableCraftingGridFromRecipePacket.NOT_SET_RECIPE_SIZE;

public class ItemListTermCraftingHelper {
    private static final Comparator<GridInventoryEntry> ENTRY_COMPARATOR = Comparator
            .comparing(GridInventoryEntry::getStoredAmount);

    public static NonNullList<ItemStack> findGoodTemplateItems(ITableRecipeAdapter recipe, ETTerminalBaseMenu<?> menu) {
        var ingredientPriorities = EncodingHelper.getIngredientPriorities(menu, ENTRY_COMPARATOR);

        var templateItems = NonNullList.withSize(menu.getCraftingGridSize(), ItemStack.EMPTY);
        var ingredients = ensureFittedCraftingGrid(recipe);
        for (int i = 0; i < ingredients.size(); i++) {
            var ingredient = ingredients.get(i);
            if (!ingredient.isEmpty()) {
                var stack = ingredientPriorities.entrySet()
                        .stream()
                        .filter(e -> e.getKey() instanceof AEItemKey itemKey && itemKey.matches(ingredient))
                        .max(Comparator.comparingInt(Map.Entry::getValue))
                        .map(e -> ((AEItemKey) e.getKey()).toStack())
                        .orElse(ingredient.getItems()[0]);
                templateItems.set(i, stack);
            }
        }
        return templateItems;
    }

    public static Map<Integer, Ingredient> getGuiSlotToIngredientMap(ITableRecipeAdapter recipe, int gridSideLength) {
        var raw = recipe.recipe().getIngredients();
        List<Ingredient> ingredients;

        int offsetX = 0;
        int offsetY = 0;
        int width = gridSideLength;
        int height = gridSideLength;
        if (recipe instanceof IShapedTableRecipeAdapter shapedRecipe) {
            ingredients = ensureFittedCraftingGrid(shapedRecipe);
            width = shapedRecipe.width();
            height = shapedRecipe.height();
            offsetX = Math.floorDiv(gridSideLength - shapedRecipe.width(),2);
            offsetY = Math.floorDiv(gridSideLength - shapedRecipe.height(),2);
        } else {
            ingredients = raw;
        }

        int max = gridSideLength * gridSideLength;
        int count = Math.min(ingredients.size(), max);
        var result = new HashMap<Integer, Ingredient>(count);
        for (int i = 0; i < count; i++) {
            int x = i % width;
            int y = i / width;

            var guiSlot = (y + offsetY) * gridSideLength + (x + offsetX);
            var ing = ingredients.get(i);
            if (!ing.isEmpty()) {
                result.put(guiSlot, ing);
            }
        }
        return result;
    }

    public static NonNullList<Ingredient> ensureNxNTableCraftingGrid(Recipe<?> recipe, int gridSize, int recipeWidth, int recipeHeight) {
        var ingredients = recipe.getIngredients();
        var expandedIngredients = NonNullList.withSize(gridSize, Ingredient.EMPTY);
        if (recipeWidth == NOT_SET_RECIPE_SIZE || recipeHeight == NOT_SET_RECIPE_SIZE) {
            for (int i = 0; i < ingredients.size(); i++) {
                var ingredient = ingredients.get(i);
                if (!ingredient.isEmpty()) {
                    expandedIngredients.set(i, ingredient);
                }
            }
        } else {
            int cursor = 0;
            var coordinator = TableCraftingHelper.indexToCoordinate(gridSize, recipeWidth, recipeHeight);

            for (int i = 0; i < expandedIngredients.size(); i++) {
                if (coordinator.test(i)) {
                    expandedIngredients.set(i, ingredients.get(cursor++));
                }
            }
        }
        return expandedIngredients;
    }

    public static NonNullList<Ingredient> ensureFittedCraftingGrid(ITableRecipeAdapter recipe) {
        var ingredients = recipe.recipe().getIngredients();
        NonNullList<Ingredient> expandedIngredients;

        if(recipe instanceof IShapedTableRecipeAdapter shapedTableRecipe) {
            int width = shapedTableRecipe.width();
            int height = shapedTableRecipe.height();
            int matrixSize = width * height;

            expandedIngredients = NonNullList.withSize(matrixSize, Ingredient.EMPTY);

            for(int h = 0; h < height; h++) {
                for(int w = 0; w < width; w++) {
                    int index = w + h * width;
                    if(index < ingredients.size()) {
                        expandedIngredients.set(index, ingredients.get(index));
                    } else {
                        expandedIngredients.set(index, Ingredient.EMPTY);
                    }
                }
            }
            return  expandedIngredients;
        } else if (recipe instanceof IShapelessTableRecipeAdapter shapelessTableRecipe) {
            expandedIngredients = TableCraftingHelper.makeNxNIngredients(shapelessTableRecipe);
            for(int i = 0; i < ingredients.size(); i++) {
                expandedIngredients.set(i, ingredients.get(i));
            }
        } else {
            expandedIngredients = NonNullList.withSize(9, Ingredient.EMPTY);
        }

        return expandedIngredients;
    }
}
