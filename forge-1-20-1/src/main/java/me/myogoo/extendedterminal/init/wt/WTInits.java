package me.myogoo.extendedterminal.init.wt;

import appeng.api.upgrades.Upgrades;
import de.mari_023.ae2wtlib.AE2wtlib;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.me.host.UnitedWTHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.wt.UnitedWTMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import me.myogoo.myotus.api.wt.AddTerminalEvent;

public final class WTInits {
    private WTInits() {
    }

    public static synchronized void registerTerminal() {
        AddTerminalEvent.register(event -> {
            var terminalName = ETMenuType.ET_TERMINAL.getWTIdAsString();
            event.addTerminal(
                    terminalName,
                    ETWTHost::new,
                    ETWTMenu.TYPE,
                    WTItems.WIRELESS_ET_TERMINAL.asItem(),
                    terminalName,
                    "item.extendedterminal.wireless_et_terminal");
            var unitedTerminalName = ETMenuType.UNITED_TERMINAL.getWTIdAsString();
            event.addTerminal(
                    unitedTerminalName,
                    UnitedWTHost::new,
                    UnitedWTMenu.TYPE,
                    WTItems.WIRELESS_UNITED_TERMINAL.asItem(),
                    unitedTerminalName,
                    "item.extendedterminal.wireless_united_terminal");
            Upgrades.add(AE2wtlib.MAGNET_CARD, WTItems.WIRELESS_ET_TERMINAL.asItem(), 1);
            Upgrades.add(AE2wtlib.MAGNET_CARD, WTItems.WIRELESS_UNITED_TERMINAL.asItem(), 1);
        });
    }
}
