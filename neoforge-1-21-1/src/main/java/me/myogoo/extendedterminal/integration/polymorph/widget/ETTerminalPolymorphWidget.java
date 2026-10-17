package me.myogoo.extendedterminal.integration.polymorph.widget;

import gripe._90.polyeng.widget.BaseTerminalWidget;
import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import net.minecraft.client.gui.GuiGraphics;

public class ETTerminalPolymorphWidget<M extends ETTerminalBaseMenu<?>> extends BaseTerminalWidget<M, ETTerminalBaseScreen<?, M>> {
    private final ETTerminalBaseScreen<?, M> screen;

    public ETTerminalPolymorphWidget(ETTerminalBaseScreen<?, M> screen) {
        super(screen, screen.getMenu().getSlots(screen.getMenu().getOutputSlotSemantic()).getFirst());
        this.screen = screen;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (menu instanceof UnitedTerminalMenu united && united.getSelectedRecipeType() != MyoRecipeType.VANILLA) {
            selectionWidget.setActive(false);
            return;
        }
        resetWidgetOffsets();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (menu instanceof UnitedTerminalMenu united && united.getSelectedRecipeType() != MyoRecipeType.VANILLA) {
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public int getXPos() {
        return menu instanceof UnitedTerminalMenu ? screen.getXSize() + 4 : super.getXPos();
    }

    @Override
    public int getYPos() {
        return menu instanceof UnitedTerminalMenu ? getOutputSlot().y : super.getYPos();
    }
}
