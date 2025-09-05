package me.myogoo.extendedterminal.integration.emi.avaritiaRe;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiWorkStation;
import committee.nova.mods.avaritia.init.compat.emi.category.tables.*;
import me.myogoo.extendedterminal.init.ETParts;

@ModAccessor.ReAvaritia
@ETEmiWorkStation
public class AVWorkStation {
    @SubscribeLoadEvent
    public static void register(EmiRegistry registry) {
        registry.addWorkstation(SculkCraftingTableCategory.CATEGORY, EmiStack.of(ETParts.SCULK_TERMINAL_PART.get()));
        registry.addWorkstation(NetherCraftingTableCategory.CATEGORY, EmiStack.of(ETParts.NETHER_TERMINAL_PART.get()));
        registry.addWorkstation(EndCraftingTableCategory.CATEGORY, EmiStack.of(ETParts.END_TERMINAL_PART.get()));
        registry.addWorkstation(ExtremeCraftingTableCategory.CATEGORY, EmiStack.of(ETParts.EXTREME_TERMINAL_PART.get()));
    }
}
