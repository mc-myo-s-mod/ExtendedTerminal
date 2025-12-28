package me.myogoo.extendedterminal.integration.itemList.module.extendedterminal;

import appeng.api.stacks.AEItemKey;
import appeng.core.network.ServerboundPacket;
import appeng.integration.modules.itemlists.EncodingHelper;
import me.myogoo.extendedterminal.api.adapter.recipe.smithing.ISmithingRecipeAdapter;
import me.myogoo.extendedterminal.integration.itemList.module.ItemListTermCraftingHelper;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.network.serverbound.FillSmithingGridFromRecipePacket;
import me.myogoo.extendedterminal.network.serverbound.FillStonecutterGridFromRecipePacket;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ETSmithingRecipeTransferHelper {
    private static final int SMITHING_SLOT_COUNT = 3;

    public static Map<Integer, Ingredient> getGuiSlotToIngredientMap(ETTerminalMenu menu, SmithingRecipe recipe) {
        var smithing = ISmithingRecipeAdapter.of(recipe);
        var ingredients = smithing.getIngredients();

        var result = new HashMap<Integer, Ingredient>(ingredients.size());

        for(int i = 0; i < ingredients.size(); i++) {
            result.put(i, ingredients.get(i));
        }

        return result;
    }

    public static void performTransfer(ETTerminalMenu menu, RecipeHolder<SmithingRecipe> recipeHolder, boolean craftingMissing) {
        var recipeId = recipeHolder.id();
        var templateItems = ETSmithingRecipeTransferHelper.findGoodTemplateItems(menu, ISmithingRecipeAdapter.of(recipeHolder.value()));

        if(menu.getPlayer().level().getRecipeManager().byKey(recipeId).isEmpty()) {
            return;
        }

        ServerboundPacket message = new FillSmithingGridFromRecipePacket(recipeId, templateItems, craftingMissing);
        PacketDistributor.sendToServer(message);
    }

    private static NonNullList<ItemStack> findGoodTemplateItems(ETTerminalMenu menu, ISmithingRecipeAdapter recipe) {
        var ingredientPriorities = EncodingHelper.getIngredientPriorities(menu, ItemListTermCraftingHelper.ENTRY_COMPARATOR); //보류

        var templateItems = NonNullList.withSize(SMITHING_SLOT_COUNT, ItemStack.EMPTY);
        var ingredients = recipe.getIngredients();

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

}
