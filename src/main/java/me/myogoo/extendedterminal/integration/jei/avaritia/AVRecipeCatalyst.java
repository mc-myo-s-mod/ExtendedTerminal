package me.myogoo.extendedterminal.integration.jei.avaritia;

import committee.nova.mods.avaritia.init.compat.jei.category.tables.EndCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.ExtremeCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.NetherCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.SculkCraftingTableCategory;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeCatalyst;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.config.ETConfig;
import me.myogoo.extendedterminal.init.ETParts;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

@ETJeiRecipeCatalyst
@ModAccessor.Avaritia
public class AVRecipeCatalyst {
    public static void init(IRecipeCatalystRegistration registration) {
        if(ETConfig.SCULK_TERMINAL_CONFIG.enableTerminal()) registration.addRecipeCatalyst(ETParts.SCULK_TERMINAL_PART, SculkCraftingTableCategory.RECIPE_TYPE);
        if(ETConfig.NETHER_TERMINAL_CONFIG.enableTerminal()) registration.addRecipeCatalyst(ETParts.NETHER_TERMINAL_PART, NetherCraftingTableCategory.RECIPE_TYPE);
        if(ETConfig.END_TERMINAL_CONFIG.enableTerminal()) registration.addRecipeCatalyst(ETParts.END_TERMINAL_PART, EndCraftingTableCategory.RECIPE_TYPE);
        if(ETConfig.EXTREME_TERMINAL_CONFIG.enableTerminal()) registration.addRecipeCatalyst(ETParts.EXTREME_TERMINAL_PART, ExtremeCraftingTableCategory.RECIPE_TYPE);
    }
}
