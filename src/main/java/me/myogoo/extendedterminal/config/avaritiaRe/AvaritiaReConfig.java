package me.myogoo.extendedterminal.config.avaritiaRe;

import me.myogoo.extendedterminal.api.config.IETConfig;
import me.myogoo.extendedterminal.api.config.IETTerminalConfig;
import me.myogoo.extendedterminal.config.TerminalConfigEntry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class AvaritiaReConfig implements IETConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec COMMON;

    private static final TerminalConfigEntry SCULK_ENTRY;
    private static final TerminalConfigEntry NETHER_ENTRY;
    private static final TerminalConfigEntry END_ENTRY;
    private static final TerminalConfigEntry EXTREME_ENTRY;

    public final static AvaritiaReConfig INSTANCE;

    static {
        BUILDER.comment("Re:Avaritia Terminal Settings").push("ReAvaritia");
        SCULK_ENTRY = TerminalConfigEntry.instantCreate(BUILDER, "Sculk Crafting Terminal", 1);
        NETHER_ENTRY = TerminalConfigEntry.instantCreate(BUILDER, "Nether Crafting Terminal", 1);
        END_ENTRY = TerminalConfigEntry.instantCreate(BUILDER, "End Crafting Terminal", 1);
        EXTREME_ENTRY = TerminalConfigEntry.instantCreate(BUILDER, "Extreme Crafting Terminal", 1);
        BUILDER.pop();
        COMMON = BUILDER.build();
        INSTANCE = new AvaritiaReConfig();
    }

    private IETTerminalConfig sculk;
    private IETTerminalConfig nether;
    private IETTerminalConfig end;
    private IETTerminalConfig extreme;

    public void bake() {
        this.sculk = SCULK_ENTRY.bake();
        this.nether = NETHER_ENTRY.bake();
        this.end = END_ENTRY.bake();
        this.extreme = EXTREME_ENTRY.bake();
    }

    private AvaritiaReConfig() {
    }

    public IETTerminalConfig getSculkConfig() {
        return this.sculk;
    }

    public IETTerminalConfig getNetherConfig() {
        return this.nether;
    }

    public IETTerminalConfig getEndConfig() {
        return this.end;
    }

    public IETTerminalConfig getExtremeConfig() {
        return this.extreme;
    }
}
