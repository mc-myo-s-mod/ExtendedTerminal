package me.myogoo.extendedterminal.init.wt;

import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import appeng.api.upgrades.Upgrades;
import me.myogoo.extendedterminal.compat.ae2helpers.AE2HelpersUpgradeRegistration;
import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.me.host.ExtendedCraftingWTHost;
import me.myogoo.extendedterminal.me.host.UnitedWTHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.wt.ExtendedCraftingWTMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.wt.UnitedWTMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import me.myogoo.myotus.api.wt.AddTerminalEvent;

public final class WTInits {
    private static final String AE_WIRELESS_TERMINAL_HOTKEY = "wireless_terminal";

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
                    AE_WIRELESS_TERMINAL_HOTKEY,
                    ETTranslationKey.ITEM.ITEM_WIRELESS_ET_TERMINAL.key());
            var unitedTerminalName = ETMenuType.UNITED_TERMINAL.getWTIdAsString();
            event.addTerminal(
                    unitedTerminalName,
                    UnitedWTHost::new,
                    UnitedWTMenu.TYPE,
                    WTItems.WIRELESS_UNITED_TERMINAL.asItem(),
                    AE_WIRELESS_TERMINAL_HOTKEY,
                    ETTranslationKey.ITEM.ITEM_WIRELESS_UNITED_TERMINAL.key());
            if (ETMenuType.EPIC_TERMINAL.canLoad()) {
                event.addTerminal(ETMenuType.EPIC_TERMINAL.getWTIdAsString(),
                        (player, slot, stack, returnToMenu) -> new ExtendedCraftingWTHost(
                                player, slot, stack, returnToMenu, ETMenuType.EPIC_TERMINAL),
                        ExtendedCraftingWTMenu.EPIC_TYPE, (ItemWT) WTItems.WIRELESS_EPIC_TERMINAL.asItem(),
                        AE_WIRELESS_TERMINAL_HOTKEY, ETTranslationKey.ITEM.ITEM_WIRELESS_EPIC_TERMINAL.key());
                AE2HelpersUpgradeRegistration.registerSupportedCards(WTItems.WIRELESS_EPIC_TERMINAL.asItem());
            }
            if (ETMenuType.LEGENDARY_TERMINAL.canLoad()) {
                event.addTerminal(ETMenuType.LEGENDARY_TERMINAL.getWTIdAsString(),
                        (player, slot, stack, returnToMenu) -> new ExtendedCraftingWTHost(
                                player, slot, stack, returnToMenu, ETMenuType.LEGENDARY_TERMINAL),
                        ExtendedCraftingWTMenu.LEGENDARY_TYPE, (ItemWT) WTItems.WIRELESS_LEGENDARY_TERMINAL.asItem(),
                        AE_WIRELESS_TERMINAL_HOTKEY, ETTranslationKey.ITEM.ITEM_WIRELESS_LEGENDARY_TERMINAL.key());
                AE2HelpersUpgradeRegistration.registerSupportedCards(WTItems.WIRELESS_LEGENDARY_TERMINAL.asItem());
            }
            Upgrades.add(AE2wtlib.MAGNET_CARD, WTItems.WIRELESS_ET_TERMINAL.asItem(), 1);
            Upgrades.add(AE2wtlib.MAGNET_CARD, WTItems.WIRELESS_UNITED_TERMINAL.asItem(), 1);
            AE2HelpersUpgradeRegistration.registerSupportedCards(WTItems.WIRELESS_ET_TERMINAL.asItem());
            AE2HelpersUpgradeRegistration.registerSupportedCards(WTItems.WIRELESS_UNITED_TERMINAL.asItem());
        });
    }
}
