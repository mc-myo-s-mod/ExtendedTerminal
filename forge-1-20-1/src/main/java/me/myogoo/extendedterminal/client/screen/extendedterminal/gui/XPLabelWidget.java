package me.myogoo.extendedterminal.client.screen.extendedterminal.gui;

import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import appeng.client.Point;
import appeng.client.gui.ICompositeWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

public class XPLabelWidget implements ICompositeWidget {
    private static final int RED_COLOR = 0xFF6060;
    private static final int GREEN_COLOR = 0x80FF20;

    private Component text = Component.translatable(ETTranslationKey.GUI.GUI_ANVIL_PANEL_XP_COST.key(), 0);
    private boolean visible;
    private int x;
    private int y;
    private int color = RED_COLOR;
    private int cost;

    @Override
    public void setPosition(Point position) {
        this.x = position.getX();
        this.y = position.getY();
    }

    @Override
    public void setSize(int width, int height) {
    }

    @Override
    public Rect2i getBounds() {
        return new Rect2i(this.x, this.y, 0, 0);
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setCost(int cost) {
        if (this.cost == cost) {
            return;
        }
        this.cost = cost;
        this.text = Component.translatable(ETTranslationKey.GUI.GUI_ANVIL_PANEL_XP_COST.key(), this.cost);
        this.color = this.cost > 0 ? GREEN_COLOR : RED_COLOR;
    }

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        if (!this.visible || this.cost <= 0) {
            return;
        }
        guiGraphics.drawString(Minecraft.getInstance().font, this.text, this.x, this.y, this.color);
    }
}
