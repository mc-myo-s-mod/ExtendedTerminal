package me.myogoo.extendedterminal.init;

import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.wt.AE2WTLib;

public final class ETModIntegration {
    private static boolean initialized;

    private ETModIntegration() {
    }

    public static synchronized void initialize() {
        if (initialized || !MyotusAPI.isInitialized()) {
            return;
        }

        initialized = true;

        var registrar = MyotusAPI.modRegistrar();
        registrar.registerLoadableMod(ModAccessor.ExtendedCrafting.class, "extendedcrafting", "*");
        registrar.registerLoadableMod(ModAccessor.EpicExCrafting.class, "extendedcrafting",
                "Extended Crafting: Expanded", "*");
        registrar.registerLoadableMod(ModAccessor.ReAvaritia.class, "avaritia", "Re-Avaritia", "*");
        registrar.registerLoadableMod(ModAccessor.AvaritiaNeo.class, "avaritia", "Avaritia", "*");
    }
}
