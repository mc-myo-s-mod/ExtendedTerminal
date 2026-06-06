package me.myogoo.extendedterminal.integration.itemList.emi;

import dev.emi.emi.api.EmiRegistry;
import me.myogoo.myotus.integration.itemList.ItemListModLoadHelper;
import me.myogoo.myotus.api.annotation.itemList.RecipeCategory;
import me.myogoo.myotus.api.annotation.itemList.RecipeTransfer;

public class EmiRegisterHelper {
    public static void registerCategories(EmiRegistry registry) {
        ItemListModLoadHelper
                .invokeItemListMod(RecipeCategory.class, EmiRegistry.class, registry);
    }

    public static void registerRecipeHandlers(EmiRegistry registry) {
        ItemListModLoadHelper
                .invokeItemListMod(RecipeTransfer.class, EmiRegistry.class, registry);
    }
}
