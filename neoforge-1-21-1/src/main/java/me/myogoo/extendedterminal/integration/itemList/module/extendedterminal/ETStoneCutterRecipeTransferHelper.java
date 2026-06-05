package me.myogoo.extendedterminal.integration.itemList.module.extendedterminal;

import appeng.api.stacks.AEItemKey;
import appeng.core.network.ServerboundPacket;
import appeng.integration.modules.itemlists.EncodingHelper;
import me.myogoo.extendedterminal.integration.itemList.module.ItemListTermCraftingHelper;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.network.serverbound.FillStonecutterGridFromRecipePacket;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ETStoneCutterRecipeTransferHelper {
    public static Map<Integer, Ingredient> getGuiSlotToIngredientMap(ETTerminalMenu menu, StonecutterRecipe recipe) {
        var ingredients = recipe.getIngredients();

        var result = new HashMap<Integer, Ingredient>(ingredients.size());
        result.put(0, ingredients.getFirst());
        return result;
    }

    public static void performTransfer(ETTerminalMenu menu, RecipeHolder<StonecutterRecipe> recipeHolder, boolean craftingMissing) {
        var recipeId = recipeHolder.id();
        var templateItem = ETStoneCutterRecipeTransferHelper.findGoodTemplateItem(menu, recipeHolder.value());

        if (menu.getPlayer().level().getRecipeManager().byKey(recipeId).isEmpty()) {
            return;
        }

        ServerboundPacket message = new FillStonecutterGridFromRecipePacket(recipeId, templateItem, craftingMissing);
        PacketDistributor.sendToServer(message);
    }

    private static ItemStack findGoodTemplateItem(ETTerminalMenu menu, SingleItemRecipe recipe) {
        var ingredientPriorities = EncodingHelper.getIngredientPriorities(menu, ItemListTermCraftingHelper.ENTRY_COMPARATOR); //보류

        var templateItems = NonNullList.withSize(1, ItemStack.EMPTY);
        var ingredients = recipe.getIngredients();

        var ingredient = ingredients.getFirst();
        if (!ingredient.isEmpty()) {
            var stack = ingredientPriorities.entrySet()
                    .stream()
                    .filter(e -> e.getKey() instanceof AEItemKey itemKey && itemKey.matches(ingredient))
                    .max(Comparator.comparingInt(Map.Entry::getValue))
                    .map(e -> ((AEItemKey) e.getKey()).toStack())
                    .orElse(ingredient.getItems()[0]);
            templateItems.set(0, stack);
        }

        return templateItems.getFirst();
    }
}
