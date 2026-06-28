package me.myogoo.extendedterminal.client.screen.extendedterminal.gui.panels;

import appeng.client.Point;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.ActionButton;
import appeng.menu.SlotSemantics;
import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import me.myogoo.extendedterminal.client.screen.extendedterminal.gui.ETTerminalModePanel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class CraftingPanel extends ETTerminalModePanel {
    private static final Blitter BG = Blitter.texture("guis/et_terminal_panel.png",512,512).src(0, 0, 159, 65);

    private final ActionButton clearToStorage;
    private final ActionButton clearToInv;

    public CraftingPanel(ETTerminalScreen screen, WidgetContainer widgets, ActionButton clearToStorage, ActionButton clearToInv) {
        super(screen, widgets, ETTranslationKey.GUI.GUI_CRAFTING_PANEL);
        this.clearToStorage = clearToStorage;
        this.clearToInv = clearToInv;

        setVisible(false);
    }

    @Override
    public ItemStack getIcon() {
        return Blocks.CRAFTING_TABLE.asItem().getDefaultInstance();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        clearToInv.setVisibility(visible);
        clearToStorage.setVisibility(visible);

        screen.setSlotsHidden(SlotSemantics.CRAFTING_GRID, !visible);
        screen.setSlotsHidden(SlotSemantics.CRAFTING_RESULT, !visible);
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        BG.dest(bounds.getX() + 8, bounds.getY() + bounds.getHeight() - 165).blit(guiGraphics);
    }
}
