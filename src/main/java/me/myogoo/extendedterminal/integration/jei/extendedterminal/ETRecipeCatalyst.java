package me.myogoo.extendedterminal.integration.jei.extendedterminal;

import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeCatalyst;
import me.myogoo.extendedterminal.init.ETParts;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

@ETJeiRecipeCatalyst
public class ETRecipeCatalyst {
    @SubscribeLoadEvent
    public static void init(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ETParts.ET_TERMINAL_PART, RecipeTypes.CRAFTING);
        registration.addRecipeCatalyst(ETParts.ET_TERMINAL_PART, RecipeTypes.SMITHING);
        registration.addRecipeCatalyst(ETParts.ET_TERMINAL_PART, RecipeTypes.STONECUTTING);
        registration.addRecipeCatalyst(ETParts.ET_TERMINAL_PART, RecipeTypes.ANVIL);
    }
}