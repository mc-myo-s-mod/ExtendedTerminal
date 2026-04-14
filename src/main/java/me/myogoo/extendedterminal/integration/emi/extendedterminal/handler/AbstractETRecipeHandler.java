package me.myogoo.extendedterminal.integration.emi.extendedterminal.handler;

import appeng.core.localization.ItemModText;
import appeng.menu.SlotSemantics;
import dev.emi.emi.api.recipe.EmiRecipe;
import me.myogoo.extendedterminal.integration.emi.handler.AbstractTableRecipeHandler;
import me.myogoo.extendedterminal.api.adapter.recipe.ITableRecipeAdapter;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractETRecipeHandler<T extends ETTerminalMenu> extends AbstractTableRecipeHandler<T> {
    @SuppressWarnings("unchecked")
    protected AbstractETRecipeHandler() {
        super((Class<T>) ETTerminalMenu.class);
    }

    @Override
    public List<Slot> getInputSources(T menu) {
        var slots = new ArrayList<Slot>();
        slots.addAll(menu.getSlots(SlotSemantics.PLAYER_HOTBAR));
        slots.addAll(menu.getSlots(SlotSemantics.PLAYER_INVENTORY));
        slots.addAll(menu.getSlots(menu.getCraftingGridSlotSemantic()));
        slots.addAll(menu.getSlots(ETSlotSemantics.SMITHING_TABLE_TEMPLATE));
        slots.addAll(menu.getSlots(ETSlotSemantics.SMITHING_TABLE_BASE));
        slots.addAll(menu.getSlots(ETSlotSemantics.SMITHING_TABLE_ADDITION));
        slots.addAll(menu.getSlots(ETSlotSemantics.STONECUTTING_INPUT));
        return slots;
    }

    @Override
    protected Result transferRecipe(T menu, EmiRecipe emiRecipe, boolean doTransfer) {
        var recipe = resolveRecipe(menu, emiRecipe);
        if (!supportsRecipe(recipe, emiRecipe)) {
            return Result.createNotApplicable();
        }

        var slotToIngredientMap = getGuiSlotToIngredientMap(menu, recipe);
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);
        if (!slotToIngredientMap.isEmpty() && missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            return Result.createFailed(ItemModText.NO_ITEMS.text(), missingSlots.missingSlots());
        }

        if (!doTransfer) {
            if (missingSlots.anyMissingOrCraftable()) {
                return new Result.PartiallyCraftable(missingSlots);
            }
        } else {
            prepareTransfer(menu, recipe);
            performTransfer(menu, recipe, AbstractContainerScreen.hasControlDown());
        }

        return Result.createSuccessful();
    }

    protected abstract boolean supportsRecipe(Recipe<?> recipe, EmiRecipe emiRecipe);

    protected abstract Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, Recipe<?> recipe);

    @Nullable
    private Recipe<?> resolveRecipe(T menu, EmiRecipe emiRecipe) {
        var backingRecipe = emiRecipe.getBackingRecipe();
        if (backingRecipe != null) {
            return backingRecipe;
        }

        var recipeId = emiRecipe.getId();
        if (recipeId == null) {
            return null;
        }

        return menu.getPlayer().level().getRecipeManager().byKey(recipeId).orElse(null);
    }

    @Override
    protected boolean isCraftingRecipe(Recipe<?> recipe, EmiRecipe emiRecipe) {
        return supportsRecipe(recipe, emiRecipe);
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, ITableRecipeAdapter recipe) {
        throw new UnsupportedOperationException("ET panel handlers use recipe-specific slot mapping");
    }

    protected void prepareTransfer(T menu, Recipe<?> recipe) {
    }

    protected abstract void performTransfer(T menu, Recipe<?> recipe, boolean craftMissing);
}
