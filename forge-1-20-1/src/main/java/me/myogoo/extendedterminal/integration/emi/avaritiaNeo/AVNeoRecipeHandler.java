package me.myogoo.extendedterminal.integration.emi.avaritiaNeo;

import dev.emi.emi.api.EmiRegistry;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.itemList.RecipeTransfer;
import me.myogoo.myotus.api.annotation.itemList.emi.EMI;
import me.myogoo.extendedterminal.integration.emi.avaritiaNeo.handler.AVNeoEmiRecipeHandler;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.avaritiaNeo.NeoExtremeTerminalMenu;
import net.byAqua3.avaritia.compat.emi.AvaritiaEMIPlugin;
import me.myogoo.extendedterminal.api.annotation.AvaritiaNeo;

@AvaritiaNeo
@EMI
@RecipeTransfer
public class AVNeoRecipeHandler {
    @MyotusSubscriber
    public static void init(EmiRegistry registry) {
        registry.addRecipeHandler(NeoExtremeTerminalMenu.TYPE, new AVNeoEmiRecipeHandler(AvaritiaEMIPlugin.EXTREME_CRAFTING, NeoExtremeTerminalMenu.class, ETMenuType.NEO_EXTREME_TERMINAL));
    }
}
