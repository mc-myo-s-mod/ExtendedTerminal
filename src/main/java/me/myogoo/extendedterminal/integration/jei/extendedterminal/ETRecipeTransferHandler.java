package me.myogoo.extendedterminal.integration.jei.extendedterminal;

import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeTransfer;
import me.myogoo.extendedterminal.config.ExtendedTerminalConfig;
import me.myogoo.extendedterminal.integration.jei.extendedterminal.handler.pnael.ETCraftingRecipeTransfer;
import me.myogoo.extendedterminal.integration.jei.extendedterminal.handler.pnael.ETSmithingRecipeTransfer;
import me.myogoo.extendedterminal.integration.jei.extendedterminal.handler.pnael.ETStonecutterRecipeTransfer;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeTransferRegistration;

@ETJeiRecipeTransfer
public class ETRecipeTransferHandler {
    @SubscribeLoadEvent
    public static void init(IRecipeTransferRegistration registration) {
        var config = ExtendedTerminalConfig.INSTANCE.getExtendedTerminalConfig();
        var helper = registration.getTransferHelper();

        if(config.enableCraftingPanel()) registration.addRecipeTransferHandler(new ETCraftingRecipeTransfer(ETTerminalMenu.TYPE, ETTerminalMenu.class, helper), RecipeTypes.CRAFTING);
        if(config.enableSmithingPanel()) registration.addRecipeTransferHandler(new ETSmithingRecipeTransfer(ETTerminalMenu.TYPE, ETTerminalMenu.class,helper), RecipeTypes.SMITHING);
        if(config.enableStonecutterPanel()) registration.addRecipeTransferHandler(new ETStonecutterRecipeTransfer(ETTerminalMenu.TYPE, ETTerminalMenu.class,helper), RecipeTypes.STONECUTTING);
    }
}
