package me.myogoo.extendedterminal.client.gui;

import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import me.myogoo.extendedterminal.ExtendedTerminal;
import net.minecraft.resources.ResourceLocation;

public enum ETIcon {
    CRAFTING(Icon.TAB_CRAFTING),
    SMITHING(Icon.TAB_SMITHING),
    STONECUTTING(Icon.TAB_STONECUTTING);

    private final int x;
    private  final int y;
    private final int width;
    private final int height;
    private final ResourceLocation texture;
    ETIcon(Icon icon) {
        this(icon.x, icon.y, Icon.TEXTURE);
    }

    ETIcon(int x, int y) {
        this(x, y, ExtendedTerminal.makeId("textures/guis/icons.png"));
    }

    ETIcon(int x, int y, ResourceLocation texture) {
        this(x, y, 16, 16, texture);
    }

    ETIcon(int x, int y, int width, int height, ResourceLocation texture) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.texture = texture;
    }

    public Blitter getBlitter() {
        return Blitter.texture(texture, 256, 256)
                .src(x, y, width, height);
    }

}
