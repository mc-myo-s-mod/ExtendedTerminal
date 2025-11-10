package me.myogoo.extendedterminal.integration.jei.avaritiaNeo;

import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiGuiHandler;
import me.myogoo.extendedterminal.client.screen.avaritiaNeo.NeoExtremeTerminalScreen;
import me.myogoo.extendedterminal.integration.jei.handler.JeiTableGuiHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.byAqua3.avaritia.compat.jei.AvaritiaJEIPlugin;
import net.byAqua3.avaritia.compat.jei.category.CategoryExtremeRecipe;

@ETJeiGuiHandler
@ModAccessor.AvaritiaNeo
public class AVNeoGuiHandler {
    @SubscribeLoadEvent
    public static void init(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(NeoExtremeTerminalScreen.class, new JeiTableGuiHandler<>(AvaritiaJEIPlugin.EXTREME_CRAFTING));
    }
}
