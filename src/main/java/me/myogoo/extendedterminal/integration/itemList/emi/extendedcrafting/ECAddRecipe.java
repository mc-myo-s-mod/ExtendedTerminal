package me.myogoo.extendedterminal.integration.itemList.emi.extendedcrafting;

import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import dev.emi.emi.api.EmiRegistry;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.ETSubscribeEvent;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiAddRecipe;
import me.myogoo.extendedterminal.integration.itemList.emi.extendedcrafting.recipe.ECTableRecipe;

@ModAccessor.ExtendedCrafting
@ETEmiAddRecipe
public class ECAddRecipe {
    @ETSubscribeEvent
    public static void register(EmiRegistry registry) {
        registry.getRecipeManager().getAllRecipesFor(ModRecipeTypes.TABLE.get())
                .stream()
                .map(x -> {
                   switch (x.value().getTier()) {
                          case 1 -> {
                              return new ECTableRecipe(ECRecipeCategory.BASIC_TABLE_CRAFTING_CATEGORY, x);
                          }
                          case 2 -> {
                              return new ECTableRecipe(ECRecipeCategory.ADVANCED_TABLE_CRAFTING_CATEGORY, x);
                          }
                          case 3 -> {
                              return new ECTableRecipe(ECRecipeCategory.ELITE_TABLE_CRAFTING_CATEGORY, x);
                          }
                          case 4 -> {
                              return new ECTableRecipe(ECRecipeCategory.ULTIMATE_TABLE_CRAFTING_CATEGORY, x);
                          }
                          default -> {
                            return null;
                          }
                   }
                })
                .forEach(registry::addRecipe);
    }
}
