package me.myogoo.extendedterminal.init.wt;

import de.mari_023.ae2wtlib.wut.WUTHandler;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

public class WTInits {
    public static void init(RegisterEvent event) {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)) {
            WUTHandler
                    .addTerminal("wireless_et_terminal",
                            WTItems.WIRELESS_ET_TERMINAL.asItem()::tryOpen,
                            ETWTHost::new,
                            ETWTMenu.TYPE,
                            WTItems.WIRELESS_ET_TERMINAL.asItem());
        }
    }
}
