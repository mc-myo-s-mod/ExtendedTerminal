package me.myogoo.extendedterminal.integration.jei.avaritiaRe;

import committee.nova.mods.avaritia.init.compat.jei.category.tables.EndCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.ExtremeCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.NetherCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.SculkCraftingTableCategory;
import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.jei.ETJeiRecipeCatalyst;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.config.avaritiaRe.AvaritiaReConfig;
import me.myogoo.extendedterminal.init.ETConfig;
import me.myogoo.extendedterminal.init.ETParts;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

@ETJeiRecipeCatalyst
@ModAccessor.ReAvaritia
public class AVRecipeCatalyst {
    @SubscribeLoadEvent
    public static void init(IRecipeCatalystRegistration registration) {
        if(AvaritiaReConfig.INSTANCE.getSculkConfig().enableTerminal()) registration.addRecipeCatalyst(ETParts.SCULK_TERMINAL_PART, SculkCraftingTableCategory.RECIPE_TYPE);
        if(AvaritiaReConfig.INSTANCE.getNetherConfig().enableTerminal()) registration.addRecipeCatalyst(ETParts.NETHER_TERMINAL_PART, NetherCraftingTableCategory.RECIPE_TYPE);
        if(AvaritiaReConfig.INSTANCE.getEndConfig().enableTerminal()) registration.addRecipeCatalyst(ETParts.END_TERMINAL_PART, EndCraftingTableCategory.RECIPE_TYPE);
        if(AvaritiaReConfig.INSTANCE.getExtremeConfig().enableTerminal()) registration.addRecipeCatalyst(ETParts.EXTREME_TERMINAL_PART, ExtremeCraftingTableCategory.RECIPE_TYPE);
    }
}
