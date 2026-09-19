package me.myogoo.extendedterminal.integration.emi.extendedterminal;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.myotus.api.annotation.itemList.RecipeCategory;
import me.myogoo.myotus.api.annotation.itemList.emi.EMI;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;

@EMI
@RecipeCategory
public class ETWorkstation {
    public static void showRecipes(ETTerminalMode mode) {
        EmiApi.displayRecipeCategory(switch (mode) {
            case CRAFTING -> VanillaEmiRecipeCategories.CRAFTING;
            case SMITHING -> VanillaEmiRecipeCategories.SMITHING;
            case STONECUTTING -> VanillaEmiRecipeCategories.STONECUTTING;
            case ANVIL -> VanillaEmiRecipeCategories.ANVIL_REPAIRING;
        });
    }

    @MyotusSubscriber
    public static void init(EmiRegistry registry) {
        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING,
                EmiStack.of(ETParts.UNITED_TERMINAL_PART));
        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(ETParts.ET_TERMINAL_PART));
        registry.addWorkstation(VanillaEmiRecipeCategories.SMITHING, EmiStack.of(ETParts.ET_TERMINAL_PART));
        registry.addWorkstation(VanillaEmiRecipeCategories.STONECUTTING, EmiStack.of(ETParts.ET_TERMINAL_PART));
        registry.addWorkstation(VanillaEmiRecipeCategories.ANVIL_REPAIRING, EmiStack.of(ETParts.ET_TERMINAL_PART));
    }
}
