package me.myogoo.extendedterminal.config;

import me.myogoo.extendedterminal.api.config.IETTerminalConfig;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Helper entry representing one terminal's config triple (enable, craft only when powered, passive drain).
 */
public final class TerminalConfigEntry { // made public
    private final ForgeConfigSpec.BooleanValue enable;
    private final ForgeConfigSpec.BooleanValue craftOnlyPowered;
    private final ForgeConfigSpec.DoubleValue passiveDrain;

    private TerminalConfigEntry(ForgeConfigSpec.Builder builder, String commentName, double defaultDrain, boolean pop) {
        builder.comment(commentName).push(commentName);
        this.enable = builder.comment("Enable this terminal").define("Enable", true);
        this.craftOnlyPowered = builder.comment("AE system must be online & powered to craft").define("Craftable when Active", false);
        this.passiveDrain = builder.comment("Passive AE/t drain").defineInRange("Passive drain AE", defaultDrain, 0.0, Double.MAX_VALUE);
        if(pop) builder.pop();
    }


    public static TerminalConfigEntry instantCreate(ForgeConfigSpec.Builder builder, String name, double defaultDrain) {
        return new TerminalConfigEntry(builder, name, defaultDrain, true);
    }

    public static TerminalConfigEntry instantCreate(ForgeConfigSpec.Builder builder, String name, double defaultDrain, boolean pop) {
        return new TerminalConfigEntry(builder, name, defaultDrain, pop);
    }

    // Make public for subpackages
    public IETTerminalConfig bake() {
        return new TerminalConfigData(enable.get(), craftOnlyPowered.get(), passiveDrain.get());
    }

    /** Runtime snapshot implementing the public interface */
    private record TerminalConfigData(boolean enableTerminal, boolean enableCraftOnlyPowered, double passiveDrainAE)
            implements IETTerminalConfig { }
}
