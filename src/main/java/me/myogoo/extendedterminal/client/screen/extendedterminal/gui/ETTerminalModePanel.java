package me.myogoo.extendedterminal.client.screen.extendedterminal.gui;

import appeng.client.Point;
import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.WidgetContainer;
import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public abstract class ETTerminalModePanel implements ICompositeWidget {
    protected final ETTerminalScreen<?> screen;
    protected final ETTerminalMenu menu;
    protected final WidgetContainer widgets;
    protected boolean visible;
    protected int x;
    protected int y;

    protected ETTerminalModePanel(ETTerminalScreen<?> screen, WidgetContainer widgets) {
        this.screen = screen;
        this.menu = screen.getMenu();
        this.widgets = widgets;
    }

    public abstract ItemStack getIcon();

    public Component getTabTooltip() {
        return Component.translatable("gui.extendedterminal." + getClass().getSimpleName().toLowerCase());
    }

    public String getWidgetId() {
        return "modePanel" + getClass().getSimpleName();
    }

    public String getModeTabButtonId() {
        return "modeTabButton" + getClass().getSimpleName();
    }

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
        return new Rect2i(this.x, this.y, 124, 66);
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
