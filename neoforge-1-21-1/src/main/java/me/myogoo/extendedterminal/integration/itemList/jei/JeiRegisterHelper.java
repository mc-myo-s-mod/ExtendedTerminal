package me.myogoo.extendedterminal.integration.itemList.jei;

import me.myogoo.myotus.api.annotation.itemList.jei.JEIGuiHandler;
import me.myogoo.myotus.api.annotation.itemList.RecipeCategory;
import me.myogoo.myotus.api.annotation.itemList.RecipeTransfer;
import me.myogoo.myotus.integration.itemList.ItemListModLoadHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import org.jetbrains.annotations.NotNull;

public class JeiRegisterHelper {
    public static void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        ItemListModLoadHelper
                .invokeItemListMod(RecipeCategory.class, IRecipeCatalystRegistration.class, registration);
    }

    public static void registerRecipeTransfer(@NotNull IRecipeTransferRegistration registration) {
        ItemListModLoadHelper
                .invokeItemListMod(RecipeTransfer.class, IRecipeTransferRegistration.class, registration);
    }

    public static void registerGuiHandler(@NotNull IGuiHandlerRegistration registration) {
        ItemListModLoadHelper
                .invokeItemListMod(JEIGuiHandler.class, IGuiHandlerRegistration.class, registration);
    }
}
