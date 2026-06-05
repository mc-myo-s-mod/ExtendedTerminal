package me.myogoo.extendedterminal.integration.itemList.emi.extendedcrafting.handler;

import com.blakebr0.cucumber.container.BaseContainerMenu;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public record ECTableRecipeHandler<T extends BaseContainerMenu>(EmiRecipeCategory category,
                                                                int gridSize) implements StandardRecipeHandler<T> {
    @Override
    public List<Slot> getInputSources(T menu) {
        return menu.slots;
    }

    @Override
    public List<Slot> getCraftingSlots(T menu) {
        ArrayList<Slot> slots = new ArrayList<Slot>();
        for (int i = 1; i <= this.gridSize; i++) {
            slots.add(menu.getSlot(i));
        }
        return slots;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        ResourceLocation categoryId = recipe.getCategory().getId();
        return categoryId.equals(this.category.getId());
    }
}
