package me.myogoo.extendedterminal.integration.jei.avaritiaNeo;

import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiGuiHandler;
import me.myogoo.extendedterminal.client.screen.avaritiaNeo.NeoExtremeTerminalScreen;
import me.myogoo.extendedterminal.integration.jei.handler.JeiTableGuiHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.byAqua3.avaritia.compat.jei.AvaritiaJEIPlugin;

@ETJeiGuiHandler
@ModAccessor.AvaritiaNeo
public class AVNeoGuiHandler {
    @MyotusSubscriber
    public static void init(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(NeoExtremeTerminalScreen.class, new JeiTableGuiHandler<>(AvaritiaJEIPlugin.EXTREME_CRAFTING));
    }
}
