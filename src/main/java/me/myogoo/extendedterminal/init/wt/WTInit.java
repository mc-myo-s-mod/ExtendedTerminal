package me.myogoo.extendedterminal.init.wt;

import appeng.api.features.GridLinkables;
import appeng.items.tools.powered.WirelessTerminalItem;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.wut.WUTHandler;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import me.myogoo.myotus.api.annotation.wt.AE2WTLib;
import me.myogoo.myotus.util.mod.ModIntegrationManager;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = ExtendedTerminal.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class WTInit {
    private WTInit() {
    }

    @SubscribeEvent
    public static void init(RegisterEvent event) {
        WTItems.register();
        WTMenus.register();

        if (!event.getRegistryKey().equals(Registries.ITEM)) {
            return;
        }

        if (!ModIntegrationManager.isLoaded(AE2WTLib.class)) {
            ExtendedTerminal.LOGGER.warn("[ET] AE2WTLib not loaded, skipping wireless terminal registration");
            return;
        }

        if (WTItems.WIRELESS_ET_TERMINAL == null) {
            ExtendedTerminal.LOGGER.warn("[ET] WIRELESS_ET_TERMINAL is null, skipping wireless terminal registration");
            return;
        }

        var terminal = (ItemWT) WTItems.WIRELESS_ET_TERMINAL.asItem();
        WUTHandler.addTerminal(
                ETMenuType.ET_TERMINAL.getWTIdAsString(),
                terminal::tryOpen,
                ETWTHost::new,
                ETWTMenu.TYPE,
                terminal,
                ETMenuType.ET_TERMINAL.getWTIdAsString(),
                "item.extendedterminal.wireless_et_terminal");
        GridLinkables.register(terminal, WirelessTerminalItem.LINKABLE_HANDLER);
    }

    @SubscribeEvent
    public static void initCapabilities(RegisterCapabilitiesEvent event) {
        // WT-specific capability wiring is handled by AE2WTLib when present.
    }
}
