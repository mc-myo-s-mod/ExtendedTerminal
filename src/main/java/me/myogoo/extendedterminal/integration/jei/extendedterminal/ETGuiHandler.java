package me.myogoo.extendedterminal.integration.jei.extendedterminal;

import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeCatalyst;
import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.integration.jei.extendedterminal.handler.ETTerminalGuiHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;

@ETJeiRecipeCatalyst
public class ETGuiHandler {
    @SubscribeLoadEvent
    public static void init(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(ETTerminalScreen.class, new ETTerminalGuiHandler());
    }
}
