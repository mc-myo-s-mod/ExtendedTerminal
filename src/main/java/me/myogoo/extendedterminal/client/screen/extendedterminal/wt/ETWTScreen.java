package me.myogoo.extendedterminal.client.screen.extendedterminal.wt;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.BackgroundPanel;
import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.terminal.ItemButton;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;
import de.mari_023.ae2wtlib.wut.CycleTerminalButton;
import de.mari_023.ae2wtlib.wut.IUniversalTerminalCapable;
import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ETWTScreen extends ETTerminalScreen<ETWTMenu> implements IUniversalTerminalCapable {
    private final ItemButton magnetCardToggleButton;
    private final ItemButton magnetCardMenuButton;

    public ETWTScreen(ETWTMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        if (menu.isWUT()) {
            addToLeftToolbar(new CycleTerminalButton(btn -> cycleTerminal()));
        }

        var magnetCardTexture = new ResourceLocation(AE2wtlib.MOD_NAME, "textures/item/magnet_card.png");
        magnetCardToggleButton = new ItemButton(btn -> setMagnetMode(), magnetCardTexture);
        addToLeftToolbar(magnetCardToggleButton);

        magnetCardMenuButton = new ItemButton(btn -> getMenu().openMagnetMenu(), magnetCardTexture);
        addToLeftToolbar(magnetCardMenuButton);
        magnetCardMenuButton.setMessage(TextConstants.MAGNET_FILTER);

        widgets.add("singularityBackground", new BackgroundPanel(style.getImage("singularityBackground")));
    }

    private void setMagnetMode() {
        if (isHandlingRightClick()) {
            switch (getMenu().getMagnetMode()) {
                case OFF -> getMenu().setMagnetMode(MagnetMode.PICKUP_ME);
                case PICKUP_INVENTORY -> getMenu().setMagnetMode(MagnetMode.OFF);
                case PICKUP_ME -> getMenu().setMagnetMode(MagnetMode.PICKUP_INVENTORY);
                case PICKUP_ME_NO_MAGNET -> getMenu().setMagnetMode(MagnetMode.OFF);
            }
            return;
        }
        switch (getMenu().getMagnetMode()) {
            case OFF -> getMenu().setMagnetMode(MagnetMode.PICKUP_INVENTORY);
            case PICKUP_INVENTORY -> getMenu().setMagnetMode(MagnetMode.PICKUP_ME);
            case PICKUP_ME -> getMenu().setMagnetMode(MagnetMode.OFF);
            case PICKUP_ME_NO_MAGNET -> getMenu().setMagnetMode(MagnetMode.PICKUP_INVENTORY);
        }
    }

    private void setMagnetModeText() {
        switch (getMenu().getMagnetMode()) {
            case INVALID, NO_CARD -> {
                magnetCardToggleButton.setVisibility(false);
                magnetCardMenuButton.setVisibility(false);
            }
            case OFF -> {
                magnetCardToggleButton.setVisibility(true);
                magnetCardMenuButton.setVisibility(true);
                magnetCardToggleButton.setMessage(TextConstants.MAGNETCARD_OFF);
            }
            case PICKUP_INVENTORY -> {
                magnetCardToggleButton.setVisibility(true);
                magnetCardMenuButton.setVisibility(true);
                magnetCardToggleButton.setMessage(TextConstants.MAGNETCARD_INVENTORY);
            }
            case PICKUP_ME, PICKUP_ME_NO_MAGNET -> {
                magnetCardToggleButton.setVisibility(true);
                magnetCardMenuButton.setVisibility(true);
                magnetCardToggleButton.setMessage(TextConstants.MAGNETCARD_ME);
            }
        }
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        setMagnetModeText();
    }

    public ETWTHost getHost() {
        return (ETWTHost) this.menu.getHost();
    }
}
