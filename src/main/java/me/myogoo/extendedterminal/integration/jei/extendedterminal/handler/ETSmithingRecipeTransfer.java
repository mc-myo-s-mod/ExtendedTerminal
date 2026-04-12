package me.myogoo.extendedterminal.integration.jei.extendedterminal.handler;

import appeng.core.localization.ItemModText;
import me.myogoo.extendedterminal.integration.module.extendedterminal.ETSmithingRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ETSmithingRecipeTransfer<T extends ETTerminalMenu> implements IRecipeTransferHandler<T, SmithingRecipe> {
    private final MenuType<T> menuType;
    private final Class<T> containerClass;
    private final IRecipeTransferHandlerHelper helper;

    public ETSmithingRecipeTransfer(MenuType<T> menuType, Class<T> containerClass,
            IRecipeTransferHandlerHelper helper) {
        this.menuType = menuType;
        this.containerClass = containerClass;
        this.helper = helper;
    }

    @Override
    public Class<? extends T> getContainerClass() {
        return containerClass;
    }

    @Override
    public Optional<MenuType<T>> getMenuType() {
        return Optional.of(menuType);
    }

    @Override
    public RecipeType<SmithingRecipe> getRecipeType() {
        return RecipeTypes.SMITHING;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(T menu, SmithingRecipe recipe,
            IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        if (!recipe.canCraftInDimensions(3, 1)) {
            return helper.createUserErrorWithTooltip(ItemModText.RECIPE_TOO_LARGE.text());
        }

        var slotToIngredientMap = ETSmithingRecipeTransferHelper.getGuiSlotToIngredientMap(recipe);
        if (!slotToIngredientMap.isEmpty()
                && menu.findMissingIngredients(slotToIngredientMap).missingSlots().size() == slotToIngredientMap.size()) {
            return helper.createUserErrorWithTooltip(ItemModText.NO_ITEMS.text());
        }

        if (doTransfer) {
            ETSmithingRecipeTransferHelper.performTransfer(menu, recipe, AbstractContainerScreen.hasControlDown());
        }
        return null;
    }
}
