package me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler;

import me.myogoo.extendedterminal.integration.itemList.jei.handler.IJeiAbstractRecipeHandler;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;
import java.util.Optional;

public abstract class ETTerminalBaseRecipeTransfer<T extends ETTerminalMenu, R> implements IRecipeTransferHandler<T, R>, IJeiAbstractRecipeHandler {
    private final MenuType<T> menuType;
    private final Class<T> containerClass;
    protected IRecipeTransferHandlerHelper helper;

    public ETTerminalBaseRecipeTransfer(MenuType<T> menuType, Class<T> containerClass, IRecipeTransferHandlerHelper helper) {
        this.menuType = menuType;
        this.containerClass = containerClass;
        this.helper = helper;
    }

    protected abstract Map<Integer, Ingredient> getGuiSlotToIngredientMap(ETTerminalMenu menu, R recipeHolder);

    @Override
    public Optional<MenuType<T>> getMenuType() {
        return Optional.of(menuType);
    }

    @Override
    public Class<? extends T> getContainerClass() {
        return containerClass;
    }
}
