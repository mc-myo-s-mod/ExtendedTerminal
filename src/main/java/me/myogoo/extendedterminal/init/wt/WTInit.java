package me.myogoo.extendedterminal.init.wt;

import appeng.api.features.HotkeyAction;
import appeng.api.features.GridLinkables;
import appeng.api.upgrades.Upgrades;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.items.tools.powered.powersink.PoweredItemCapabilities;
import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.api.gui.Icon;
import de.mari_023.ae2wtlib.api.registration.AddTerminalEvent;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import me.myogoo.myotus.api.annotation.wt.AE2WTLib;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import static me.myogoo.extendedterminal.init.wt.WTItems.WT_ITEMS;

public class WTInit {
    private final static ResourceLocation ICON_SRC = ExtendedTerminal.makeId("textures/guis/icons.png");
    private final static Icon.Texture TEXTURE = new Icon.Texture(ICON_SRC, 128, 128);
    private static boolean terminalRegistered = false;

    public static synchronized void registerTerminal() {
        if (terminalRegistered || !MyotusAPI.get().modIntegrationManager().isLoaded(AE2WTLib.class)) {
            return;
        }
        terminalRegistered = true;
        register(WTItems.WIRELESS_ET_TERMINAL, ETMenuType.ET_TERMINAL, ETWTHost::new, ETWTMenu.TYPE,
                new Icon(0, 0, 16, 16, TEXTURE));
    }

    private static void register(ItemLike terminal, ETMenuType etMenuType, WTDefinition.WTMenuHostFactory host,
            MenuType<?> menuType, Icon icon) {
        AddTerminalEvent
                .register(e -> e.builder(etMenuType.getWTIdAsString(), host, menuType, (ItemWT) terminal.asItem(), icon)
                        .hotkeyName(HotkeyAction.WIRELESS_TERMINAL)
                        .addTerminal());
    }

    public static void registerUpgradeTooltips(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.ITEM)) {
            return;
        }

        Upgrades.add(AE2wtlibItems.MAGNET_CARD, WTItems.WIRELESS_ET_TERMINAL.asItem(), 1);
        Upgrades.add(AE2wtlibItems.QUANTUM_BRIDGE_CARD, WTItems.WIRELESS_ET_TERMINAL.asItem(), 1);

        GridLinkables.register(WTItems.WIRELESS_ET_TERMINAL.asItem(), WirelessTerminalItem.LINKABLE_HANDLER);
    }

    public static void initCapabilities(RegisterCapabilitiesEvent event) {
        for (var def : WT_ITEMS) {
            ItemWT item = (ItemWT) def.asItem();
            event.registerItem(Capabilities.EnergyStorage.ITEM,
                    (object, index) -> new PoweredItemCapabilities(object, item), item);
        }
    }
}
