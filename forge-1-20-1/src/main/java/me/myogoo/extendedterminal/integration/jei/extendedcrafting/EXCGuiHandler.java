package me.myogoo.extendedterminal.integration.jei.extendedcrafting;

import com.blakebr0.extendedcrafting.compat.jei.category.table.EpicTableCategory;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.itemList.jei.JEI;
import me.myogoo.myotus.api.annotation.itemList.jei.JEIGuiHandler;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.EpicTerminalScreen;
import me.myogoo.extendedterminal.integration.jei.handler.JeiTableGuiHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import me.myogoo.extendedterminal.api.annotation.EpicExCrafting;

@EpicExCrafting
@JEI
@JEIGuiHandler
public class EXCGuiHandler {
    @MyotusSubscriber
    public static void init(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(EpicTerminalScreen.class, new JeiTableGuiHandler<>(EpicTableCategory.RECIPE_TYPE));
    }
}
