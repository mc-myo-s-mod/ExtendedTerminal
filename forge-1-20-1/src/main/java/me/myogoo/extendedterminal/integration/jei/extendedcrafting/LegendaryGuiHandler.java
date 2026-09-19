package me.myogoo.extendedterminal.integration.jei.extendedcrafting;

import com.blakebr0.extendedcrafting.compat.jei.category.table.LegendaryTableCategory;
import me.myogoo.extendedterminal.api.annotation.LegendaryExCrafting;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.LegendaryTerminalScreen;
import me.myogoo.extendedterminal.integration.jei.handler.JeiTableGuiHandler;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.itemList.jei.JEI;
import me.myogoo.myotus.api.annotation.itemList.jei.JEIGuiHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;

@LegendaryExCrafting
@JEI
@JEIGuiHandler
public class LegendaryGuiHandler {
    @MyotusSubscriber
    public static void init(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(LegendaryTerminalScreen.class,
                new JeiTableGuiHandler<>(LegendaryTableCategory.RECIPE_TYPE));
    }
}
