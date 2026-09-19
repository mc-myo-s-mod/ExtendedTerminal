package me.myogoo.extendedterminal.integration.itemList.emi.avaritiaRe;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import me.myogoo.myotus.api.annotation.itemList.RecipeCategory;
import me.myogoo.myotus.api.annotation.itemList.emi.EMI;
import committee.nova.mods.avaritia.init.compat.emi.category.tables.*;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.extendedterminal.init.wt.WTItems;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import me.myogoo.extendedterminal.api.annotation.ReAvaritia;

@ReAvaritia
@EMI
@RecipeCategory
public class AVWorkStation {
    @MyotusSubscriber
    public static void register(EmiRegistry registry) {
        registry.addWorkstation(SculkCraftingTableCategory.CATEGORY, EmiStack.of(ETParts.SCULK_TERMINAL_PART.get()));
        registry.addWorkstation(NetherCraftingTableCategory.CATEGORY, EmiStack.of(ETParts.NETHER_TERMINAL_PART.get()));
        registry.addWorkstation(EndCraftingTableCategory.CATEGORY, EmiStack.of(ETParts.END_TERMINAL_PART.get()));
        registry.addWorkstation(ExtremeCraftingTableCategory.CATEGORY, EmiStack.of(ETParts.EXTREME_TERMINAL_PART.get()));
        registry.addWorkstation(SculkCraftingTableCategory.CATEGORY, EmiStack.of(ETParts.UNITED_TERMINAL_PART.get()));
        registry.addWorkstation(NetherCraftingTableCategory.CATEGORY, EmiStack.of(ETParts.UNITED_TERMINAL_PART.get()));
        registry.addWorkstation(EndCraftingTableCategory.CATEGORY, EmiStack.of(ETParts.UNITED_TERMINAL_PART.get()));
        registry.addWorkstation(ExtremeCraftingTableCategory.CATEGORY, EmiStack.of(ETParts.UNITED_TERMINAL_PART.get()));
        if (MyotusAPI.integrations().isLoaded(AE2WTLib.class)) {
            var wirelessUnited = EmiStack.of(WTItems.WIRELESS_UNITED_TERMINAL);
            registry.addWorkstation(SculkCraftingTableCategory.CATEGORY, wirelessUnited);
            registry.addWorkstation(NetherCraftingTableCategory.CATEGORY, wirelessUnited);
            registry.addWorkstation(EndCraftingTableCategory.CATEGORY, wirelessUnited);
            registry.addWorkstation(ExtremeCraftingTableCategory.CATEGORY, wirelessUnited);
        }
    }
}
