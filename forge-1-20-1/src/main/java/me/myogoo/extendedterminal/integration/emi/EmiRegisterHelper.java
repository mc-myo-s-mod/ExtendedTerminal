package me.myogoo.extendedterminal.integration.emi;

import dev.emi.emi.api.EmiRegistry;
import me.myogoo.myotus.api.annotation.itemList.RecipeAdd;
import me.myogoo.myotus.api.annotation.itemList.RecipeCategory;
import me.myogoo.myotus.api.annotation.itemList.RecipeTransfer;
import me.myogoo.myotus.integration.itemList.ItemListModLoadHelper;

public class EmiRegisterHelper {
    public static void registerCategories(EmiRegistry registry) {
        ItemListModLoadHelper.invokeItemListMod(RecipeCategory.class, EmiRegistry.class, registry);
    }

    /**
     * Myotus' current item-list API uses RecipeCategory for both EMI categories and workstation/catalyst
     * registrations, so registerCategories already invokes both groups.
     */
    public static void registerWorkStations(EmiRegistry registry) {
    }

    public static void registerRecipeHandlers(EmiRegistry registry) {
        ItemListModLoadHelper.invokeItemListMod(RecipeTransfer.class, EmiRegistry.class, registry);
    }


    public static void addRecipes(EmiRegistry registry) {
        ItemListModLoadHelper.invokeItemListMod(RecipeAdd.class, EmiRegistry.class, registry);
    }
}
