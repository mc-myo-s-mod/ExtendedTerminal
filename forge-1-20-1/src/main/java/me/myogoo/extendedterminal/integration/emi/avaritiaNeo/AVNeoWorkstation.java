package me.myogoo.extendedterminal.integration.emi.avaritiaNeo;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.itemList.RecipeCategory;
import me.myogoo.myotus.api.annotation.itemList.emi.EMI;
import me.myogoo.extendedterminal.init.ETParts;
import net.byAqua3.avaritia.compat.emi.AvaritiaEMIPlugin;
import me.myogoo.extendedterminal.api.annotation.AvaritiaNeo;

@AvaritiaNeo
@EMI
@RecipeCategory
public class AVNeoWorkstation {
    @MyotusSubscriber
    public static void init(EmiRegistry registry) {
        registry.addWorkstation(AvaritiaEMIPlugin.EXTREME_CRAFTING, EmiStack.of(ETParts.NEO_EXTREME_TERMINAL_PART));
        if (ExtendedCraftingConfig.INSTANCE.getUltimateConfig().enableTerminal()) {
            registry.addWorkstation(AvaritiaEMIPlugin.EXTREME_CRAFTING, EmiStack.of(ETParts.UNITED_TERMINAL_PART));
        }
    }
}
