package me.myogoo.extendedterminal.integration.jei.avaritiaNeo;

import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.itemList.jei.JEI;
import me.myogoo.myotus.api.annotation.itemList.jei.JEIGuiHandler;
import me.myogoo.extendedterminal.client.screen.avaritiaNeo.NeoExtremeTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.UnitedTerminalScreen;
import me.myogoo.extendedterminal.integration.jei.handler.JeiTableGuiHandler;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.byAqua3.avaritia.compat.jei.AvaritiaJEIPlugin;
import me.myogoo.extendedterminal.api.annotation.AvaritiaNeo;

@JEI
@JEIGuiHandler
@AvaritiaNeo
public class AVNeoGuiHandler {
    @MyotusSubscriber
    public static void init(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(NeoExtremeTerminalScreen.class, new JeiTableGuiHandler<>(AvaritiaJEIPlugin.EXTREME_CRAFTING));
        registration.addGuiContainerHandler(UnitedTerminalScreen.class, (IGuiContainerHandler) new JeiTableGuiHandler<>(AvaritiaJEIPlugin.EXTREME_CRAFTING));
    }
}
