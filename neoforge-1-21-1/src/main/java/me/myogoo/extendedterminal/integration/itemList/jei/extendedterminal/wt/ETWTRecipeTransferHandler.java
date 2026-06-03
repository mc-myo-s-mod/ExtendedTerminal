package me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.wt;

import me.myogoo.myotus.api.annotation.itemList.RecipeTransfer;
import me.myogoo.myotus.api.annotation.itemList.jei.JEI;
import me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler.panel.ETCraftingRecipeTransfer;
import me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler.panel.ETSmithingRecipeTransfer;
import me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler.panel.ETStonecutterRecipeTransfer;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeTransferRegistration;

@AE2WTLib
@JEI
@RecipeTransfer
public class ETWTRecipeTransferHandler {
    @MyotusSubscriber
    public static void init(IRecipeTransferRegistration registration) {
        var helper = registration.getTransferHelper();
        registration.addRecipeTransferHandler(new ETCraftingRecipeTransfer<>(ETWTMenu.TYPE, ETWTMenu.class, helper), RecipeTypes.CRAFTING);
        registration.addRecipeTransferHandler(new ETSmithingRecipeTransfer<>(ETWTMenu.TYPE, ETWTMenu.class, helper), RecipeTypes.SMITHING);
        registration.addRecipeTransferHandler(new ETStonecutterRecipeTransfer<>(ETWTMenu.TYPE, ETWTMenu.class, helper), RecipeTypes.STONECUTTING);
    }
}
