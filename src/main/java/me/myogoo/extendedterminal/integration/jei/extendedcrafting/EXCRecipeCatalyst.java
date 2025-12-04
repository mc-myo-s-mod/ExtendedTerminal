package me.myogoo.extendedterminal.integration.jei.extendedcrafting;

import com.blakebr0.extendedcrafting.compat.jei.category.table.EpicTableCategory;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeCatalyst;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import me.myogoo.extendedterminal.init.ETParts;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

@ModAccessor.EpicExCrafting
@ETJeiRecipeCatalyst
public class EXCRecipeCatalyst {
    @SubscribeLoadEvent
    public static void init(IRecipeCatalystRegistration registration) {
        if(ExtendedCraftingConfig.INSTANCE.getEpicConfig().enableTerminal()) {
            registration.addRecipeCatalyst(ETParts.EPIC_TERMINAL_PART, EpicTableCategory.RECIPE_TYPE);
        }
    }
}
