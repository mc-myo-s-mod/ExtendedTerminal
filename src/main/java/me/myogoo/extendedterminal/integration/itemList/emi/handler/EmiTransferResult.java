package me.myogoo.extendedterminal.integration.itemList.emi.handler;

import appeng.api.stacks.AEKey;
import appeng.integration.modules.emi.EmiStackHelper;
import appeng.integration.modules.itemlists.TransferHelper;
import appeng.menu.AEBaseMenu;
import appeng.menu.me.items.CraftingTermMenu;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.Widget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static appeng.integration.modules.itemlists.TransferHelper.BLUE_SLOT_HIGHLIGHT_COLOR;
import static appeng.integration.modules.itemlists.TransferHelper.RED_SLOT_HIGHLIGHT_COLOR;

public interface EmiTransferResult {
    sealed abstract class Result {
        /**
         * @return null doesn't override the default tooltip.
         */
        @Nullable
        List<Component> getTooltip(EmiRecipe recipe, EmiCraftContext<?> context) {
            return null;
        }

        abstract boolean canCraft();

        void render(EmiRecipe recipe, EmiCraftContext<? extends AEBaseMenu> context, List<Widget> widgets,
                    GuiGraphics draw) {
        }

        static final class Success extends Result {
            @Override
            boolean canCraft() {
                return true;
            }
        }

        /**
         * There are missing ingredients, but at least one is present.
         */
        public static final class PartiallyCraftable extends Result {
            private final CraftingTermMenu.MissingIngredientSlots missingSlots;

            public PartiallyCraftable(CraftingTermMenu.MissingIngredientSlots missingSlots) {
                this.missingSlots = missingSlots;
            }

            @Override
            boolean canCraft() {
                return true;
            }

            @Override
            List<Component> getTooltip(EmiRecipe recipe, EmiCraftContext<?> context) {
                // EMI caches this tooltip, we cannot dynamically react to control being held here
                return TransferHelper.createCraftingTooltip(missingSlots, false, false);
            }

            @Override
            void render(EmiRecipe recipe, EmiCraftContext<? extends AEBaseMenu> context, List<Widget> widgets,
                        GuiGraphics guiGraphics) {
                renderMissingAndCraftableSlotOverlays(getRecipeInputSlots(recipe, widgets), guiGraphics,
                        missingSlots.missingSlots(),
                        missingSlots.craftableSlots());
            }
        }

        /**
         * Indicates that some of the slots can already be crafted by the auto-crafting system.
         */
        static final class EncodeWithCraftables extends Result {
            private final Set<AEKey> craftableKeys;

            /**
             * @param craftableKeys All keys that the current system can auto-craft.
             */
            public EncodeWithCraftables(Set<AEKey> craftableKeys) {
                this.craftableKeys = craftableKeys;
            }

            @Override
            boolean canCraft() {
                return true;
            }

            @Override
            List<Component> getTooltip(EmiRecipe emiRecipe, EmiCraftContext<?> context) {
                var anyCraftable = emiRecipe.getInputs().stream()
                        .anyMatch(ing -> isCraftable(craftableKeys, ing));
                if (anyCraftable) {
                    return TransferHelper.createEncodingTooltip(true, false);
                }
                return null;
            }

            @Override
            void render(EmiRecipe recipe, EmiCraftContext<? extends AEBaseMenu> context, List<Widget> widgets,
                        GuiGraphics guiGraphics) {
                for (var widget : widgets) {
                    if (widget instanceof SlotWidget slot && isInputSlot(slot)) {
                        if (isCraftable(craftableKeys, slot.getStack())) {
                            var poseStack = guiGraphics.pose();
                            poseStack.pushPose();
                            poseStack.translate(0, 0, 400);
                            var bounds = getInnerBounds(slot);
                            guiGraphics.fill(bounds.x(), bounds.y(), bounds.right(), bounds.bottom(),
                                    BLUE_SLOT_HIGHLIGHT_COLOR);
                            poseStack.popPose();
                        }
                    }
                }
            }

            private static boolean isCraftable(Set<AEKey> craftableKeys, EmiIngredient ingredient) {
                return ingredient.getEmiStacks().stream().anyMatch(emiIngredient -> {
                    var stack = EmiStackHelper.toGenericStack(emiIngredient);
                    return stack != null && craftableKeys.contains(stack.what());
                });
            }
        }

        static final class NotApplicable extends Result {
            @Override
            boolean canCraft() {
                return false;
            }
        }

        static final class Error extends Result {
            private final Component message;
            private final Set<Integer> missingSlots;

            public Error(Component message, Set<Integer> missingSlots) {
                this.message = message;
                this.missingSlots = missingSlots;
            }

            public Component getMessage() {
                return message;
            }

            @Override
            boolean canCraft() {
                return false;
            }

            @Override
            void render(EmiRecipe recipe, EmiCraftContext<? extends AEBaseMenu> context, List<Widget> widgets,
                        GuiGraphics guiGraphics) {

                renderMissingAndCraftableSlotOverlays(getRecipeInputSlots(recipe, widgets), guiGraphics, missingSlots,
                        Set.of());
            }
        }

        public static Result.NotApplicable createNotApplicable() {
            return new Result.NotApplicable();
        }

        public static Result.Success createSuccessful() {
            return new Result.Success();
        }

        public static Result.Error createFailed(Component text) {
            return new Result.Error(text, Set.of());
        }

        public static Result.Error createFailed(Component text, Set<Integer> missingSlots) {
            return new Result.Error(text, missingSlots);
        }
    }

    private static void renderMissingAndCraftableSlotOverlays(Map<Integer, SlotWidget> inputSlots,
                                                              GuiGraphics guiGraphics,
                                                              Set<Integer> missingSlots, Set<Integer> craftableSlots) {
        for (var entry : inputSlots.entrySet()) {
            boolean missing = missingSlots.contains(entry.getKey());
            boolean craftable = craftableSlots.contains(entry.getKey());
            if (missing || craftable) {
                var poseStack = guiGraphics.pose();
                poseStack.pushPose();
                poseStack.translate(0, 0, 400);
                var innerBounds = getInnerBounds(entry.getValue());
                guiGraphics.fill(innerBounds.x(), innerBounds.y(), innerBounds.right(),
                        innerBounds.bottom(), missing ? RED_SLOT_HIGHLIGHT_COLOR : BLUE_SLOT_HIGHLIGHT_COLOR);
                poseStack.popPose();
            }
        }
    }

    private static boolean isInputSlot(SlotWidget slot) {
        return slot.getRecipe() == null;
    }

    private static Bounds getInnerBounds(SlotWidget slot) {
        var bounds = slot.getBounds();
        return new Bounds(
                bounds.x() + 1,
                bounds.y() + 1,
                bounds.width() - 2,
                bounds.height() - 2);
    }

    private static Map<Integer, SlotWidget> getRecipeInputSlots(EmiRecipe recipe, List<Widget> widgets) {
        List<EmiIngredient> input = recipe.getInputs();

        var inputSlots = new HashMap<Integer, SlotWidget>(input.size());
        for (int i = 0; i < input.size(); i++) {
            for (var widget : widgets) {
                if (widget instanceof SlotWidget slot && isInputSlot(slot)) {
                    if (slot.getStack() == input.get(i)) {
                        inputSlots.put(i, slot);
                    }
                }
            }
        }
        return inputSlots;
    }
}
