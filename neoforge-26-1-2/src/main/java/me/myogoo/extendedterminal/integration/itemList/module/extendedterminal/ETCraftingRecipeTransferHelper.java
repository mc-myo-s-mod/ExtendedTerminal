package me.myogoo.extendedterminal.integration.itemList.module.extendedterminal;

import appeng.core.network.ServerboundPacket;
import appeng.core.network.serverbound.FillCraftingGridFromRecipePacket;
import appeng.util.CraftingRecipeUtil;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.client.ae2helpers.ETAutoCraftingWatcher;
import me.myogoo.extendedterminal.integration.itemList.module.extendedcrafting.ECRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.HashMap;
import java.util.Map;

public class ETCraftingRecipeTransferHelper {
    public static Map<Integer, Ingredient> getGuiSlotToIngredientMap(ETTerminalMenu menu, CraftingRecipe recipe) {
        var ingredients = CraftingRecipeUtil.getIngredients(recipe);

        int width;
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            width = shapedRecipe.getWidth();
        } else {
            width = 3;
        }

        var result = new HashMap<Integer, Ingredient>(ingredients.size());
        for (int i = 0; i < ingredients.size(); i++) {
            var guiSlot = (i / width) * 3 + (i % width);
            var ingredient = ingredients.get(i);
            if (!ingredient.isEmpty()) {
                result.put(guiSlot, ingredient.get());
            }
        }
        return result;
    }

    public static Map<Integer, Ingredient> getGuiSlotToIngredientMap(UnitedTerminalMenu menu,
                                                                     MyoTableRecipe recipe) {
        return ECRecipeTransferHelper.getGuiSlotToIngredientMap(menu, recipe);
    }

    public static void performTransfer(ETTerminalMenu menu, RecipeHolder<CraftingRecipe> recipeHolder,
            boolean craftingMissing) {
        performTransfer(menu, recipeHolder.value(), recipeHolder.id(), craftingMissing);
    }

    public static void performTransfer(ETTerminalMenu menu, CraftingRecipe recipe, ResourceKey<Recipe<?>> recipeId,
            boolean craftingMissing) {
        var templateItems = MyoTableRecipe.of(recipe, recipeId).findGoodTemplateItems(menu);

        ETAutoCraftingWatcher.INSTANCE.preparePending(menu, getGuiSlotToIngredientMap(menu, recipe), craftingMissing);
        ServerboundPacket message = new FillCraftingGridFromRecipePacket(recipeId, templateItems, craftingMissing);
        ClientPacketDistributor.sendToServer(message);
    }
}
