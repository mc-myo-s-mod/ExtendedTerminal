package me.myogoo.extendedterminal.client.screen.extendedterminal.wt;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.BackgroundPanel;
import de.mari_023.ae2wtlib.wut.CycleTerminalButton;
import de.mari_023.ae2wtlib.wut.IUniversalTerminalCapable;
import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ETWTScreen extends ETTerminalScreen<ETWTMenu> implements IUniversalTerminalCapable {
    public ETWTScreen(ETWTMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        if (menu.isWUT()) {
            addToLeftToolbar(new CycleTerminalButton(btn -> cycleTerminal()));
        }

        widgets.add("singularityBackground", new BackgroundPanel(style.getImage("singularityBackground")));
    }

    public ETWTHost getHost() {
        return (ETWTHost) this.menu.getHost();
    }
}
