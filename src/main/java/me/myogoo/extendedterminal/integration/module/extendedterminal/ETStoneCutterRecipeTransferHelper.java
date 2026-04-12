package me.myogoo.extendedterminal.integration.module.extendedterminal;

import appeng.api.stacks.AEItemKey;
import appeng.integration.modules.jeirei.EncodingHelper;
import appeng.menu.me.common.GridInventoryEntry;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.network.serverbound.ETFillStonecutterGridFromRecipePacket;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.StonecutterRecipe;

import java.util.Comparator;
import java.util.Map;

public final class ETStoneCutterRecipeTransferHelper {
    private static final Comparator<GridInventoryEntry> ENTRY_COMPARATOR = Comparator
            .comparing(GridInventoryEntry::getStoredAmount);

    private ETStoneCutterRecipeTransferHelper() {
    }

    public static Map<Integer, Ingredient> getGuiSlotToIngredientMap(StonecutterRecipe recipe) {
        var ingredients = recipe.getIngredients();
        if (ingredients.isEmpty() || ingredients.get(0).isEmpty()) {
            return Map.of();
        }
        return Map.of(0, ingredients.get(0));
    }

    public static void performTransfer(ETTerminalMenu menu, StonecutterRecipe recipe, boolean craftMissing) {
        var ingredient = recipe.getIngredients().isEmpty() ? Ingredient.EMPTY : recipe.getIngredients().get(0);
        if (ingredient.isEmpty()) {
            return;
        }

        var ingredientPriorities = EncodingHelper.getIngredientPriorities(menu, ENTRY_COMPARATOR);
        var templateItem = ingredientPriorities.entrySet().stream()
                .filter(priority -> priority.getKey() instanceof AEItemKey itemKey && itemKey.matches(ingredient))
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(priority -> ((AEItemKey) priority.getKey()).toStack())
                .orElse(ingredient.getItems()[0]);

        MyotusAPI.network().sendToServer(new ETFillStonecutterGridFromRecipePacket(recipe.getId(), templateItem,
                craftMissing));
    }
}
