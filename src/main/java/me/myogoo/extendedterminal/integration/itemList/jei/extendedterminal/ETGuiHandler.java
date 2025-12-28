package me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal;

import me.myogoo.extendedterminal.api.ETSubscribeEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeCatalyst;
import mezz.jei.api.registration.IGuiHandlerRegistration;

@ETJeiRecipeCatalyst
public class ETGuiHandler {
    @ETSubscribeEvent
    public static void init(IGuiHandlerRegistration registration) {
        //registration.addGuiContainerHandler(ETTerminalScreen<ET>.class, new ETTerminalGuiHandler());
    }
}
