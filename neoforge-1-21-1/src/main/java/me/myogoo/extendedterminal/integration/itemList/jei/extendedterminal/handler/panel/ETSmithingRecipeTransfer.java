package me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler.panel;

import appeng.core.localization.ItemModText;
import me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler.ETTerminalBaseRecipeTransfer;
import me.myogoo.extendedterminal.integration.itemList.module.extendedterminal.ETSmithingRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

import static appeng.integration.modules.itemlists.TransferHelper.BLUE_PLUS_BUTTON_COLOR;
import static appeng.integration.modules.itemlists.TransferHelper.ORANGE_PLUS_BUTTON_COLOR;


public class ETSmithingRecipeTransfer<T extends ETTerminalMenu> extends ETTerminalBaseRecipeTransfer<T,RecipeHolder<SmithingRecipe>> {

    public ETSmithingRecipeTransfer(MenuType<T> menuType, Class<T> classContainer, IRecipeTransferHandlerHelper helper) {
        super(menuType, classContainer, helper);
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(ETTerminalMenu menu, RecipeHolder<SmithingRecipe> recipeHolder) {
        var recipe = recipeHolder.value();
        return ETSmithingRecipeTransferHelper.getGuiSlotToIngredientMap(menu, recipe);
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(T menu, RecipeHolder<SmithingRecipe> recipeHolder, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        var recipe = recipeHolder.value();

        boolean craftingMissing = AbstractContainerScreen.hasControlDown();
        var inputSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);

        var slotToIngredientMap = getGuiSlotToIngredientMap(menu, recipeHolder);
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);

        if (missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            var missingSlotViews = missingSlots.missingSlots().stream()
                    .map(idx -> idx < inputSlots.size() ? inputSlots.get(idx) : null)
                    .filter(Objects::nonNull)
                    .toList();

            return helper.createUserErrorForMissingSlots(ItemModText.NO_ITEMS.text(), missingSlotViews);
        }

        if (!doTransfer) {
            if (missingSlots.totalSize() != 0) {
                int color = missingSlots.anyMissing() ? ORANGE_PLUS_BUTTON_COLOR : BLUE_PLUS_BUTTON_COLOR;
                return new Result.PartiallyCraftable(missingSlots, color, craftingMissing);
            }
        } else {
            ETSmithingRecipeTransferHelper.performTransfer(menu, recipeHolder, craftingMissing);
        }

        return Result.createSuccessful();
    }

    @Override
    public RecipeType<RecipeHolder<SmithingRecipe>> getRecipeType() {
        return RecipeTypes.SMITHING;
    }
}
