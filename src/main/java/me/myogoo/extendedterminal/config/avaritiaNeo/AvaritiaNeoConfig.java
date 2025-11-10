package me.myogoo.extendedterminal.config.avaritiaNeo;

import me.myogoo.extendedterminal.api.config.IETConfig;
import me.myogoo.extendedterminal.api.config.IETTerminalConfig;
import me.myogoo.extendedterminal.config.TerminalConfigEntry;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class AvaritiaNeoConfig implements IETConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec COMMON;

    private static final TerminalConfigEntry NEO_EXTREME_ENTRY;

    public static AvaritiaNeoConfig INSTANCE;

    static {
        BUILDER.comment("AvaritiaNeo Terminal Settings").push("AvaritiaNeo");
        NEO_EXTREME_ENTRY = TerminalConfigEntry.instantCreate(BUILDER, "Neo Extreme Crafting Terminal", 1);
        BUILDER.pop();
        COMMON = BUILDER.build();
        INSTANCE = new AvaritiaNeoConfig();
    }

    private IETTerminalConfig extreme;

    public void bake() {
        this.extreme = NEO_EXTREME_ENTRY.bake();
    }

    private AvaritiaNeoConfig() {
    }

    public IETTerminalConfig getExtremeConfig() {
        return this.extreme;
    }

}
