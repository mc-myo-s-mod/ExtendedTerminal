package me.myogoo.extendedterminal.integration.emi.avaritiaRe;

import committee.nova.mods.avaritia.init.compat.emi.category.tables.EndCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.emi.category.tables.ExtremeCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.emi.category.tables.NetherCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.emi.category.tables.SculkCraftingTableCategory;
import dev.emi.emi.api.EmiRegistry;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.itemList.RecipeTransfer;
import me.myogoo.myotus.api.annotation.itemList.emi.EMI;
import me.myogoo.extendedterminal.integration.emi.avaritiaRe.handler.AVTerminalRecipeHandler;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.avaritiaRe.EndTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.ExtremeTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.NetherTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.SculkTerminalMenu;
import me.myogoo.extendedterminal.api.annotation.ReAvaritia;

@ReAvaritia
@EMI
@RecipeTransfer
public class AVRecipeHandler {
    @MyotusSubscriber
    public static void init(EmiRegistry registry) {
        registry.addRecipeHandler(SculkTerminalMenu.TYPE, new AVTerminalRecipeHandler<>(SculkCraftingTableCategory.CATEGORY, SculkTerminalMenu.class, ETMenuType.SCULK_TERMINAL));
        registry.addRecipeHandler(NetherTerminalMenu.TYPE, new AVTerminalRecipeHandler<>(NetherCraftingTableCategory.CATEGORY, NetherTerminalMenu.class, ETMenuType.NETHER_TERMINAL));
        registry.addRecipeHandler(EndTerminalMenu.TYPE, new AVTerminalRecipeHandler<>(EndCraftingTableCategory.CATEGORY, EndTerminalMenu.class, ETMenuType.END_TERMINAL));
        registry.addRecipeHandler(ExtremeTerminalMenu.TYPE, new AVTerminalRecipeHandler<>(ExtremeCraftingTableCategory.CATEGORY, ExtremeTerminalMenu.class, ETMenuType.EXTREME_TERMINAL));
    }
}
