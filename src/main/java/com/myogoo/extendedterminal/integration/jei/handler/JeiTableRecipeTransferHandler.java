package com.myogoo.extendedterminal.integration.jei.handler;

import appeng.core.localization.ItemModText;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import com.myogoo.extendedterminal.integration.ItemListTermCraftingHelper;
import com.myogoo.extendedterminal.menu.extendedcrafting.ExtendedTerminalBaseMenu;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static appeng.integration.modules.itemlists.TransferHelper.*;
import static com.myogoo.extendedterminal.integration.ItemListTermCraftingHelper.getGuiSlotToIngredientMap;

public class JeiTableRecipeTransferHandler<T extends ExtendedTerminalBaseMenu> extends AbstractTableRecipeHandler<T> {
    private final IRecipeTransferHandlerHelper helper;

    public JeiTableRecipeTransferHandler(Class<T> containerClass, MenuType<T> container, RecipeType<ITableRecipe> recipeType, IRecipeTransferHandlerHelper helper) {
        super(containerClass, container, recipeType);
        this.helper = helper;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(T menu, ITableRecipe recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        if(recipe.getIngredients().isEmpty()) {
            return Result.createInCompatibleError(helper);
        }

        if(!recipe.canCraftInDimensions(menu.getCraftingGridWidth(),menu.getCraftingGridHeight())) {
            return Result.createRecipeToLargeError(helper);
        }

        boolean craftMissing = AbstractContainerScreen.hasControlDown();
        var inputSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);

        var slotToIngredientMap = getGuiSlotToIngredientMap(recipe, menu.getETMenuType().getGridSideLength());
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);

        if (missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            // All missing, can't do much...
            var missingSlotViews = missingSlots.missingSlots().stream()
                    .map(idx -> idx < inputSlots.size() ? inputSlots.get(idx) : null)
                    .filter(Objects::nonNull)
                    .toList();
            return helper.createUserErrorForMissingSlots(ItemModText.NO_ITEMS.text(), missingSlotViews);
        }

        if (!doTransfer) {
            if (missingSlots.totalSize() != 0) {
                int color = missingSlots.anyMissing() ? ORANGE_PLUS_BUTTON_COLOR : BLUE_PLUS_BUTTON_COLOR;
                return new Result.PartiallyCraftable(missingSlots, color, craftMissing);
            }
        } else {
            if(recipe instanceof ShapedTableRecipe shapedRecipe) {
                ItemListTermCraftingHelper.performTransfer(menu, recipe,craftMissing,
                        shapedRecipe.getWidth(), shapedRecipe.getHeight());
            } else {
                ItemListTermCraftingHelper.performTransfer(menu,null, recipe, craftMissing);
            }
        }

        return Result.createSuccessful();
    }

}
