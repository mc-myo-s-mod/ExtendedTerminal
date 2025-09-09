package me.myogoo.extendedterminal.integration.jei.extendedcrafting;

import com.blakebr0.extendedcrafting.compat.jei.category.table.AdvancedTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.BasicTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.EliteTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.UltimateTableCategory;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiGuiHandler;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.AdvancedTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.BasicTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.EliteTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.UltimateTerminalScreen;
import me.myogoo.extendedterminal.integration.jei.handler.JeiTableGuiHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;

@ModAccessor.ExtendedCrafting
@ETJeiGuiHandler
public class ECGuiHandler {
    @SubscribeLoadEvent
    public static void init(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(BasicTerminalScreen.class, new JeiTableGuiHandler<>(BasicTableCategory.RECIPE_TYPE));
        registration.addGuiContainerHandler(AdvancedTerminalScreen.class, new JeiTableGuiHandler<>(AdvancedTableCategory.RECIPE_TYPE));
        registration.addGuiContainerHandler(EliteTerminalScreen.class, new JeiTableGuiHandler<>(EliteTableCategory.RECIPE_TYPE));
        registration.addGuiContainerHandler(UltimateTerminalScreen.class, new JeiTableGuiHandler<>(UltimateTableCategory.RECIPE_TYPE));
    }
}
