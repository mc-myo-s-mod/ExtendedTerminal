package me.myogoo.extendedterminal.integration.itemList.module.extendedterminal;

import appeng.api.stacks.AEItemKey;
import appeng.core.network.ServerboundPacket;
import appeng.integration.modules.itemlists.EncodingHelper;
import me.myogoo.extendedterminal.integration.itemList.module.ItemListTermCraftingHelper;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.network.serverbound.FillStonecutterGridFromRecipePacket;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.Comparator;
import java.util.Map;

public class ETStoneCutterRecipeTransferHelper {
    public static Map<Integer, Ingredient> getGuiSlotToIngredientMap(ETTerminalMenu menu, StonecutterRecipe recipe) {
        return Map.of(0, recipe.input());
    }

    public static void performTransfer(ETTerminalMenu menu, RecipeHolder<StonecutterRecipe> recipeHolder, boolean craftingMissing) {
        var recipeId = recipeHolder.id();
        var templateItem = ETStoneCutterRecipeTransferHelper.findGoodTemplateItem(menu, recipeHolder.value());

        ServerboundPacket message = new FillStonecutterGridFromRecipePacket(recipeId, templateItem, craftingMissing);
        ClientPacketDistributor.sendToServer(message);
    }

    private static ItemStack findGoodTemplateItem(ETTerminalMenu menu, SingleItemRecipe recipe) {
        var ingredientPriorities = EncodingHelper.getIngredientPriorities(menu, ItemListTermCraftingHelper.ENTRY_COMPARATOR); //보류

        var ingredient = recipe.input();
        if (!ingredient.isEmpty()) {
            return ingredientPriorities.entrySet()
                    .stream()
                    .filter(e -> e.getKey() instanceof AEItemKey itemKey && itemKey.matches(ingredient))
                    .max(Comparator.comparingInt(Map.Entry::getValue))
                    .map(e -> ((AEItemKey) e.getKey()).toStack())
                    .orElseGet(() -> ingredient.items().map(Holder::value).map(Item::getDefaultInstance)
                            .findFirst().orElse(ItemStack.EMPTY));
        }

        return ItemStack.EMPTY;
    }
}
