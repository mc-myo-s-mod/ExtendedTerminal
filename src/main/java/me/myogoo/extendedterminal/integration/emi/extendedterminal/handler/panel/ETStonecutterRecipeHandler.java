package me.myogoo.extendedterminal.integration.emi.extendedterminal.handler.panel;

import appeng.core.localization.ItemModText;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import me.myogoo.extendedterminal.api.adapter.recipe.ITableRecipeAdapter;
import me.myogoo.extendedterminal.integration.emi.extendedterminal.handler.ETTerminalBaseRecipeHandler;
import me.myogoo.extendedterminal.integration.module.extendedterminal.ETStoneCutterRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;

import java.util.Map;

public class ETStonecutterRecipeHandler<T extends ETTerminalMenu> extends ETTerminalBaseRecipeHandler<T> {
    public ETStonecutterRecipeHandler(Class<T> containerClass) {
        super(containerClass);
    }

    @Override
    protected Result transferRecipe(T menu, EmiRecipe emiRecipe, boolean doTransfer) {
        var recipeId = emiRecipe.getId();
        var recipe = emiRecipe.getBackingRecipe();
        boolean craftingRecipe = isCraftingRecipe(recipe, emiRecipe);
        if (!craftingRecipe || recipe == null) {
            return Result.createNotApplicable();
        }
        if (!recipe.canCraftInDimensions(1, 1)) {
            return Result.createFailed(ItemModText.RECIPE_TOO_LARGE.text());
        }
        if (!(recipe instanceof StonecutterRecipe singleItemRecipe)) {
            return Result.createFailed(ItemModText.INCOMPATIBLE_RECIPE.text());
        }

        var slotToIngredientMap = ETStoneCutterRecipeTransferHelper.getGuiSlotToIngredientMap(singleItemRecipe);
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);

        if (missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            return Result.createFailed(ItemModText.NO_ITEMS.text(), missingSlots.missingSlots(),
                    slotToIngredientMap.keySet());
        }

        if (!doTransfer) {
            if (missingSlots.anyMissingOrCraftable()) {
                return new Result.PartiallyCraftable(missingSlots, slotToIngredientMap.keySet());
            }
        } else {
            boolean craftingMissing = AbstractContainerScreen.hasControlDown();
            ETStoneCutterRecipeTransferHelper.performTransfer(menu, singleItemRecipe, craftingMissing);
        }

        return Result.createSuccessful();
    }

    @Override
    protected boolean isCraftingRecipe(Recipe<?> recipe, EmiRecipe emiRecipe) {
        return emiRecipe.getCategory().equals(VanillaEmiRecipeCategories.STONECUTTING);
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().equals(VanillaEmiRecipeCategories.STONECUTTING);
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, ITableRecipeAdapter<?> recipe) {
        return Map.of();
    }
}
