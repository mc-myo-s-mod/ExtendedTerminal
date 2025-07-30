package me.myogoo.extendedterminal.integration;

import appeng.api.stacks.AEItemKey;
import appeng.integration.modules.itemlists.EncodingHelper;
import appeng.menu.me.common.GridInventoryEntry;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.adapter.recipe.IShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.api.adapter.recipe.IShapelessTableRecipeAdapter;
import me.myogoo.extendedterminal.api.adapter.recipe.ITableRecipeAdapter;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.util.extendedcrafting.TableCraftingHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.slf4j.Logger;

import java.util.*;

public class ItemListTermCraftingHelper {
    private static final Comparator<GridInventoryEntry> ENTRY_COMPARATOR = Comparator
            .comparing(GridInventoryEntry::getStoredAmount);

    public static NonNullList<ItemStack> findGoodTemplateItems(ITableRecipeAdapter recipe, ETTerminalBaseMenu<?> menu) {
        var ingredientPriorities = EncodingHelper.getIngredientPriorities(menu, ENTRY_COMPARATOR);

        var templateItems = NonNullList.withSize(menu.getCraftingGridSize(), ItemStack.EMPTY);
        var ingredients = ensureNxNCraftingMatrix(recipe);
        for (int i = 0; i < ingredients.size(); i++) {
            var ingredient = ingredients.get(i);
            if (!ingredient.isEmpty()) {
                // Try to find the best item. In case the ingredient is a tag, it might contain versions the
                // player doesn't actually have
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
            ingredients = fittedCraftingMatrix(shapedRecipe);
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

    public static NonNullList<Ingredient> fittedCraftingMatrix(ITableRecipeAdapter recipe) {
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

    public static NonNullList<Ingredient> ensureNxNCraftingMatrix(ITableRecipeAdapter recipe) {
        var ingredients = recipe.recipe().getIngredients();
        NonNullList<Ingredient> expandedIngredients;
        if(recipe instanceof ITableRecipeAdapter tableRecipe) {
            expandedIngredients = TableCraftingHelper.makeNxNIngredients(tableRecipe);

            if(tableRecipe instanceof IShapedTableRecipeAdapter shapedRecipe) {
                var width = shapedRecipe.width();
                var height = shapedRecipe.height();
                int matrixWidth = TableCraftingHelper.getCraftingGridWidth(shapedRecipe);
                // Map shaped recipe into center of NxN matrix
                for(int h = 0; h < height; h++) {
                    for(int w = 0; w < width; w++) {
                        var source = w + h * width;
                        var target = w + h * matrixWidth;
                        var ing = ingredients.get(source);
                        expandedIngredients.set(target, ing);
                    }
                }
            } else if (tableRecipe instanceof IShapelessTableRecipeAdapter) {
                for(int i = 0; i < ingredients.size(); i++) {
                    expandedIngredients.set(i, ingredients.get(i));
                }
            }
        } else {
            expandedIngredients = NonNullList.withSize(9, Ingredient.EMPTY);
        }
        return expandedIngredients;
    }

}
