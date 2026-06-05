package me.myogoo.extendedterminal.integration.itemList.module;

import appeng.menu.me.common.GridInventoryEntry;
import me.myogoo.extendedterminal.util.TableCraftingHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.*;

import static me.myogoo.extendedterminal.network.serverbound.FillTableCraftingGridFromRecipePacket.NOT_SET_RECIPE_SIZE;

public class ItemListTermCraftingHelper {
    public static final Comparator<GridInventoryEntry> ENTRY_COMPARATOR = Comparator
            .comparing(GridInventoryEntry::getStoredAmount);

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
}
