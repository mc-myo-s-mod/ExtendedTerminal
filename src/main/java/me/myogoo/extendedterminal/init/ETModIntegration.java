package me.myogoo.extendedterminal.init;

import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.myotus.api.MyotusAPI;

public class ETModIntegration {
    public static void initialize() {
        var register = MyotusAPI.get().modRegistrar();
        register.loadableMod(ModAccessor.ExtendedCrafting.class, "extendedcrafting");
        register.loadableMod(ModAccessor.Curios.class, "curios");
        register.loadableMod(ModAccessor.AvaritiaNeo.class, "avaritia", "Avaritia", "[1.2.7,]");
        register.loadableMod(ModAccessor.ReAvaritia.class, "avaritia", "Re-Avaritia", "[1.3.9.6,]");
        register.loadableMod(ModAccessor.Polymorph.class, "polyeng");
    }
}
