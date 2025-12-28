package me.myogoo.extendedterminal.integration.itemList.jei.extendedcrafting;

import com.blakebr0.extendedcrafting.compat.jei.category.table.AdvancedTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.BasicTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.EliteTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.UltimateTableCategory;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.ETSubscribeEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiGuiHandler;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.AdvancedTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.BasicTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.EliteTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.UltimateTerminalScreen;
import me.myogoo.extendedterminal.integration.itemList.jei.handler.JeiTableHolderGuiHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;

@ModAccessor.ExtendedCrafting
@ETJeiGuiHandler
public class ECGuiHandler {
    @ETSubscribeEvent
    public static void init(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(BasicTerminalScreen.class, new JeiTableHolderGuiHandler<>(BasicTableCategory.RECIPE_TYPE));
        registration.addGuiContainerHandler(AdvancedTerminalScreen.class, new JeiTableHolderGuiHandler<>(AdvancedTableCategory.RECIPE_TYPE));
        registration.addGuiContainerHandler(EliteTerminalScreen.class, new JeiTableHolderGuiHandler<>(EliteTableCategory.RECIPE_TYPE));
        registration.addGuiContainerHandler(UltimateTerminalScreen.class, new JeiTableHolderGuiHandler<>(UltimateTableCategory.RECIPE_TYPE));
    }
}
