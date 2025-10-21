package me.myogoo.extendedterminal.integration.jei.extendedterminal.handler;

import me.myogoo.extendedterminal.integration.jei.handler.IAbstractRecipeHandler;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;
import java.util.Optional;

public abstract class ETTerminalBaseRecipeTransfer<T extends ETTerminalMenu,R> implements IRecipeTransferHandler<T, R>, IAbstractRecipeHandler {
    private final MenuType<T> menuType;
    private final Class<T> containerClass;
    protected IRecipeTransferHandlerHelper helper;

    public ETTerminalBaseRecipeTransfer(MenuType<T> menuType, Class<T> containerClass, IRecipeTransferHandlerHelper helper) {
        this.menuType = menuType;
        this.containerClass = containerClass;
        this.helper = helper;
    }

    protected abstract Map<Integer, Ingredient> getGuiSlotToIngredientMap(ETTerminalMenu menu, R recipeHolder);

    protected abstract void performTransfer(ETTerminalMenu menu, R recipeHolder, boolean craftingMissing);

    @Override
    public Optional<MenuType<T>> getMenuType() {
        return Optional.of(menuType);
    }

    @Override
    public Class<? extends T> getContainerClass() {
        return containerClass;
    }
}
