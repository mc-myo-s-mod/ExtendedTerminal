package me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal;

import me.myogoo.myotus.api.annotation.itemList.RecipeCategory;
import me.myogoo.myotus.api.annotation.itemList.jei.JEI;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import mezz.jei.api.registration.IGuiHandlerRegistration;

@JEI
@RecipeCategory
public class ETGuiHandler {
    @MyotusSubscriber
    public static void init(IGuiHandlerRegistration registration) {
        //registration.addGuiContainerHandler(ETTerminalScreen<ET>.class, new ETTerminalGuiHandler());
    }
}
