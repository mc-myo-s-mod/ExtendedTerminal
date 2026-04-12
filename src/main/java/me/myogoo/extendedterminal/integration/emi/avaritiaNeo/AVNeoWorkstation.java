package me.myogoo.extendedterminal.integration.emi.avaritiaNeo;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiWorkstation;
import me.myogoo.extendedterminal.init.ETParts;
import net.byAqua3.avaritia.compat.emi.AvaritiaEMIPlugin;

@ModAccessor.AvaritiaNeo
@ETEmiWorkstation
public class AVNeoWorkstation {
    @MyotusSubscriber
    public static void init(EmiRegistry registry) {
        registry.addWorkstation(AvaritiaEMIPlugin.EXTREME_CRAFTING, EmiStack.of(ETParts.NEO_EXTREME_TERMINAL_PART));
    }
}
