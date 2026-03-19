package me.myogoo.extendedterminal.init;

import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.myotus.api.MyotusAPI;

public class ETModIntegration {
    public static void initialize() {
        var register = MyotusAPI.get().modRegistrar();
        register.loadableMod(ModAccessor.ExtendedCrafting.class, "extendedcrafting");
        register.loadableMod(ModAccessor.AvaritiaNeo.class, "avaritia", "AvaritiaNeo", "[1.2.7,]");
        register.loadableMod(ModAccessor.ReAvaritia.class, "avaritia", "Re-Avaritia", "[1.3.9.6,]");
    }
}
