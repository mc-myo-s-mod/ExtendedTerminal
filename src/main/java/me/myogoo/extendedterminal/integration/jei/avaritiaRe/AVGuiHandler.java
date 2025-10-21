package me.myogoo.extendedterminal.integration.jei.avaritiaRe;

import committee.nova.mods.avaritia.init.compat.jei.category.tables.EndCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.ExtremeCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.NetherCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.SculkCraftingTableCategory;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiGuiHandler;
import me.myogoo.extendedterminal.client.screen.avaritiaRe.EndTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritiaRe.ExtremeTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritiaRe.NetherTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritiaRe.SculkTerminalScreen;
import me.myogoo.extendedterminal.integration.jei.handler.JeiTableGuiHandler;
import me.myogoo.extendedterminal.integration.jei.handler.JeiTableHolderGuiHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;

@ModAccessor.ReAvaritia
@ETJeiGuiHandler
public class AVGuiHandler {
    @SubscribeLoadEvent
    public static void init(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(SculkTerminalScreen.class, new JeiTableHolderGuiHandler<>(SculkCraftingTableCategory.RECIPE_TYPE));
        registration.addGuiContainerHandler(NetherTerminalScreen.class, new JeiTableHolderGuiHandler<>(NetherCraftingTableCategory.RECIPE_TYPE));
        registration.addGuiContainerHandler(EndTerminalScreen.class, new JeiTableHolderGuiHandler<>(EndCraftingTableCategory.RECIPE_TYPE));
        registration.addGuiContainerHandler(ExtremeTerminalScreen.class, new JeiTableHolderGuiHandler<>(ExtremeCraftingTableCategory.RECIPE_TYPE));
    }
}
