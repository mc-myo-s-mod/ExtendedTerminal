package me.myogoo.extendedterminal.integration.jei.extendedterminal;

import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeTransfer;
import me.myogoo.extendedterminal.integration.jei.extendedterminal.handler.pnael.ETCraftingRecipeTransfer;
import me.myogoo.extendedterminal.integration.jei.extendedterminal.handler.pnael.ETSmithingRecipeTransfer;
import me.myogoo.extendedterminal.integration.jei.extendedterminal.handler.pnael.ETStonecutterRecipeTransfer;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeTransferRegistration;

@ETJeiRecipeTransfer
public class ETRecipeTransferHandler {
    @SubscribeLoadEvent
    public static void init(IRecipeTransferRegistration registration) {
        var helper = registration.getTransferHelper();

        registration.addRecipeTransferHandler(new ETCraftingRecipeTransfer(helper), RecipeTypes.CRAFTING);
        registration.addRecipeTransferHandler(new ETSmithingRecipeTransfer(helper), RecipeTypes.SMITHING);
        registration.addRecipeTransferHandler(new ETStonecutterRecipeTransfer(helper), RecipeTypes.STONECUTTING);
    }
}
