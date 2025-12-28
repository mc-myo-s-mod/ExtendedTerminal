package me.myogoo.extendedterminal.integration.itemList.emi.extendedterminal;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;
import me.myogoo.extendedterminal.api.ETSubscribeEvent;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiWorkStation;
import me.myogoo.extendedterminal.init.ETParts;

@ETEmiWorkStation
public class ETWorkStation {
    @ETSubscribeEvent
    public static void init(EmiRegistry registry) {
        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(ETParts.ET_TERMINAL_PART));
        registry.addWorkstation(VanillaEmiRecipeCategories.SMITHING, EmiStack.of(ETParts.ET_TERMINAL_PART));
        registry.addWorkstation(VanillaEmiRecipeCategories.ANVIL_REPAIRING, EmiStack.of(ETParts.ET_TERMINAL_PART));
        registry.addWorkstation(VanillaEmiRecipeCategories.STONECUTTING, EmiStack.of(ETParts.ET_TERMINAL_PART));
    }
}
