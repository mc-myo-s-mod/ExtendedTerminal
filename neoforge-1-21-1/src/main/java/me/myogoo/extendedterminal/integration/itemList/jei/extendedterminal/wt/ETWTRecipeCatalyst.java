package me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.wt;

import me.myogoo.myotus.api.annotation.itemList.RecipeCategory;
import me.myogoo.myotus.api.annotation.itemList.jei.JEI;
import me.myogoo.extendedterminal.init.ETItems;
import me.myogoo.extendedterminal.init.wt.WTItems;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

@AE2WTLib
@JEI
@RecipeCategory
public class ETWTRecipeCatalyst {
    @MyotusSubscriber
    public static void init(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(WTItems.WIRELESS_ET_TERMINAL, RecipeTypes.CRAFTING);
        registration.addRecipeCatalyst(WTItems.WIRELESS_ET_TERMINAL, RecipeTypes.SMITHING);
        registration.addRecipeCatalyst(WTItems.WIRELESS_ET_TERMINAL, RecipeTypes.STONECUTTING);
        registration.addRecipeCatalyst(WTItems.WIRELESS_ET_TERMINAL, RecipeTypes.ANVIL);
    }
}
