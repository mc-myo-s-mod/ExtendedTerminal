package me.myogoo.extendedterminal.integration.itemList.jei.extendedcrafting;

import com.blakebr0.extendedcrafting.compat.jei.category.table.AdvancedTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.BasicTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.EliteTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.UltimateTableCategory;
import me.myogoo.extendedterminal.api.ETSubscribeEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeCatalyst;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.extendedterminal.api.ModAccessor;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

@ModAccessor.ExtendedCrafting
@ETJeiRecipeCatalyst
public class ECRecipeCatalyst{
    @ETSubscribeEvent
    public static void init(IRecipeCatalystRegistration registration) {
        if (ExtendedCraftingConfig.INSTANCE.getBasicConfig().enableTerminal())
            registration.addRecipeCatalyst(ETParts.BASIC_TERMINAL_PART, BasicTableCategory.RECIPE_TYPE);
        if (ExtendedCraftingConfig.INSTANCE.getAdvancedConfig().enableTerminal())
            registration.addRecipeCatalyst(ETParts.ADVANCED_TERMINAL_PART, AdvancedTableCategory.RECIPE_TYPE);
        if (ExtendedCraftingConfig.INSTANCE.getEliteConfig().enableTerminal())
            registration.addRecipeCatalyst(ETParts.ELITE_TERMINAL_PART, EliteTableCategory.RECIPE_TYPE);
        if (ExtendedCraftingConfig.INSTANCE.getUltimateConfig().enableTerminal())
            registration.addRecipeCatalyst(ETParts.ULTIMATE_TERMINAL_PART, UltimateTableCategory.RECIPE_TYPE);

    }
}
