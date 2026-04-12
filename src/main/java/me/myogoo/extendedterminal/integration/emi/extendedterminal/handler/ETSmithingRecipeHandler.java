package me.myogoo.extendedterminal.integration.emi.extendedterminal.handler;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import me.myogoo.extendedterminal.integration.module.extendedterminal.ETSmithingRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmithingRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ETSmithingRecipeHandler<T extends ETTerminalMenu> extends AbstractETRecipeHandler<T> {
    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().equals(VanillaEmiRecipeCategories.SMITHING);
    }

    @Override
    public List<Slot> getCraftingSlots(T menu) {
        var slots = new ArrayList<Slot>();
        slots.addAll(menu.getSlots(ETSlotSemantics.SMITHING_TABLE_TEMPLATE));
        slots.addAll(menu.getSlots(ETSlotSemantics.SMITHING_TABLE_BASE));
        slots.addAll(menu.getSlots(ETSlotSemantics.SMITHING_TABLE_ADDITION));
        return slots;
    }

    @Override
    public Slot getOutputSlot(T menu) {
        return menu.getSlots(ETSlotSemantics.SMITHING_TABLE_RESULT).get(0);
    }

    @Override
    protected boolean supportsRecipe(Recipe<?> recipe, EmiRecipe emiRecipe) {
        return recipe instanceof SmithingRecipe && emiRecipe.getCategory().equals(VanillaEmiRecipeCategories.SMITHING);
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, Recipe<?> recipe) {
        return ETSmithingRecipeTransferHelper.getGuiSlotToIngredientMap((SmithingRecipe) recipe);
    }

    @Override
    protected void performTransfer(T menu, Recipe<?> recipe, boolean craftMissing) {
        ETSmithingRecipeTransferHelper.performTransfer(menu, (SmithingRecipe) recipe, craftMissing);
    }
}
