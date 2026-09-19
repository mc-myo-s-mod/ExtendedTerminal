package me.myogoo.extendedterminal.integration.jei.extendedterminal;

import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.UnitedTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.integration.emi.extendedterminal.ETWorkstation;
import me.myogoo.extendedterminal.integration.jei.extendedterminal.handler.UnitedTerminalGuiHandler;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.itemList.jei.JEI;
import me.myogoo.myotus.api.annotation.itemList.jei.JEIGuiHandler;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.renderer.Rect2i;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

@JEI
@JEIGuiHandler
public final class ETGuiHandler {
    private ETGuiHandler() {
    }

    @MyotusSubscriber
    public static void init(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(ETTerminalScreen.class, new ETPanelGuiHandler());
        registration.addGenericGuiContainerHandler(UnitedTerminalScreen.class,
                new UnitedTerminalGuiHandler());
    }

    private static class ETPanelGuiHandler implements IGuiContainerHandler<ETTerminalScreen<?>> {
        @Override
        public @NotNull List<Rect2i> getGuiExtraAreas(ETTerminalScreen<?> screen) {
            return screen.getExclusionZones();
        }

        @Override
        public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(
                ETTerminalScreen<?> screen,
                double guiMouseX,
                double guiMouseY) {
            var mode = screen.getMenu().getMode();
            var outputSlot = screen.getMenu().getSlots(outputSlotSemantic(mode)).get(0);
            var x = mode == ETTerminalMode.STONECUTTING ? outputSlot.x : outputSlot.x - 40;
            var y = mode == ETTerminalMode.STONECUTTING ? outputSlot.y + 24 : outputSlot.y;
            var area = new Rect2i(x, y, 24, 24);
            return List.of(new IGuiClickableArea() {
                @Override
                public @NotNull Rect2i getArea() {
                    return area;
                }

                @Override
                public void onClick(IFocusFactory focusFactory, IRecipesGui recipesGui) {
                    if (ModList.get().isLoaded("emi")) {
                        ETWorkstation.showRecipes(mode);
                    } else {
                        recipesGui.showTypes(List.of(recipeTypeFor(mode)));
                    }
                }
            });
        }

        private static SlotSemantic outputSlotSemantic(ETTerminalMode mode) {
            return switch (mode) {
                case CRAFTING -> ETMenuType.ET_TERMINAL.getSlotSemanticResult();
                case STONECUTTING -> ETSlotSemantics.STONECUTTING_RESULT;
                case SMITHING -> SlotSemantics.SMITHING_TABLE_RESULT;
                case ANVIL -> ETSlotSemantics.ANVIL_RESULT;
            };
        }

        private static RecipeType<?> recipeTypeFor(ETTerminalMode mode) {
            return switch (mode) {
                case CRAFTING -> RecipeTypes.CRAFTING;
                case SMITHING -> RecipeTypes.SMITHING;
                case STONECUTTING -> RecipeTypes.STONECUTTING;
                case ANVIL -> RecipeTypes.ANVIL;
            };
        }
    }
}
