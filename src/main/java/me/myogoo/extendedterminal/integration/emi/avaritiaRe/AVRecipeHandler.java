package me.myogoo.extendedterminal.integration.emi.avaritiaRe;

import committee.nova.mods.avaritia.init.compat.emi.category.tables.*;
import dev.emi.emi.api.EmiRegistry;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiRecipeHandler;
import me.myogoo.extendedterminal.integration.emi.avaritiaRe.handler.AVTerminalRecipeHandler;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.avaritiaRe.EndTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.ExtremeTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.NetherTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.SculkTerminalMenu;

@ModAccessor.ReAvaritia
@ETEmiRecipeHandler
public class AVRecipeHandler {
    @SubscribeLoadEvent
    public static void register(EmiRegistry registry) {
        registry.addRecipeHandler(SculkTerminalMenu.TYPE, new AVTerminalRecipeHandler<>(SculkCraftingTableCategory.CATEGORY, SculkTerminalMenu.class, ETMenuType.SCULK_TERMINAL));
        registry.addRecipeHandler(NetherTerminalMenu.TYPE, new AVTerminalRecipeHandler<>(NetherCraftingTableCategory.CATEGORY, NetherTerminalMenu.class, ETMenuType.NETHER_TERMINAL));
        registry.addRecipeHandler(EndTerminalMenu.TYPE, new AVTerminalRecipeHandler<>(EndCraftingTableCategory.CATEGORY, EndTerminalMenu.class, ETMenuType.END_TERMINAL));
        registry.addRecipeHandler(ExtremeTerminalMenu.TYPE, new AVTerminalRecipeHandler<>(ExtremeCraftingTableCategory.CATEGORY, ExtremeTerminalMenu.class, ETMenuType.EXTREME_TERMINAL));
    }
}
