package me.myogoo.extendedterminal.integration.jei.avaritia;

import committee.nova.mods.avaritia.init.compat.jei.category.tables.EndCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.ExtremeCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.NetherCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.SculkCraftingTableCategory;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiGuiHandler;
import me.myogoo.extendedterminal.client.screen.avaritia.EndTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritia.ExtremeTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritia.NetherTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritia.SculkTerminalScreen;
import me.myogoo.extendedterminal.integration.jei.JeiTableGuiHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;

@ModAccessor.Avaritia
@ETJeiGuiHandler
public class AVGuiHandler {
    @SubscribeLoadEvent
    public static void init(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(SculkTerminalScreen.class, new JeiTableGuiHandler<>(SculkCraftingTableCategory.RECIPE_TYPE));
        registration.addGuiContainerHandler(NetherTerminalScreen.class, new JeiTableGuiHandler<>(NetherCraftingTableCategory.RECIPE_TYPE));
        registration.addGuiContainerHandler(EndTerminalScreen.class, new JeiTableGuiHandler<>(EndCraftingTableCategory.RECIPE_TYPE));
        registration.addGuiContainerHandler(ExtremeTerminalScreen.class, new JeiTableGuiHandler<>(ExtremeCraftingTableCategory.RECIPE_TYPE));
    }
}
