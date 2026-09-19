package me.myogoo.extendedterminal.client.screen.extendedcrafting.wt;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.BackgroundPanel;
import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.terminal.ItemButton;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;
import de.mari_023.ae2wtlib.wut.CycleTerminalButton;
import de.mari_023.ae2wtlib.wut.IUniversalTerminalCapable;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.UnitedTerminalScreen;
import me.myogoo.extendedterminal.me.host.UnitedWTHost;
import me.myogoo.extendedterminal.menu.extendedcrafting.wt.UnitedWTMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class UnitedWTScreen extends UnitedTerminalScreen<UnitedWTMenu> implements IUniversalTerminalCapable {
    private final ItemButton magnetCardMenuButton;

    public UnitedWTScreen(UnitedWTMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        if (menu.isWUT()) {
            addToLeftToolbar(new CycleTerminalButton(btn -> cycleTerminal()));
        }

        var magnetCardTexture = new ResourceLocation(AE2wtlib.MOD_NAME, "textures/item/magnet_card.png");
        this.magnetCardMenuButton = new ItemButton(btn -> getMenu().openMagnetMenu(), magnetCardTexture);
        addToLeftToolbar(this.magnetCardMenuButton);
        this.magnetCardMenuButton.setMessage(TextConstants.MAGNET_FILTER);

        widgets.add("singularityBackground", new BackgroundPanel(style.getImage("singularityBackground")));
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        var mode = getMenu().getMagnetMode();
        this.magnetCardMenuButton.setVisibility(mode != MagnetMode.INVALID && mode != MagnetMode.NO_CARD);
    }

    public UnitedWTHost getHost() {
        return (UnitedWTHost) this.menu.getHost();
    }
}
