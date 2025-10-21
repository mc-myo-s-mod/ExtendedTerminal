package me.myogoo.extendedterminal.integration.jei.avaritiaNeo;

import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeTransfer;
import me.myogoo.extendedterminal.integration.jei.avaritiaNeo.handler.AVNeoJeiRecipeTransferHandler;
import me.myogoo.extendedterminal.menu.avaritiaNeo.NeoExtremeTerminalMenu;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.byAqua3.avaritia.compat.jei.AvaritiaJEIPlugin;

@ETJeiRecipeTransfer
@ModAccessor.AvaritiaNeo
public class AVNeoRecipeTransferHandler {
    @SubscribeLoadEvent
    public static void init(IRecipeTransferRegistration registration) {
        var helper = registration.getTransferHelper();
        
        registration.addRecipeTransferHandler(new AVNeoJeiRecipeTransferHandler<>(NeoExtremeTerminalMenu.class, NeoExtremeTerminalMenu.TYPE, AvaritiaJEIPlugin.EXTREME_CRAFTING, helper), AvaritiaJEIPlugin.EXTREME_CRAFTING);
    }
}
