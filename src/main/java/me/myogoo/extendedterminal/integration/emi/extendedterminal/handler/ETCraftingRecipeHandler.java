package me.myogoo.extendedterminal.integration.emi.extendedterminal.handler;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import me.myogoo.extendedterminal.integration.module.extendedterminal.ETCraftingRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;
import java.util.Map;

public class ETCraftingRecipeHandler<T extends ETTerminalMenu> extends AbstractETRecipeHandler<T> {
    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().equals(VanillaEmiRecipeCategories.CRAFTING);
    }

    @Override
    public List<Slot> getCraftingSlots(T menu) {
        return menu.getSlots(menu.getCraftingGridSlotSemantic());
    }

    @Override
    public Slot getOutputSlot(T menu) {
        return menu.getSlots(menu.getOutputSlotSemantic()).get(0);
    }

    @Override
    protected boolean supportsRecipe(Recipe<?> recipe, EmiRecipe emiRecipe) {
        return recipe instanceof CraftingRecipe && emiRecipe.getCategory().equals(VanillaEmiRecipeCategories.CRAFTING);
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, Recipe<?> recipe) {
        return ETCraftingRecipeTransferHelper.getGuiSlotToIngredientMap(menu, (CraftingRecipe) recipe);
    }

    @Override
    protected void prepareTransfer(T menu, Recipe<?> recipe) {
        menu.setMode(ETTerminalMode.CRAFTING);
    }

    @Override
    protected void performTransfer(T menu, Recipe<?> recipe, boolean craftMissing) {
        ETCraftingRecipeTransferHelper.performTransfer(menu, (CraftingRecipe) recipe, null, craftMissing);
    }
}
