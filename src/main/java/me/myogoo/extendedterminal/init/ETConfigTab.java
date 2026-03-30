package me.myogoo.extendedterminal.init;

import me.myogoo.extendedterminal.client.screen.extendedterminal.gui.config.ETTerminalConfigScreen;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.config.MyoConfigTab;
import net.minecraft.network.chat.Component;

public class ETConfigTab {
    public static void initialize() {
        /*
        MyotusAPI.get()
                .configRegistrar()
                .terminalConfigTab(new MyoConfigTab(
                        Component.translatable("gui.extendedterminal.config.title"),
                        ETParts.ET_TERMINAL_PART.stack(),
                        "et_config.json",
                        new ETTerminalConfigScreen()
                ));
         */
    }
}
