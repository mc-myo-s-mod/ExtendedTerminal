package me.myogoo.extendedterminal.client.screen.extendedterminal.gui;

import appeng.client.Point;
import appeng.client.gui.ICompositeWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

public class LabelWidget implements ICompositeWidget {
    private Component text;
    private boolean visible;
    private int x;
    private int y;
    private int color;
    private int cost;

    private static final int RED_COLOR = 0xFF6060;
    private static final int GREEN_COLOR = 0x80FF20;

    public LabelWidget() {
        this.cost = 0;
        this.text = Component.translatable("gui.extendedterminal.anvilpanel.xpcost", cost);
        this.color = RED_COLOR;
    }

    @Override
    public void setPosition(Point position) {
        this.x = position.getX();
        this.y = position.getY();
    }

    @Override
    public void setSize(int width, int height) {}

    @Override
    public Rect2i getBounds() {
        return new Rect2i(x,y,0,0);
    }

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        if (!visible) return;
        if(cost > 0) {
            color = GREEN_COLOR;
            guiGraphics.drawString(Minecraft.getInstance().font,text , x,y, color);
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    public void setCost(int cost) {
        if (this.cost == cost) return;
        this.cost = cost;

        // 상태 변경 직후 텍스트/색 갱신 → 다음 프레임에 바로 반영됨
        this.text = Component.translatable("gui.extendedterminal.anvilpanel.xpcost", this.cost);
        this.color = (this.cost > 0) ? GREEN_COLOR : RED_COLOR;
    }
}
