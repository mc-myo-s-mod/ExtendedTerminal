package me.myogoo.extendedterminal.integration.itemList.jei.extendedcrafting.handler;

import appeng.core.localization.ItemModText;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.api.adapter.recipe.table.IShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.integration.itemList.jei.handler.AbstractTableHolderRecipeHandler;
import me.myogoo.extendedterminal.integration.itemList.jei.handler.IJeiAbstractRecipeHandler;
import me.myogoo.extendedterminal.integration.itemList.module.extendedcrafting.ECRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Optional;

import static appeng.integration.modules.itemlists.TransferHelper.BLUE_PLUS_BUTTON_COLOR;
import static appeng.integration.modules.itemlists.TransferHelper.ORANGE_PLUS_BUTTON_COLOR;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;

public class ECJeiRecipeTransferHandler<T extends ETTerminalBaseMenu<?>> extends AbstractTableHolderRecipeHandler<T, ITableRecipe, RecipeHolder<ITableRecipe>> {
    private final IRecipeTransferHandlerHelper helper;
    private final @Nullable MyoRecipeType recipeType;

    public ECJeiRecipeTransferHandler(Class<T> containerClass, MenuType<T> container, IRecipeType<RecipeHolder<ITableRecipe>> recipeType,
                                      IRecipeTransferHandlerHelper helper) {
        this(containerClass, container, recipeType, helper, null);
    }

    public ECJeiRecipeTransferHandler(Class<T> containerClass, MenuType<T> container, IRecipeType<RecipeHolder<ITableRecipe>> recipeType,
                                      IRecipeTransferHandlerHelper helper, @Nullable MyoRecipeType myoRecipeType) {
        super(containerClass, container, recipeType);
        this.helper = helper;
        this.recipeType = myoRecipeType;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(@NotNull T menu, RecipeHolder<ITableRecipe> recipeHolder, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        var recipe = recipeHolder.value();
        var adapterRecipe = MyoTableRecipe.of(recipe, recipeHolder.id());
        if (adapterRecipe.ensureFittedCraftingGrid().stream().noneMatch(Optional::isPresent)) {
            return Result.createInCompatibleError(helper);
        }

        if (adapterRecipe instanceof IShapedTableRecipeAdapter shaped
                && (shaped.width() > menu.getCraftingGridWidth() || shaped.height() > menu.getCraftingGridHeight())
                || adapterRecipe.ensureFittedCraftingGrid().size() > menu.getCraftingGridSize()) {
            return Result.createRecipeToLargeError(helper);
        }

        boolean craftMissing = Minecraft.getInstance().hasControlDown();
        var inputSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);

        var slotToIngredientMap = getGuiSlotToIngredientMap(menu, adapterRecipe);
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);
        Set<Integer> inputSlotKeys = slotToIngredientMap.keySet();

        if (missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            // All missing, can't do much...
            var inputSlotsByKey = IJeiAbstractRecipeHandler.getInputSlotViewsByKey(inputSlots, inputSlotKeys);
            var missingSlotViews = missingSlots.missingSlots().stream()
                    .map(inputSlotsByKey::get)
                    .filter(Objects::nonNull)
                    .toList();
            return helper.createUserErrorForMissingSlots(ItemModText.NO_ITEMS.text(), missingSlotViews);
        }

        if (!doTransfer) {
            if (missingSlots.totalSize() != 0) {
                int color = missingSlots.anyMissing() ? ORANGE_PLUS_BUTTON_COLOR : BLUE_PLUS_BUTTON_COLOR;
                return new Result.PartiallyCraftable(missingSlots, color, craftMissing, inputSlotKeys);
            }
        } else {
            performTransfer(menu, adapterRecipe, craftMissing, recipeType);
        }
        return Result.createSuccessful();
    }

    @Override
    public Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, MyoTableRecipe recipe) {
        return ECRecipeTransferHelper.getGuiSlotToIngredientMap(menu, recipe);
    }

}
