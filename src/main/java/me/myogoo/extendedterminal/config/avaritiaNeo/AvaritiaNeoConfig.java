package me.myogoo.extendedterminal.config.avaritiaNeo;

import me.myogoo.extendedterminal.api.config.IETConfig;
import me.myogoo.extendedterminal.api.config.IETTerminalConfig;
import me.myogoo.extendedterminal.config.TerminalConfigEntry;
import net.minecraftforge.common.ForgeConfigSpec;

public final class AvaritiaNeoConfig implements IETConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec COMMON;

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
