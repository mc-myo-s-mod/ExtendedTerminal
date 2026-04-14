package me.myogoo.extendedterminal.integration.emi.extendedterminal.handler;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import me.myogoo.extendedterminal.integration.module.extendedterminal.ETStoneCutterRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;

import java.util.List;
import java.util.Map;

public class ETStonecutterRecipeHandler<T extends ETTerminalMenu> extends AbstractETRecipeHandler<T> {
    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().equals(VanillaEmiRecipeCategories.STONECUTTING);
    }

    @Override
    public List<Slot> getCraftingSlots(T menu) {
        return menu.getSlots(ETSlotSemantics.STONECUTTING_INPUT);
    }

    @Override
    public Slot getOutputSlot(T menu) {
        return menu.getSlots(ETSlotSemantics.STONECUTTING_RESULT).get(0);
    }

    @Override
    protected boolean supportsRecipe(Recipe<?> recipe, EmiRecipe emiRecipe) {
        return recipe instanceof StonecutterRecipe
                && emiRecipe.getCategory().equals(VanillaEmiRecipeCategories.STONECUTTING);
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, Recipe<?> recipe) {
        return ETStoneCutterRecipeTransferHelper.getGuiSlotToIngredientMap((StonecutterRecipe) recipe);
    }

    @Override
    protected void prepareTransfer(T menu, Recipe<?> recipe) {
        var stonecutterRecipe = (StonecutterRecipe) recipe;
        menu.setMode(ETTerminalMode.STONECUTTING);
        menu.setStoneCutterRecipeId(stonecutterRecipe.getId());
    }

    @Override
    protected void performTransfer(T menu, Recipe<?> recipe, boolean craftMissing) {
        ETStoneCutterRecipeTransferHelper.performTransfer(menu, (StonecutterRecipe) recipe, craftMissing);
    }
}
