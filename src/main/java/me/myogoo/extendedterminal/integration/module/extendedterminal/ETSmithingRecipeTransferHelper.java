package me.myogoo.extendedterminal.integration.module.extendedterminal;

import appeng.api.stacks.AEItemKey;
import appeng.integration.modules.jeirei.EncodingHelper;
import appeng.menu.me.common.GridInventoryEntry;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.network.serverbound.ETFillSmithingGridFromRecipePacket;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipe;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public final class ETSmithingRecipeTransferHelper {
    private static final Comparator<GridInventoryEntry> ENTRY_COMPARATOR = Comparator
            .comparing(GridInventoryEntry::getStoredAmount);

    private ETSmithingRecipeTransferHelper() {
    }

    public static Map<Integer, Ingredient> getGuiSlotToIngredientMap(SmithingRecipe recipe) {
        var ingredients = recipe.getIngredients();
        var result = new HashMap<Integer, Ingredient>(ingredients.size());
        for (int i = 0; i < ingredients.size(); i++) {
            var ingredient = ingredients.get(i);
            if (!ingredient.isEmpty()) {
                result.put(i, ingredient);
            }
        }
        return result;
    }

    public static void performTransfer(ETTerminalMenu menu, SmithingRecipe recipe, boolean craftMissing) {
        var ingredientPriorities = EncodingHelper.getIngredientPriorities(menu, ENTRY_COMPARATOR);
        var templateItems = NonNullList.withSize(3, ItemStack.EMPTY);
        for (var entry : getGuiSlotToIngredientMap(recipe).entrySet()) {
            var ingredient = entry.getValue();
            var stack = ingredientPriorities.entrySet().stream()
                    .filter(priority -> priority.getKey() instanceof AEItemKey itemKey && itemKey.matches(ingredient))
                    .max(Comparator.comparingInt(Map.Entry::getValue))
                    .map(priority -> ((AEItemKey) priority.getKey()).toStack())
                    .orElse(ingredient.getItems()[0]);
            templateItems.set(entry.getKey(), stack);
        }

        MyotusAPI.network().sendToServer(new ETFillSmithingGridFromRecipePacket(recipe.getId(), templateItems,
                craftMissing));
    }
}
