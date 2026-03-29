package me.myogoo.extendedterminal.integration.itemList.emi.extendedterminal.wt;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiCategory;
import me.myogoo.extendedterminal.init.wt.WTItems;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.wt.AE2WTLib;

@ETEmiCategory
@AE2WTLib
public class ETWTRecipeCategory {
    @MyotusSubscriber
    public static void init(EmiRegistry registry) {
        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(WTItems.WIRELESS_ET_TERMINAL));
        registry.addWorkstation(VanillaEmiRecipeCategories.SMITHING, EmiStack.of(WTItems.WIRELESS_ET_TERMINAL));
        registry.addWorkstation(VanillaEmiRecipeCategories.ANVIL_REPAIRING, EmiStack.of(WTItems.WIRELESS_ET_TERMINAL));
        registry.addWorkstation(VanillaEmiRecipeCategories.STONECUTTING, EmiStack.of(WTItems.WIRELESS_ET_TERMINAL));
    }
}
