package me.myogoo.extendedterminal.integration.jei.extendedcrafting;

import com.blakebr0.extendedcrafting.compat.jei.category.table.EpicTableCategory;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.itemList.RecipeCategory;
import me.myogoo.myotus.api.annotation.itemList.jei.JEI;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import me.myogoo.extendedterminal.init.ETParts;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import me.myogoo.extendedterminal.api.annotation.EpicExCrafting;

@EpicExCrafting
@JEI
@RecipeCategory
public class EXCRecipeCatalyst {
    @MyotusSubscriber
    public static void init(IRecipeCatalystRegistration registration) {
        if(ExtendedCraftingConfig.INSTANCE.getEpicConfig().enableTerminal()) {
            registration.addRecipeCatalyst(ETParts.EPIC_TERMINAL_PART, EpicTableCategory.RECIPE_TYPE);
        }
    }
}
