package me.myogoo.extendedterminal.integration.itemList.jei.avaritiaNeo;

import me.myogoo.myotus.api.annotation.itemList.RecipeCategory;
import me.myogoo.myotus.api.annotation.itemList.jei.JEI;
import me.myogoo.extendedterminal.config.avaritiaNeo.AvaritiaNeoConfig;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.byAqua3.avaritia.compat.jei.AvaritiaJEIPlugin;
import me.myogoo.extendedterminal.api.annotation.AvaritiaNeo;

@JEI
@RecipeCategory
@AvaritiaNeo
public class AVNeoRecipeCatalyst {
    @MyotusSubscriber
    public static void init(IRecipeCatalystRegistration registration) {
        if(AvaritiaNeoConfig.INSTANCE.getExtremeConfig().enableTerminal()) {
            registration.addRecipeCatalyst(ETParts.NEO_EXTREME_TERMINAL_PART, AvaritiaJEIPlugin.EXTREME_CRAFTING);
        }
        if (ExtendedCraftingConfig.INSTANCE.getUltimateConfig().enableTerminal()) {
            registration.addRecipeCatalyst(ETParts.UNITED_TERMINAL_PART, AvaritiaJEIPlugin.EXTREME_CRAFTING);
        }
    }
}
