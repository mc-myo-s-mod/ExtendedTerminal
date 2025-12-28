package me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.wt;

import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.ETSubscribeEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeTransfer;
import me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler.panel.ETCraftingRecipeTransfer;
import me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler.panel.ETSmithingRecipeTransfer;
import me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler.panel.ETStonecutterRecipeTransfer;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeTransferRegistration;

@ModAccessor.AE2WTLib
@ETJeiRecipeTransfer
public class ETWTRecipeTransferHandler {
    @ETSubscribeEvent
    public static void init(IRecipeTransferRegistration registration) {
        var helper = registration.getTransferHelper();
        registration.addRecipeTransferHandler(new ETCraftingRecipeTransfer<>(ETWTMenu.TYPE, ETWTMenu.class, helper), RecipeTypes.CRAFTING);
        registration.addRecipeTransferHandler(new ETSmithingRecipeTransfer<>(ETWTMenu.TYPE, ETWTMenu.class, helper), RecipeTypes.SMITHING);
        registration.addRecipeTransferHandler(new ETStonecutterRecipeTransfer<>(ETWTMenu.TYPE, ETWTMenu.class, helper), RecipeTypes.STONECUTTING);
    }
}
