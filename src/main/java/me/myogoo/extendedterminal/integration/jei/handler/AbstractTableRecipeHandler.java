package me.myogoo.extendedterminal.integration.jei.handler;

import appeng.core.localization.ItemModText;
import appeng.integration.modules.itemlists.TransferHelper;
import appeng.menu.me.items.CraftingTermMenu;
import me.myogoo.extendedterminal.api.adapter.recipe.ITableRecipeAdapter;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

import static appeng.integration.modules.itemlists.TransferHelper.*;

public abstract class AbstractTableRecipeHandler<T extends ETTerminalBaseMenu<R>, R extends Recipe<?>> implements IRecipeTransferHandler<T, R> {
    private final Class<T> containerClass;
    private final MenuType<T> menuType;
    private final RecipeType<R> recipeType;

    public AbstractTableRecipeHandler(Class<T> containerClass, MenuType<T> menuType, RecipeType<R> recipeType) {
        this.containerClass = containerClass;
        this.menuType = menuType;
        this.recipeType = recipeType;
    }

    @Override
    public @NotNull Class<? extends T> getContainerClass() {
        return containerClass;
    }

    @Override
    public @NotNull Optional<MenuType<T>> getMenuType() {
        return Optional.of(menuType);
    }

    @Override
    public @NotNull RecipeType<R> getRecipeType() {
        return recipeType;
    }

    protected abstract void performTransfer(T mene, @Nullable R recipe, boolean craftMissing);

    protected static abstract class Result implements IRecipeTransferError {
        @Override
        public @NotNull Type getType() {
            return Type.COSMETIC;
        }

        @Nullable
        public static IRecipeTransferError createSuccessful() {
            return null;
        }

        public static IRecipeTransferError createNotApplicable(IRecipeTransferHandlerHelper helper) {
            return helper.createInternalError();
        }

        public static IRecipeTransferError createInCompatibleError(IRecipeTransferHandlerHelper helper) {
            return helper.createUserErrorWithTooltip(ItemModText.INCOMPATIBLE_RECIPE.text());
        }

        public static IRecipeTransferError createRecipeToLargeError(IRecipeTransferHandlerHelper helper) {
            return helper.createUserErrorWithTooltip(ItemModText.RECIPE_TOO_LARGE.text());
        }

        public static final class PartiallyCraftable extends Result {
            private final CraftingTermMenu.MissingIngredientSlots missingSlots;
            private final boolean craftMissing;
            private final int color;
            public PartiallyCraftable(CraftingTermMenu.MissingIngredientSlots missingSlots, int color, boolean craftMissing) {
                this.missingSlots = missingSlots;
                this.craftMissing = craftMissing;
                this.color = color;
            }

            @Override
            public int getButtonHighlightColor() {
                return color;
            }

            @Override
            public void showError(GuiGraphics guiGraphics, int mouseX, int mouseY, IRecipeSlotsView slots, int recipeX,
                                  int recipeY) {
                var poseStack = guiGraphics.pose();
                poseStack.pushPose();
                poseStack.translate(recipeX, recipeY, 0);

                // 1) draw slot highlights
                var slotViews = slots.getSlotViews(RecipeIngredientRole.INPUT);
                for (int i = 0; i < slotViews.size(); i++) {
                    var slotView = slotViews.get(i);
                    boolean missing = missingSlots.missingSlots().contains(i);
                    boolean craftable = missingSlots.craftableSlots().contains(i);
                    if (missing || craftable) {
                        slotView.drawHighlight(
                                guiGraphics,
                                missing ? RED_SLOT_HIGHLIGHT_COLOR : BLUE_SLOT_HIGHLIGHT_COLOR);
                    }
                }

                poseStack.popPose();
            }

            @Override
            public int getMissingCountHint() {
                return missingSlots.missingSlots().size();
            }

            @Override
            public void getTooltip(ITooltipBuilder tooltip) {
                tooltip.addAll(TransferHelper.createCraftingTooltip(missingSlots, craftMissing,false));
            }
        }
    }

    public abstract Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, ITableRecipeAdapter recipe);
}
