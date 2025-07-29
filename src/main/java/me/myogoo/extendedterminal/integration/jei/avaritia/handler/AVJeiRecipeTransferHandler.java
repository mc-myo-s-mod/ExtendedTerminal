package me.myogoo.extendedterminal.integration.jei.avaritia.handler;

import appeng.core.localization.ItemModText;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapedTableCraftingRecipe;
import me.myogoo.extendedterminal.integration.jei.handler.AbstractTableRecipeHandler;
import me.myogoo.extendedterminal.menu.avaritia.AvaritiaTerminalBaseMenu;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static appeng.integration.modules.itemlists.TransferHelper.BLUE_PLUS_BUTTON_COLOR;
import static appeng.integration.modules.itemlists.TransferHelper.ORANGE_PLUS_BUTTON_COLOR;
import static me.myogoo.extendedterminal.integration.ItemListTermCraftingHelper.fittedCraftingMatrix;
import static me.myogoo.extendedterminal.integration.ItemListTermCraftingHelper.getGuiSlotToIngredientMap;

public class AVJeiRecipeTransferHandler<T extends AvaritiaTerminalBaseMenu> extends AbstractTableRecipeHandler<T, ITierCraftingRecipe> {
    private final IRecipeTransferHandlerHelper helper;
    public AVJeiRecipeTransferHandler(Class<T> containerClass, MenuType<T> menuType, RecipeType<ITierCraftingRecipe> recipeType, IRecipeTransferHandlerHelper helper) {
        super(containerClass, menuType, recipeType);
        this.helper = helper;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(T menu, ITierCraftingRecipe recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        if(recipe.getIngredients().isEmpty()) {
            return Result.createInCompatibleError(helper);
        }

        if(!recipe.canCraftInDimensions(menu.getCraftingGridWidth(),menu.getCraftingGridHeight())) {
            return Result.createRecipeToLargeError(helper);
        }

        boolean craftMissing = AbstractContainerScreen.hasControlDown();
        var inputSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);

        var slotToIngredientMap = getGuiSlotToIngredientMap(menu, recipe);
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
            performTransfer(menu, recipe, craftMissing);
        }

        return Result.createSuccessful();
    }

    @Override
    public void performTransfer(T menu, @Nullable ITierCraftingRecipe recipe, boolean craftMissing) {

    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(AvaritiaTerminalBaseMenu menu ,ITierCraftingRecipe recipe) {
        int gridSideLength = menu.getETMenuType().getGridSideLength();
        var raw = recipe.getIngredients();
        List<Ingredient> ingredients;

        int offsetX = 0;
        int offsetY = 0;
        int width = gridSideLength;
        int height = gridSideLength;
        if (recipe instanceof ShapedTableCraftingRecipe tableRecipe) {
            ingredients = fittedCraftingMatrix(tableRecipe);
            width = tableRecipe.getWidth();
            height = tableRecipe.getHeight();
            offsetX = Math.floorDiv(gridSideLength - tableRecipe.getWidth(),2);
            offsetY = Math.floorDiv(gridSideLength - tableRecipe.getHeight(),2);
        } else {
            ingredients = raw;
        }

        int max = gridSideLength * gridSideLength;
        int count = Math.min(ingredients.size(), max);
        var result = new HashMap<Integer, Ingredient>(count);
        for (int i = 0; i < count; i++) {
            int x = i % width;
            int y = i / width;

            var guiSlot = (y + offsetY) * gridSideLength + (x + offsetX);
            var ing = ingredients.get(i);
            if (!ing.isEmpty()) {
                result.put(guiSlot, ing);
            }
        }
        return result;
    }

}
