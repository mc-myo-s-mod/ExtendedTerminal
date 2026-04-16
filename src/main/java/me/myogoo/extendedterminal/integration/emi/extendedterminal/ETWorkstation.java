package me.myogoo.extendedterminal.integration.emi.extendedterminal;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiWorkstation;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;

@ETEmiWorkstation
public class ETWorkstation {
    @MyotusSubscriber
    public static void init(EmiRegistry registry) {
        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(ETParts.ET_TERMINAL_PART));
        registry.addWorkstation(VanillaEmiRecipeCategories.SMITHING, EmiStack.of(ETParts.ET_TERMINAL_PART));
        registry.addWorkstation(VanillaEmiRecipeCategories.STONECUTTING, EmiStack.of(ETParts.ET_TERMINAL_PART));
        registry.addWorkstation(VanillaEmiRecipeCategories.ANVIL_REPAIRING, EmiStack.of(ETParts.ET_TERMINAL_PART));
    }
}
