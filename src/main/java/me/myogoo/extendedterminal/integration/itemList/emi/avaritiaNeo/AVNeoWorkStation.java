package me.myogoo.extendedterminal.integration.itemList.emi.avaritiaNeo;

import dev.emi.emi.api.EmiRegistry;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiWorkStation;
import me.myogoo.extendedterminal.integration.itemList.emi.avaritiaNeo.handler.AVNeoTerminalRecipeHandler;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.avaritiaNeo.NeoExtremeTerminalMenu;

import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import net.byAqua3.avaritia.compat.emi.AvaritiaEMIPlugin;

@ModAccessor.AvaritiaNeo
@ETEmiWorkStation
public class AVNeoWorkStation {
    @MyotusSubscriber
    public static void init(EmiRegistry registry) {
        registry.addRecipeHandler(NeoExtremeTerminalMenu.TYPE, new AVNeoTerminalRecipeHandler(AvaritiaEMIPlugin.EXTREME_CRAFTING, NeoExtremeTerminalMenu.class, ETMenuType.NEO_EXTREME_TERMINAL));
    }
}
