package me.myogoo.extendedterminal.client.screen.extendedterminal.gui.panels;

import appeng.api.config.ActionItems;
import appeng.client.Point;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.ActionButton;
import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedterminal.gui.ETTerminalModePanel;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import static me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart.SMITHING_INVENTORY;

public class SmithingTablePanel extends ETTerminalModePanel {
    private static final Blitter BG = Blitter.texture("guis/et_terminal_panel.png", 512, 512).src(0, 67, 159, 65);

    private final ActionButton clearBtn;
    private final ActionButton clearToPlayerInvBtn;

    public SmithingTablePanel(ETTerminalScreen screen, WidgetContainer widgets) {
        super(screen, widgets);

        this.clearBtn = new ActionButton(ActionItems.STASH, btn -> menu.clearSmithingGrid());
        this.clearBtn.setHalfSize(true);
        widgets.add("clearSmithingGrid", this.clearBtn);

        this.clearToPlayerInvBtn = new ActionButton(ActionItems.STASH_TO_PLAYER_INV,
                btn -> menu.clearToPlayerInventory(SMITHING_INVENTORY));
        this.clearToPlayerInvBtn.setHalfSize(true);
        widgets.add("clearSmithingGridToPlayerInv", this.clearToPlayerInvBtn);

        setVisible(false);
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Blocks.SMITHING_TABLE);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        screen.setSlotsHidden(ETSlotSemantics.SMITHING_TABLE_TEMPLATE, !visible);
        screen.setSlotsHidden(ETSlotSemantics.SMITHING_TABLE_BASE, !visible);
        screen.setSlotsHidden(ETSlotSemantics.SMITHING_TABLE_ADDITION, !visible);
        screen.setSlotsHidden(ETSlotSemantics.SMITHING_TABLE_RESULT, !visible);
        this.clearBtn.setVisibility(visible);
        this.clearToPlayerInvBtn.setVisibility(visible);
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        BG.dest(bounds.getX() + 8, bounds.getY() + bounds.getHeight() - 165).blit(guiGraphics);
    }
}
