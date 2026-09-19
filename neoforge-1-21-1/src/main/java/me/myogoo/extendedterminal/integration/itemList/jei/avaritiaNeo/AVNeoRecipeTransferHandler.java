package me.myogoo.extendedterminal.integration.itemList.jei.avaritiaNeo;

import me.myogoo.myotus.api.annotation.itemList.RecipeTransfer;
import me.myogoo.myotus.api.annotation.itemList.jei.JEI;
import me.myogoo.extendedterminal.integration.itemList.jei.avaritiaNeo.handler.AVNeoJeiRecipeTransferHandler;
import me.myogoo.extendedterminal.menu.avaritiaNeo.NeoExtremeTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.wt.UnitedWTMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.byAqua3.avaritia.compat.jei.AvaritiaJEIPlugin;
import me.myogoo.extendedterminal.api.annotation.AvaritiaNeo;

@JEI
@RecipeTransfer
@AvaritiaNeo
public class AVNeoRecipeTransferHandler {
    @MyotusSubscriber
    public static void init(IRecipeTransferRegistration registration) {
        var helper = registration.getTransferHelper();
        
        registration.addRecipeTransferHandler(new AVNeoJeiRecipeTransferHandler<>(NeoExtremeTerminalMenu.class, NeoExtremeTerminalMenu.TYPE, AvaritiaJEIPlugin.EXTREME_CRAFTING, helper), AvaritiaJEIPlugin.EXTREME_CRAFTING);
        registration.addRecipeTransferHandler(new AVNeoJeiRecipeTransferHandler<>(UnitedTerminalMenu.class, UnitedTerminalMenu.TYPE, AvaritiaJEIPlugin.EXTREME_CRAFTING, helper), AvaritiaJEIPlugin.EXTREME_CRAFTING);
        if (MyotusAPI.integrations().isLoaded(AE2WTLib.class)) {
            registration.addRecipeTransferHandler(new AVNeoJeiRecipeTransferHandler<>(UnitedWTMenu.class,
                    UnitedWTMenu.TYPE, AvaritiaJEIPlugin.EXTREME_CRAFTING, helper),
                    AvaritiaJEIPlugin.EXTREME_CRAFTING);
        }
    }
}
