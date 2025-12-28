package me.myogoo.extendedterminal.integration.itemList.jei;

import me.myogoo.extendedterminal.api.integration.jei.ETJeiGuiHandler;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeCatalyst;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeTransfer;
import me.myogoo.extendedterminal.integration.itemList.ItemListModLoadHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import org.jetbrains.annotations.NotNull;

public class JeiRegisterHelper {
    public static void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        ItemListModLoadHelper
                .invokeItemListMod(ETJeiRecipeCatalyst.class, IRecipeCatalystRegistration.class, registration);
    }

    public static void registerRecipeTransfer(@NotNull IRecipeTransferRegistration registration) {
        ItemListModLoadHelper
                .invokeItemListMod(ETJeiRecipeTransfer.class, IRecipeTransferRegistration.class, registration);
    }

    public static void registerGuiHandler(@NotNull IGuiHandlerRegistration registration) {
        ItemListModLoadHelper
                .invokeItemListMod(ETJeiGuiHandler.class, IGuiHandlerRegistration.class, registration);
    }
}
