package me.myogoo.extendedterminal.integration.emi.avaritiaRe;

import committee.nova.mods.avaritia.init.compat.emi.category.tables.EndCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.emi.category.tables.ExtremeCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.emi.category.tables.NetherCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.emi.category.tables.SculkCraftingTableCategory;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.itemList.RecipeCategory;
import me.myogoo.myotus.api.annotation.itemList.emi.EMI;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.extendedterminal.api.annotation.ReAvaritia;

@ReAvaritia
@EMI
@RecipeCategory
public class AVWorkstation {
    @MyotusSubscriber
    public static void init(EmiRegistry registry) {
        registry.addWorkstation(SculkCraftingTableCategory.CATEGORY, EmiStack.of(ETParts.SCULK_TERMINAL_PART));
        registry.addWorkstation(NetherCraftingTableCategory.CATEGORY, EmiStack.of(ETParts.NETHER_TERMINAL_PART));
        registry.addWorkstation(EndCraftingTableCategory.CATEGORY, EmiStack.of(ETParts.END_TERMINAL_PART));
        registry.addWorkstation(ExtremeCraftingTableCategory.CATEGORY, EmiStack.of(ETParts.EXTREME_TERMINAL_PART));
    }
}
