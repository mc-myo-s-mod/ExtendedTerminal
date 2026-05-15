package me.myogoo.extendedterminal.integration.polymorph.widget;

import appeng.menu.SlotSemantics;
import gripe._90.polyeng.widget.BaseTerminalWidget;
import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import net.minecraft.client.gui.GuiGraphics;

public class ETTerminalPolymorphWidget<M extends ETTerminalBaseMenu<?>> extends BaseTerminalWidget<M, ETTerminalBaseScreen<?, M>> {
    public ETTerminalPolymorphWidget(ETTerminalBaseScreen<?, M> screen) {
        super(screen, screen.getMenu().getSlots(SlotSemantics.CRAFTING_RESULT).getFirst());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        resetWidgetOffsets();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
