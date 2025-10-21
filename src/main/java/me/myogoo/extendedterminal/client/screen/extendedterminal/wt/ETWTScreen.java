package me.myogoo.extendedterminal.client.screen.extendedterminal.wt;

import appeng.client.gui.style.ScreenStyle;
import de.mari_023.ae2wtlib.api.gui.ScrollingUpgradesPanel;
import de.mari_023.ae2wtlib.api.terminal.IUniversalTerminalCapable;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ETWTScreen extends ETTerminalScreen<ETWTMenu> implements IUniversalTerminalCapable {
    private final ScrollingUpgradesPanel upgradesPanel;

    public ETWTScreen(ETWTMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        if(menu.isWUT()) {
            addToLeftToolbar(cycleTerminalButton());
        }

        this.upgradesPanel = addUpgradePanel(widgets, getMenu());
    }

    @Override
    public void init() {
        super.init();
        upgradesPanel.setMaxRows(Math.max(2, getVisibleRows()));
    }

    @Override
    public @NotNull WTMenuHost getHost() {
        return (WTMenuHost) this.menu.getHost();
    }
}
