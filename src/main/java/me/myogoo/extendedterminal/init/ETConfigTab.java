package me.myogoo.extendedterminal.init;

import me.myogoo.extendedterminal.client.screen.extendedterminal.gui.config.ETTerminalConfigScreen;
import me.myogoo.extendedterminal.init.wt.WTItems;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.wt.AE2WTLib;
import me.myogoo.myotus.api.config.MyoConfigTab;
import net.minecraft.network.chat.Component;

public class ETConfigTab {
    public static void initialize() {
        if (!MyotusAPI.modIntegrationManager().isLoaded(AE2WTLib.class)) {
            return;
        }

        MyotusAPI.get()
                .configRegistrar()
                .terminalConfigTab(new MyoConfigTab(
                        Component.translatable("gui.extendedterminal.config.title"),
                        WTItems.WIRELESS_ET_TERMINAL.stack(),
                        "et_config.json",
                        new ETTerminalConfigScreen()
                ).visibleWhen(context -> context.host() instanceof ETWTHost));
    }
}
