package me.myogoo.extendedterminal.integration.jei.extendedterminal.wt;

import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeCatalyst;
import me.myogoo.extendedterminal.init.ETItems;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

@ModAccessor.AE2WT
@ETJeiRecipeCatalyst
public class ETWTRecipeCatalyst {
    @SubscribeLoadEvent
    public static void init(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ETItems.ET_WT, RecipeTypes.CRAFTING);
        registration.addRecipeCatalyst(ETItems.ET_WT, RecipeTypes.SMITHING);
        registration.addRecipeCatalyst(ETItems.ET_WT, RecipeTypes.STONECUTTING);
    }
}
