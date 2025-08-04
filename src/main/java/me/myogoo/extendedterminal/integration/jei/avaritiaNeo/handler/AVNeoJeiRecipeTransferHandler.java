package me.myogoo.extendedterminal.integration.jei.avaritiaNeo.handler;

import appeng.core.localization.ItemModText;
import appeng.core.network.ServerboundPacket;
import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapedTableCraftingRecipe;
import me.myogoo.extendedterminal.api.adapter.recipe.IShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.api.adapter.recipe.ITableRecipeAdapter;
import me.myogoo.extendedterminal.integration.jei.handler.AbstractTableRecipeHandler;
import me.myogoo.extendedterminal.menu.avaritiaNeo.NeoExtremeTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.AvaritiaTerminalBaseMenu;
import me.myogoo.extendedterminal.network.serverbound.FillTableCraftingGridFromRecipePacket;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.byAqua3.avaritia.recipe.RecipeExtremeCrafting;
import net.byAqua3.avaritia.recipe.RecipeExtremeShaped;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static appeng.integration.modules.itemlists.TransferHelper.BLUE_PLUS_BUTTON_COLOR;
import static appeng.integration.modules.itemlists.TransferHelper.ORANGE_PLUS_BUTTON_COLOR;
import static me.myogoo.extendedterminal.integration.ItemListTermCraftingHelper.ensureFittedCraftingGrid;
import static me.myogoo.extendedterminal.integration.ItemListTermCraftingHelper.findGoodTemplateItems;
import static me.myogoo.extendedterminal.network.serverbound.FillTableCraftingGridFromRecipePacket.NOT_SET_RECIPE_SIZE;

public class AVNeoJeiRecipeTransferHandler<T extends NeoExtremeTerminalMenu> extends AbstractTableRecipeHandler<T, RecipeExtremeCrafting> {
    private final IRecipeTransferHandlerHelper helper;

    public AVNeoJeiRecipeTransferHandler(Class<T> containerClass, MenuType<T> menuType, RecipeType<RecipeExtremeCrafting> recipeType, IRecipeTransferHandlerHelper helper) {
        super(containerClass, menuType, recipeType);
        this.helper = helper;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(T menu, RecipeExtremeCrafting recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        if (recipe.getIngredients().isEmpty()) {
            return Result.createInCompatibleError(helper);
        }

        if (!recipe.canCraftInDimensions(menu.getCraftingGridWidth(), menu.getCraftingGridHeight())) {
            return Result.createRecipeToLargeError(helper);
        }

        boolean craftMissing = AbstractContainerScreen.hasControlDown();
        var inputSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);
        var adapterRecipe = ITableRecipeAdapter.of(recipe);

        var slotToIngredientMap = getGuiSlotToIngredientMap(menu,adapterRecipe);
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
            performTransfer(menu, adapterRecipe, craftMissing);
        }

        return Result.createSuccessful();
    }

    @Override
    public Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, ITableRecipeAdapter recipe) {
        int gridSideLength = menu.getCraftingGridWidth();
        var raw = recipe.recipe().getIngredients();
        List<Ingredient> ingredients;

        int width = gridSideLength;
        int height = gridSideLength;
        if (recipe instanceof IShapedTableRecipeAdapter shapedRecipe) {
            ingredients = ensureFittedCraftingGrid(shapedRecipe);
            width = shapedRecipe.width();
            height = shapedRecipe.height();
        } else {
            ingredients = raw;
        }

        int max = gridSideLength * gridSideLength;
        int count = Math.min(ingredients.size(), max);
        var result = new HashMap<Integer, Ingredient>(count);
        for (int i = 0; i < count; i++) {
            int x = i % width;
            int y = i / width;

            var guiSlot = y * gridSideLength + x;
            var ing = ingredients.get(i);
            if (!ing.isEmpty()) {
                result.put(guiSlot, ing);
            }
        }
        return result;
    }
}
