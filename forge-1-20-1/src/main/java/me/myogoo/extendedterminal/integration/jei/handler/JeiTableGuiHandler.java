package me.myogoo.extendedterminal.integration.jei.handler;

import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class JeiTableGuiHandler<R extends Recipe<?>, T extends ETTerminalBaseMenu<R>, S extends ETTerminalBaseScreen<R,T>> implements IGuiContainerHandler<S> {
    private final RecipeType<R> recipeType;

    public JeiTableGuiHandler(RecipeType<R> recipeType) {
        this.recipeType = recipeType;
    }

    @Override
    public @NotNull List<Rect2i> getGuiExtraAreas(S screen) {
        return screen.getExclusionZones();
    }

    @Override
    public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(S screen, double guiMouseX, double guiMouseY) {
        var menu = screen.getMenu();
        var outputSlot = menu.getSlots(menu.getOutputSlotSemantic()).get(0);
        boolean aboveOutput = menu.getETMenuType() == ETMenuType.EPIC_TERMINAL
                || menu.getETMenuType() == ETMenuType.LEGENDARY_TERMINAL;
        int x = aboveOutput ? outputSlot.x - 4 : outputSlot.x - 50;
        int y = aboveOutput ? outputSlot.y - 30 : outputSlot.y;
        return List.of(IGuiClickableArea.createBasic(x, y, aboveOutput ? 24 : 40, 24, recipeType));
    }
}
