package me.myogoo.extendedterminal.integration.jei.extendedterminal.handler;

import me.myogoo.extendedterminal.client.screen.extendedcrafting.UnitedTerminalScreen;
import me.myogoo.extendedterminal.integration.jei.ETJeiPlugin;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class UnitedTerminalGuiHandler
        implements IGuiContainerHandler<UnitedTerminalScreen<? extends UnitedTerminalMenu>> {
    @Override
    public @NotNull List<Rect2i> getGuiExtraAreas(
            UnitedTerminalScreen<? extends UnitedTerminalMenu> screen) {
        return screen.getExclusionZones();
    }

    @Override
    public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(
            UnitedTerminalScreen<? extends UnitedTerminalMenu> screen,
            double guiMouseX,
            double guiMouseY) {
        var menu = screen.getMenu();
        var outputSlot = menu.getSlots(menu.getOutputSlotSemantic()).get(0);
        var recipeType = recipeTypeFor(menu.getSelectedRecipeKind());
        return List.of(IGuiClickableArea.createBasic(
                outputSlot.x - 50,
                outputSlot.y,
                40,
                24,
                recipeType));
    }

    private static RecipeType<?> recipeTypeFor(UnitedTerminalMenu.UnitedRecipeKind kind) {
        if (kind == UnitedTerminalMenu.UnitedRecipeKind.VANILLA) {
            return RecipeTypes.CRAFTING;
        }

        var uid = switch (kind) {
            case VANILLA -> RecipeTypes.CRAFTING.getUid();
            case EXTENDED_CRAFTING_BASIC -> new ResourceLocation("extendedcrafting", "basic_crafting");
            case EXTENDED_CRAFTING_ADVANCED -> new ResourceLocation("extendedcrafting", "advanced_crafting");
            case EXTENDED_CRAFTING_ELITE -> new ResourceLocation("extendedcrafting", "elite_crafting");
            case EXTENDED_CRAFTING_ULTIMATE -> new ResourceLocation("extendedcrafting", "ultimate_crafting");
            case AVARITIA_NEO_EXTREME -> new ResourceLocation("avaritia", "extreme_crafting");
            case RE_AVARITIA_SCULK -> new ResourceLocation("avaritia", "sculk_craft");
            case RE_AVARITIA_NETHER -> new ResourceLocation("avaritia", "nether_craft");
            case RE_AVARITIA_END -> new ResourceLocation("avaritia", "end_craft");
            case RE_AVARITIA_EXTREME -> new ResourceLocation("avaritia", "extreme_craft");
        };
        return ETJeiPlugin.recipeTypeFor(uid);
    }
}
