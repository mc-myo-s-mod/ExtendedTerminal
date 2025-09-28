package me.myogoo.extendedterminal.integration.jei.extendedterminal.handler;

import me.myogoo.extendedterminal.integration.jei.handler.IAbstractRecipeHandler;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;
import java.util.Optional;

public abstract class ETTerminalBaseRecipeTransfer<R> implements IRecipeTransferHandler<ETTerminalMenu, R>, IAbstractRecipeHandler {
    protected IRecipeTransferHandlerHelper helper;

    public ETTerminalBaseRecipeTransfer(IRecipeTransferHandlerHelper helper) {
        this.helper = helper;
    }

    protected abstract Map<Integer, Ingredient> getGuiSlotToIngredientMap(ETTerminalMenu menu, R recipeHolder);

    protected abstract void performTransfer(ETTerminalMenu menu, R recipeHolder, boolean craftingMissing);

    @Override
    public Optional<MenuType<ETTerminalMenu>> getMenuType() {
        return Optional.of(ETTerminalMenu.TYPE);
    }

    @Override
    public Class<? extends ETTerminalMenu> getContainerClass() {
        return ETTerminalMenu.class;
    }
}
