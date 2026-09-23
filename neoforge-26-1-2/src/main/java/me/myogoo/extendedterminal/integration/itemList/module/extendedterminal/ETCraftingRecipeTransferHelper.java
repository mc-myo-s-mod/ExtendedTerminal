package me.myogoo.extendedterminal.integration.itemList.module.extendedterminal;

import appeng.core.network.ServerboundPacket;
import appeng.core.network.serverbound.FillCraftingGridFromRecipePacket;
import me.myogoo.extendedterminal.api.adapter.recipe.table.IShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.client.ae2helpers.ETAutoCraftingWatcher;
import me.myogoo.extendedterminal.integration.itemList.module.extendedcrafting.ECRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import me.myogoo.extendedterminal.network.serverbound.FillTableCraftingGridFromRecipePacket;
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
        return getGuiSlotToIngredientMap(menu, MyoTableRecipe.of(recipe, null));
    }

    public static Map<Integer, Ingredient> getGuiSlotToIngredientMap(ETTerminalMenu menu, MyoTableRecipe recipe) {
        if (recipe instanceof IShapedTableRecipeAdapter && !(recipe.get() instanceof ShapedRecipe)) {
            return ECRecipeTransferHelper.getGuiSlotToIngredientMap(menu, recipe);
        }
        var ingredients = recipe.ensureFittedCraftingGrid();

        int width;
        if (recipe instanceof IShapedTableRecipeAdapter shapedRecipe) {
            width = shapedRecipe.width();
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
        if (!MyoTableRecipe.hasCraftingLayout(recipe)) {
            return;
        }
        var adapter = MyoTableRecipe.of(recipe, recipeId);
        var templateItems = adapter.findGoodTemplateItems(menu);

        ETAutoCraftingWatcher.INSTANCE.preparePending(menu, getGuiSlotToIngredientMap(menu, adapter), craftingMissing);
        ServerboundPacket message = adapter instanceof IShapedTableRecipeAdapter shaped && !(recipe instanceof ShapedRecipe)
                ? new FillTableCraftingGridFromRecipePacket(recipeId, templateItems, craftingMissing, shaped.width(), shaped.height())
                : new FillCraftingGridFromRecipePacket(recipeId, templateItems, craftingMissing);
        ClientPacketDistributor.sendToServer(message);
    }
}
