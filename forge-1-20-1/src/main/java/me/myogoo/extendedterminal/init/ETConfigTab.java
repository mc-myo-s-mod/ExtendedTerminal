package me.myogoo.extendedterminal.init;

import me.myogoo.extendedterminal.client.screen.extendedterminal.gui.config.ETTerminalConfigScreen;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.init.wt.WTItems;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import me.myogoo.myotus.api.config.MyoConfigTab;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class ETConfigTab {
    private ETConfigTab() {
    }

    public static void initialize() {
        if (!MyotusAPI.integrations().isLoaded(AE2WTLib.class)) {
            return;
        }

        MyotusAPI.configTabs()
                .terminalConfigTab(new MyoConfigTab(
                        new ResourceLocation(ExtendedTerminal.MODID, "wireless_et_terminal"),
                        Component.translatable("gui.extendedterminal.config.title"),
                        WTItems.WIRELESS_ET_TERMINAL.stack(),
                        "et_config.json",
                        new ETTerminalConfigScreen()
                ).visibleWhen(context -> context.host() instanceof ETWTHost));
    }
}
