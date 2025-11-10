package me.myogoo.extendedterminal.integration.emi;

import dev.emi.emi.api.EmiRegistry;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiAddRecipe;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiCategory;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiRecipeHandler;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiWorkStation;
import me.myogoo.extendedterminal.integration.ItemListModLoadHelper;

public class EmiRegisterHelper {
    public static void registerCategories(EmiRegistry registry) {
        ItemListModLoadHelper
                .invokeItemListMod(ETEmiCategory.class, EmiRegistry.class, registry);
    }

    public static void registerWorkStations(EmiRegistry registry) {
        ItemListModLoadHelper
                .invokeItemListMod(ETEmiWorkStation.class, EmiRegistry.class, registry);
    }

    public static void registerRecipeHandlers(EmiRegistry registry) {
        ItemListModLoadHelper
                .invokeItemListMod(ETEmiRecipeHandler.class, EmiRegistry.class, registry);
    }

    public static void addRecipes(EmiRegistry registry) {
        ItemListModLoadHelper
                .invokeItemListMod(ETEmiAddRecipe.class, EmiRegistry.class, registry);
    }
}
