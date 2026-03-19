package me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal;

import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeCatalyst;
import me.myogoo.extendedterminal.config.ExtendedTerminalConfig;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

@ETJeiRecipeCatalyst
public class ETRecipeCatalyst {
    @MyotusSubscriber
    public static void init(IRecipeCatalystRegistration registration) {
        var config = ExtendedTerminalConfig.INSTANCE.getExtendedTerminalConfig();
        if (config.enableTerminal()) {
            if(config.enableCraftingPanel()) registration.addRecipeCatalyst(ETParts.ET_TERMINAL_PART, RecipeTypes.CRAFTING);
            if(config.enableSmithingPanel()) registration.addRecipeCatalyst(ETParts.ET_TERMINAL_PART, RecipeTypes.SMITHING);
            if(config.enableStonecutterPanel()) registration.addRecipeCatalyst(ETParts.ET_TERMINAL_PART, RecipeTypes.STONECUTTING);
            if(config.enableAnvilPanel()) registration.addRecipeCatalyst(ETParts.ET_TERMINAL_PART, RecipeTypes.ANVIL);
        }
    }
}