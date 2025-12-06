package me.myogoo.extendedterminal.integration.jei.extendedcrafting;

import com.blakebr0.extendedcrafting.compat.jei.category.table.EpicTableCategory;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiGuiHandler;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.EpicTerminalScreen;
import me.myogoo.extendedterminal.integration.jei.handler.JeiTableGuiHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;

@ModAccessor.EpicExCrafting
@ETJeiGuiHandler
public class EXCGuiHandler {
    @SubscribeLoadEvent
    public static void init(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(EpicTerminalScreen.class, new JeiTableGuiHandler<>(EpicTableCategory.RECIPE_TYPE));
    }
}
