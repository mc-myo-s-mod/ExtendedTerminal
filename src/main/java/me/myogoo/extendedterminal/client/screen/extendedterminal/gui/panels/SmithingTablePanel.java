package me.myogoo.extendedterminal.client.screen.extendedterminal.gui.panels;

import appeng.client.Point;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.style.Blitter;
import appeng.menu.SlotSemantics;
import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedterminal.gui.ETTerminalModePanel;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class SmithingTablePanel extends ETTerminalModePanel {
    private static final Blitter BG = Blitter.texture("guis/et_terminal_panel.png").src(0, 67, 159, 65);

    public SmithingTablePanel(ETTerminalScreen screen, WidgetContainer widgets) {
        super(screen, widgets);
    }

    @Override
    public ItemStack getIcon() {
        return Blocks.SMITHING_TABLE.asItem().getDefaultInstance();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        screen.setSlotsHidden(ETSlotSemantics.SMITHING_TABLE_TEMPLATE, !visible);
        screen.setSlotsHidden(ETSlotSemantics.SMITHING_TABLE_BASE, !visible);
        screen.setSlotsHidden(ETSlotSemantics.SMITHING_TABLE_ADDITION, !visible);
        screen.setSlotsHidden(SlotSemantics.SMITHING_TABLE_RESULT, !visible);
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        BG.dest(bounds.getX() + 8, bounds.getY() + bounds.getHeight() - 165).blit(guiGraphics);
    }
}
