package me.myogoo.extendedterminal.integration.itemList.emi.extendedterminal.wt;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;
import me.myogoo.extendedterminal.api.ETSubscribeEvent;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiCategory;
import me.myogoo.extendedterminal.init.ETItems;

@ETEmiCategory
@ModAccessor.AE2WTLib
public class ETWTRecipeCategory {
    @ETSubscribeEvent
    public static void init(EmiRegistry registry) {
        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(ETItems.WIRELESS_ET_TERMINAL));
        registry.addWorkstation(VanillaEmiRecipeCategories.SMITHING, EmiStack.of(ETItems.WIRELESS_ET_TERMINAL));
        registry.addWorkstation(VanillaEmiRecipeCategories.ANVIL_REPAIRING, EmiStack.of(ETItems.WIRELESS_ET_TERMINAL));
        registry.addWorkstation(VanillaEmiRecipeCategories.STONECUTTING, EmiStack.of(ETItems.WIRELESS_ET_TERMINAL));
    }
}
