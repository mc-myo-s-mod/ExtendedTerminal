package me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal;

import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeCatalyst;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import mezz.jei.api.registration.IGuiHandlerRegistration;

@ETJeiRecipeCatalyst
public class ETGuiHandler {
    @MyotusSubscriber
    public static void init(IGuiHandlerRegistration registration) {
        //registration.addGuiContainerHandler(ETTerminalScreen<ET>.class, new ETTerminalGuiHandler());
    }
}
