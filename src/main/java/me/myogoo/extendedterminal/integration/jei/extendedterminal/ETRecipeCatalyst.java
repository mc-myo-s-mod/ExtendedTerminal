package me.myogoo.extendedterminal.integration.jei.extendedterminal;

import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeCatalyst;
import me.myogoo.extendedterminal.config.ExtendedTerminalConfig;
import me.myogoo.extendedterminal.init.ETParts;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

@ETJeiRecipeCatalyst
public class ETRecipeCatalyst {
    @SubscribeLoadEvent
    public static void init(IRecipeCatalystRegistration registration) {
        var etconfig = ExtendedTerminalConfig.INSTANCE.getExtendedTerminalConfig();
        if (etconfig.enableTerminal()) {
            if(etconfig.enableCraftingPanel()) registration.addRecipeCatalyst(ETParts.ET_TERMINAL_PART, RecipeTypes.CRAFTING);
            if(etconfig.enableSmithingPanel()) registration.addRecipeCatalyst(ETParts.ET_TERMINAL_PART, RecipeTypes.SMITHING);
            if(etconfig.enableStonecutterPanel()) registration.addRecipeCatalyst(ETParts.ET_TERMINAL_PART, RecipeTypes.STONECUTTING);
            if(etconfig.enableAnvilPanel()) registration.addRecipeCatalyst(ETParts.ET_TERMINAL_PART, RecipeTypes.ANVIL);
        }
    }
}