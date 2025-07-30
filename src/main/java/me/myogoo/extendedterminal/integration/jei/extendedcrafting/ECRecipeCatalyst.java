package me.myogoo.extendedterminal.integration.jei.extendedcrafting;

import com.blakebr0.extendedcrafting.compat.jei.category.table.AdvancedTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.BasicTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.EliteTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.UltimateTableCategory;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeCatalyst;
import me.myogoo.extendedterminal.config.ETConfig;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.extendedterminal.api.ModAccessor;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

@ModAccessor.ExtendedCrafting
@ETJeiRecipeCatalyst
public class ECRecipeCatalyst{
    public static void init(IRecipeCatalystRegistration registration) {
        if (ETConfig.BASIC_TERMINAL_CONFIG.enableTerminal())
            registration.addRecipeCatalyst(ETParts.BASIC_TERMINAL_PART, BasicTableCategory.RECIPE_TYPE);
        if (ETConfig.ADVANCED_TERMINAL_CONFIG.enableTerminal())
            registration.addRecipeCatalyst(ETParts.ADVANCED_TERMINAL_PART, AdvancedTableCategory.RECIPE_TYPE);
        if (ETConfig.ELITE_TERMINAL_CONFIG.enableTerminal())
            registration.addRecipeCatalyst(ETParts.ELITE_TERMINAL_PART, EliteTableCategory.RECIPE_TYPE);
        if (ETConfig.ULTIMATE_TERMINAL_CONFIG.enableTerminal())
            registration.addRecipeCatalyst(ETParts.ULTIMATE_TERMINAL_PART, UltimateTableCategory.RECIPE_TYPE);

    }
}
