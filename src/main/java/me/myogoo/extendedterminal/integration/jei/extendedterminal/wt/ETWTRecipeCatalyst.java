package me.myogoo.extendedterminal.integration.jei.extendedterminal.wt;

import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeCatalyst;
import me.myogoo.extendedterminal.init.ETItems;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

@ModAccessor.AE2WTLib
@ETJeiRecipeCatalyst
public class ETWTRecipeCatalyst {
    @SubscribeLoadEvent
    public static void init(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ETItems.WIRELESS_ET_TERMINAL, RecipeTypes.CRAFTING);
        registration.addRecipeCatalyst(ETItems.WIRELESS_ET_TERMINAL, RecipeTypes.SMITHING);
        registration.addRecipeCatalyst(ETItems.WIRELESS_ET_TERMINAL, RecipeTypes.STONECUTTING);
    }
}
