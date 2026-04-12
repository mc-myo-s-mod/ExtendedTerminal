package me.myogoo.extendedterminal.integration.emi.extendedterminal.handler;

import appeng.menu.SlotSemantics;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.stack.EmiStack;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractETRecipeHandler<T extends ETTerminalMenu> implements StandardRecipeHandler<T> {
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
    public EmiPlayerInventory getInventory(AbstractContainerScreen<T> screen) {
        var list = new ArrayList<EmiStack>();
        for (Slot slot : getInputSources(screen.getMenu())) {
            list.add(EmiStack.of(slot.getItem()));
        }
        return new EmiPlayerInventory(list);
    }

    @Override
    public boolean canCraft(EmiRecipe recipe, EmiCraftContext<T> context) {
        return transferRecipe(recipe, context, false);
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<T> context) {
        return transferRecipe(recipe, context, true);
    }

    private boolean transferRecipe(EmiRecipe emiRecipe, EmiCraftContext<T> context, boolean doTransfer) {
        var menu = context.getScreenHandler();
        if (menu == null) {
            return false;
        }

        var recipe = emiRecipe.getBackingRecipe();
        if (!supportsRecipe(recipe, emiRecipe)) {
            return false;
        }

        var slotToIngredientMap = getGuiSlotToIngredientMap(menu, recipe);
        if (!slotToIngredientMap.isEmpty()
                && menu.findMissingIngredients(slotToIngredientMap).missingSlots().size() == slotToIngredientMap.size()) {
            return false;
        }

        if (doTransfer) {
            performTransfer(menu, recipe, AbstractContainerScreen.hasControlDown());
            Minecraft.getInstance().setScreen(context.getScreen());
        }
        return true;
    }

    protected abstract boolean supportsRecipe(Recipe<?> recipe, EmiRecipe emiRecipe);

    protected abstract Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, Recipe<?> recipe);

    protected abstract void performTransfer(T menu, Recipe<?> recipe, boolean craftMissing);
}
