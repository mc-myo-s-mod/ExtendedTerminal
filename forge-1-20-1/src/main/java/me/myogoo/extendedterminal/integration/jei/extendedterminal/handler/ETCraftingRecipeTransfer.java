package me.myogoo.extendedterminal.integration.jei.extendedterminal.handler;

import appeng.core.localization.ItemModText;
import me.myogoo.extendedterminal.adapter.recipe.TableRecipeAdapters;
import me.myogoo.extendedterminal.api.adapter.recipe.ITableRecipeAdapter;
import me.myogoo.extendedterminal.integration.jei.handler.AbstractTableRecipeHandler;
import me.myogoo.extendedterminal.integration.module.extendedterminal.ETCraftingRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static appeng.integration.modules.jeirei.TransferHelper.BLUE_PLUS_BUTTON_COLOR;
import static appeng.integration.modules.jeirei.TransferHelper.BLUE_SLOT_HIGHLIGHT_COLOR;
import static appeng.integration.modules.jeirei.TransferHelper.ORANGE_PLUS_BUTTON_COLOR;
import static appeng.integration.modules.jeirei.TransferHelper.RED_SLOT_HIGHLIGHT_COLOR;

public class ETCraftingRecipeTransfer<T extends ETTerminalBaseMenu<?>>
        extends AbstractTableRecipeHandler<T, CraftingRecipe> {
    private final IRecipeTransferHandlerHelper helper;

    public ETCraftingRecipeTransfer(MenuType<T> menuType, Class<T> containerClass,
            IRecipeTransferHandlerHelper helper) {
        super(containerClass, menuType, RecipeTypes.CRAFTING);
        this.helper = helper;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(T menu, CraftingRecipe recipe,
            IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        if (recipe.getIngredients().isEmpty()) {
            return Result.createInCompatibleError(helper);
        }
        if (!recipe.canCraftInDimensions(3, 3)) {
            return Result.createRecipeToLargeError(helper);
        }

        boolean craftMissing = AbstractContainerScreen.hasControlDown();
        var inputSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);
        var adapterRecipe = TableRecipeAdapters.of(recipe);
        var slotToIngredientMap = getGuiSlotToIngredientMap(menu, adapterRecipe);
        var inputSlotKeys = getDisplayedInputSlotKeys(menu, slotToIngredientMap);
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);

        if (!slotToIngredientMap.isEmpty() && missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            var missingSlotViews = getMissingSlotViews(menu, inputSlots, missingSlots.missingSlots(), inputSlotKeys);
            return helper.createUserErrorForMissingSlots(ItemModText.NO_ITEMS.text(), missingSlotViews);
        }

        if (!doTransfer) {
            if (missingSlots.totalSize() != 0) {
                int color;
                if (menu instanceof UnitedTerminalMenu) {
                    color = missingSlots.anyMissing() ? ORANGE_PLUS_BUTTON_COLOR : BLUE_PLUS_BUTTON_COLOR;
                } else {
                    color = missingSlots.anyMissing() ? RED_SLOT_HIGHLIGHT_COLOR : BLUE_SLOT_HIGHLIGHT_COLOR;
                }
                return new Result.PartiallyCraftable(missingSlots, color, craftMissing, inputSlotKeys);
            }
        } else if (menu instanceof UnitedTerminalMenu) {
            performTransfer(menu, adapterRecipe, craftMissing, UnitedTerminalMenu.UnitedRecipeKind.VANILLA);
        } else if (menu instanceof ETTerminalMenu etMenu) {
            etMenu.setMode(ETTerminalMode.CRAFTING);
            ETCraftingRecipeTransferHelper.performTransfer(etMenu, recipe, craftMissing);
        } else {
            return Result.createNotApplicable(helper);
        }
        return Result.createSuccessful();
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, ITableRecipeAdapter<?> recipe) {
        var craftingRecipe = recipe.unwrap(CraftingRecipe.class).orElseThrow();
        if (menu instanceof UnitedTerminalMenu unitedMenu) {
            return ETCraftingRecipeTransferHelper.getGuiSlotToIngredientMap(unitedMenu, recipe);
        }
        return ETCraftingRecipeTransferHelper.getJeiDisplaySlotToIngredientMap(craftingRecipe);
    }
}
