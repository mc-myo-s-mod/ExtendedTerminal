package me.myogoo.extendedterminal.integration.jei.extendedcrafting;

import com.blakebr0.extendedcrafting.compat.jei.category.table.LegendaryTableCategory;
import me.myogoo.extendedterminal.api.annotation.LegendaryExCrafting;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.extendedterminal.init.wt.WTItems;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.itemList.RecipeCategory;
import me.myogoo.myotus.api.annotation.itemList.jei.JEI;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

@LegendaryExCrafting
@JEI
@RecipeCategory
public class LegendaryRecipeCatalyst {
    @MyotusSubscriber
    public static void init(IRecipeCatalystRegistration registration) {
        if (ExtendedCraftingConfig.INSTANCE.getLegendaryConfig().enableTerminal()) {
            registration.addRecipeCatalyst(ETParts.LEGENDARY_TERMINAL_PART,
                    LegendaryTableCategory.RECIPE_TYPE);
            if (MyotusAPI.integrations().isLoaded(AE2WTLib.class)) {
                registration.addRecipeCatalyst(WTItems.WIRELESS_LEGENDARY_TERMINAL,
                        LegendaryTableCategory.RECIPE_TYPE);
            }
        }
    }
}
