package me.myogoo.extendedterminal.integration.jei.avaritia;

import committee.nova.mods.avaritia.init.compat.jei.category.tables.EndCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.ExtremeCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.NetherCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.SculkCraftingTableCategory;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeTransfer;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.integration.jei.avaritia.handler.AVJeiRecipeTransferHandler;
import me.myogoo.extendedterminal.menu.avaritia.EndTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritia.ExtremeTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritia.NetherTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritia.SculkTerminalMenu;
import mezz.jei.api.registration.IRecipeTransferRegistration;

@ETJeiRecipeTransfer
@ModAccessor.Avaritia
public class AVRecipeTransferHandler {
    public static void init(IRecipeTransferRegistration registration) {
        var helper = registration.getTransferHelper();

        registration.addRecipeTransferHandler(new AVJeiRecipeTransferHandler<>(SculkTerminalMenu.class, SculkTerminalMenu.TYPE, SculkCraftingTableCategory.RECIPE_TYPE, helper), SculkCraftingTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(new AVJeiRecipeTransferHandler<>(NetherTerminalMenu.class, NetherTerminalMenu.TYPE, NetherCraftingTableCategory.RECIPE_TYPE, helper), NetherCraftingTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(new AVJeiRecipeTransferHandler<>(EndTerminalMenu.class, EndTerminalMenu.TYPE, EndCraftingTableCategory.RECIPE_TYPE, helper), EndCraftingTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(new AVJeiRecipeTransferHandler<>(ExtremeTerminalMenu.class, ExtremeTerminalMenu.TYPE, ExtremeCraftingTableCategory.RECIPE_TYPE, helper), ExtremeCraftingTableCategory.RECIPE_TYPE);
    }
}
