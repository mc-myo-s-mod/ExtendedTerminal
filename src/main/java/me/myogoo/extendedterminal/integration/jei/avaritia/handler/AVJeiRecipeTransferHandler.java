package me.myogoo.extendedterminal.integration.jei.avaritia.handler;

import appeng.core.localization.ItemModText;
import appeng.core.network.ServerboundPacket;
import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapedTableCraftingRecipe;
import me.myogoo.extendedterminal.api.adapter.recipe.ITableRecipeAdapter;
import me.myogoo.extendedterminal.integration.jei.handler.AbstractTableRecipeHandler;
import me.myogoo.extendedterminal.menu.avaritia.AvaritiaTerminalBaseMenu;
import me.myogoo.extendedterminal.network.serverbound.FillTableCraftingGridFromRecipePacket;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static appeng.integration.modules.itemlists.TransferHelper.BLUE_PLUS_BUTTON_COLOR;
import static appeng.integration.modules.itemlists.TransferHelper.ORANGE_PLUS_BUTTON_COLOR;
import static me.myogoo.extendedterminal.integration.ItemListTermCraftingHelper.*;
import static me.myogoo.extendedterminal.network.serverbound.FillTableCraftingGridFromRecipePacket.NOT_SET_RECIPE_SIZE;

public class AVJeiRecipeTransferHandler<T extends AvaritiaTerminalBaseMenu> extends AbstractTableRecipeHandler<T, ITierCraftingRecipe> {
    private final IRecipeTransferHandlerHelper helper;

    public AVJeiRecipeTransferHandler(Class<T> containerClass, MenuType<T> menuType, RecipeType<ITierCraftingRecipe> recipeType, IRecipeTransferHandlerHelper helper) {
        super(containerClass, menuType, recipeType);
        this.helper = helper;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(T menu, ITierCraftingRecipe recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        if (recipe.getIngredients().isEmpty()) {
            return Result.createInCompatibleError(helper);
        }

        if (!recipe.canCraftInDimensions(menu.getCraftingGridWidth(), menu.getCraftingGridHeight())) {
            return Result.createRecipeToLargeError(helper);
        }

        boolean craftMissing = AbstractContainerScreen.hasControlDown();
        var inputSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);

        var slotToIngredientMap = getGuiSlotToIngredientMap(ITableRecipeAdapter.of(recipe), menu.getETMenuType().getGridSideLength());
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
        var templateItems = findGoodTemplateItems(ITableRecipeAdapter.of(recipe), menu);
        int recipeWidth = NOT_SET_RECIPE_SIZE;
        int recipeHeight = NOT_SET_RECIPE_SIZE;
        if (recipe instanceof ShapedTableCraftingRecipe shapedRecipe) {
            recipeWidth = shapedRecipe.getWidth();
            recipeHeight = shapedRecipe.getHeight();
        }
        ServerboundPacket message = new FillTableCraftingGridFromRecipePacket(templateItems, craftMissing,
                recipeWidth, recipeHeight);
        PacketDistributor.sendToServer(message);

    }
}
