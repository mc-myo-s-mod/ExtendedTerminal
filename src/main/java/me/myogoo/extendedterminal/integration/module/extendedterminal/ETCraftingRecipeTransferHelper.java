package me.myogoo.extendedterminal.integration.module.extendedterminal;

import appeng.api.stacks.AEItemKey;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.FillCraftingGridFromRecipePacket;
import appeng.integration.modules.jeirei.EncodingHelper;
import appeng.menu.me.common.GridInventoryEntry;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public final class ETCraftingRecipeTransferHelper {
    private static final Comparator<GridInventoryEntry> ENTRY_COMPARATOR = Comparator
            .comparing(GridInventoryEntry::getStoredAmount);

    private ETCraftingRecipeTransferHelper() {
    }

    public static Map<Integer, Ingredient> getGuiSlotToIngredientMap(ETTerminalMenu menu, CraftingRecipe recipe) {
        var ingredients = recipe.getIngredients();
        int gridWidth = menu.getCraftingGridWidth();
        int recipeWidth = recipe instanceof ShapedRecipe shapedRecipe ? shapedRecipe.getWidth() : gridWidth;

        var result = new HashMap<Integer, Ingredient>(ingredients.size());
        for (int i = 0; i < ingredients.size(); i++) {
            int guiSlot = (i / recipeWidth) * gridWidth + (i % recipeWidth);
            var ingredient = ingredients.get(i);
            if (!ingredient.isEmpty()) {
                result.put(guiSlot, ingredient);
            }
        }
        return result;
    }

    public static void performTransfer(ETTerminalMenu menu, CraftingRecipe recipe,
            boolean craftMissing) {
        var ingredientPriorities = EncodingHelper.getIngredientPriorities(menu, ENTRY_COMPARATOR);
        var templateItems = NonNullList.withSize(menu.getCraftingGridSize(), ItemStack.EMPTY);
        for (var entry : getGuiSlotToIngredientMap(menu, recipe).entrySet()) {
            var ingredient = entry.getValue();
            var stack = ingredientPriorities.entrySet().stream()
                    .filter(priority -> priority.getKey() instanceof AEItemKey itemKey && itemKey.matches(ingredient))
                    .max(Comparator.comparingInt(Map.Entry::getValue))
                    .map(priority -> ((AEItemKey) priority.getKey()).toStack())
                    .orElse(ingredient.getItems()[0]);
            templateItems.set(entry.getKey(), stack);
        }

        NetworkHandler.instance().sendToServer(new FillCraftingGridFromRecipePacket(recipe.getId(), templateItems, craftMissing));
    }
}
