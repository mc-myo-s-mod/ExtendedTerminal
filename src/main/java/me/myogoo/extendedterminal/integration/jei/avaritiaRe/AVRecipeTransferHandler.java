package me.myogoo.extendedterminal.integration.jei.avaritiaRe;

import committee.nova.mods.avaritia.init.compat.jei.category.tables.EndCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.ExtremeCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.NetherCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.SculkCraftingTableCategory;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeTransfer;
import me.myogoo.extendedterminal.integration.jei.avaritiaRe.handler.AVJeiRecipeTransferHandler;
import me.myogoo.extendedterminal.menu.avaritiaRe.EndTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.ExtremeTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.NetherTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.SculkTerminalMenu;
import mezz.jei.api.registration.IRecipeTransferRegistration;

@ETJeiRecipeTransfer
@ModAccessor.ReAvaritia
public class AVRecipeTransferHandler {
    @MyotusSubscriber
    public static void init(IRecipeTransferRegistration registration) {
        var helper = registration.getTransferHelper();

        registration.addRecipeTransferHandler(new AVJeiRecipeTransferHandler<>(SculkTerminalMenu.class, SculkTerminalMenu.TYPE, SculkCraftingTableCategory.RECIPE_TYPE, helper), SculkCraftingTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(new AVJeiRecipeTransferHandler<>(NetherTerminalMenu.class, NetherTerminalMenu.TYPE, NetherCraftingTableCategory.RECIPE_TYPE, helper), NetherCraftingTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(new AVJeiRecipeTransferHandler<>(EndTerminalMenu.class, EndTerminalMenu.TYPE, EndCraftingTableCategory.RECIPE_TYPE, helper), EndCraftingTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(new AVJeiRecipeTransferHandler<>(ExtremeTerminalMenu.class, ExtremeTerminalMenu.TYPE, ExtremeCraftingTableCategory.RECIPE_TYPE, helper), ExtremeCraftingTableCategory.RECIPE_TYPE);
    }
}
