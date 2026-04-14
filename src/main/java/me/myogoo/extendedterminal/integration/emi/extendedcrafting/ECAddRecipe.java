package me.myogoo.extendedterminal.integration.emi.extendedcrafting;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import dev.emi.emi.api.EmiRegistry;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiAddRecipe;
import me.myogoo.extendedterminal.integration.emi.extendedcrafting.recipe.ECTableRecipe;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;

@ModAccessor.ExtendedCrafting
@ETEmiAddRecipe
public class ECAddRecipe {
    @MyotusSubscriber
    public static void register(EmiRegistry registry) {
        registry.getRecipeManager().getAllRecipesFor(ModRecipeTypes.TABLE.get())
                .stream()
                .map(recipe -> switch (recipe.getTier()) {
                    case 1 -> new ECTableRecipe(ECRecipeCategory.BASIC_TABLE_CRAFTING_CATEGORY, recipe);
                    case 2 -> new ECTableRecipe(ECRecipeCategory.ADVANCED_TABLE_CRAFTING_CATEGORY, recipe);
                    case 3 -> new ECTableRecipe(ECRecipeCategory.ELITE_TABLE_CRAFTING_CATEGORY, recipe);
                    case 4 -> new ECTableRecipe(ECRecipeCategory.ULTIMATE_TABLE_CRAFTING_CATEGORY, recipe);
                    default -> null;
                })
                .filter(recipe -> recipe != null)
                .forEach(registry::addRecipe);
    }
}
