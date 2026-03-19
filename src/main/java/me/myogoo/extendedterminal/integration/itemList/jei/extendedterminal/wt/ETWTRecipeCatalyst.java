package me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.wt;

import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeCatalyst;
import me.myogoo.extendedterminal.init.ETItems;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.wt.AE2WTLib;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

@AE2WTLib
@ETJeiRecipeCatalyst
public class ETWTRecipeCatalyst {
    @MyotusSubscriber
    public static void init(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ETItems.WIRELESS_ET_TERMINAL, RecipeTypes.CRAFTING);
        registration.addRecipeCatalyst(ETItems.WIRELESS_ET_TERMINAL, RecipeTypes.SMITHING);
        registration.addRecipeCatalyst(ETItems.WIRELESS_ET_TERMINAL, RecipeTypes.STONECUTTING);
        registration.addRecipeCatalyst(ETItems.WIRELESS_ET_TERMINAL, RecipeTypes.ANVIL);
    }
}
