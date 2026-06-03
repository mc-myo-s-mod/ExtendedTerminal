package me.myogoo.extendedterminal.integration.jei.extendedterminal.handler;

import appeng.core.localization.ItemModText;
import me.myogoo.extendedterminal.integration.jei.handler.IJeiAbstractRecipeHandler;
import me.myogoo.extendedterminal.integration.module.extendedterminal.ETCraftingRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ETCraftingRecipeTransfer<T extends ETTerminalMenu>
        implements IRecipeTransferHandler<T, CraftingRecipe>, IJeiAbstractRecipeHandler {
    private final MenuType<T> menuType;
    private final Class<T> containerClass;
    private final IRecipeTransferHandlerHelper helper;

    public ETCraftingRecipeTransfer(MenuType<T> menuType, Class<T> containerClass,
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
    public RecipeType<CraftingRecipe> getRecipeType() {
        return RecipeTypes.CRAFTING;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(T menu, CraftingRecipe recipe,
            IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        if (recipe.getIngredients().isEmpty()) {
            return Result.createInCompatibleError(helper);
        }
        if (!recipe.canCraftInDimensions(menu.getCraftingGridWidth(), menu.getCraftingGridHeight())) {
            return Result.createRecipeToLargeError(helper);
        }

        boolean craftMissing = AbstractContainerScreen.hasControlDown();
        List<IRecipeSlotView> inputSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);
        var slotToIngredientMap = ETCraftingRecipeTransferHelper.getJeiDisplaySlotToIngredientMap(recipe);
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);
        if (!slotToIngredientMap.isEmpty() && missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            var missingSlotViews = missingSlots.missingSlots().stream()
                    .map(idx -> idx < inputSlots.size() ? inputSlots.get(idx) : null)
                    .filter(Objects::nonNull)
                    .toList();
            return helper.createUserErrorForMissingSlots(ItemModText.NO_ITEMS.text(), missingSlotViews);
        }

        if (!doTransfer) {
            if (missingSlots.totalSize() != 0) {
                int color = missingSlots.anyMissing() ? RED_SLOT_HIGHLIGHT_COLOR : BLUE_SLOT_HIGHLIGHT_COLOR;
                return new Result.PartiallyCraftable(missingSlots, color, craftMissing);
            }
        } else {
            menu.setMode(ETTerminalMode.CRAFTING);
            ETCraftingRecipeTransferHelper.performTransfer(menu, recipe, craftMissing);
        }
        return Result.createSuccessful();
    }
}
