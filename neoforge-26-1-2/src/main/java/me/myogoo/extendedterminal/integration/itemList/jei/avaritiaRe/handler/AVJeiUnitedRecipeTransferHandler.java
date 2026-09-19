package me.myogoo.extendedterminal.integration.itemList.jei.avaritiaRe.handler;

import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeHolder;

public class AVJeiUnitedRecipeTransferHandler<T extends UnitedTerminalMenu>
        extends AVJeiRecipeTransferHandler<T> {
    private final MyoRecipeType recipeType;

    public AVJeiUnitedRecipeTransferHandler(Class<T> menuClass, MenuType<T> menuType,
                                            IRecipeType<RecipeHolder<ITierCraftingRecipe>> recipeType,
                                            IRecipeTransferHandlerHelper helper,
                                            MyoRecipeType myoRecipeType) {
        super(menuClass, menuType, recipeType, helper);
        this.recipeType = myoRecipeType;
    }

    @Override
    protected void performTransfer(T menu, MyoTableRecipe recipe, boolean craftMissing) {
        super.performTransfer(menu, recipe, craftMissing, recipeType);
    }
}
