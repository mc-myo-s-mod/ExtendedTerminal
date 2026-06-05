package me.myogoo.extendedterminal.config.extendedcrafting;

import me.myogoo.extendedterminal.api.config.IETConfig;
import me.myogoo.extendedterminal.api.config.IETTerminalConfig;
import me.myogoo.extendedterminal.config.TerminalConfigEntry;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class ExtendedCraftingConfig implements IETConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec COMMON;

    private static final TerminalConfigEntry BASIC_ENTRY;
    private static final TerminalConfigEntry ADVANCED_ENTRY;
    private static final TerminalConfigEntry ELITE_ENTRY;
    private static final TerminalConfigEntry ULTIMATE_ENTRY;

    public final static ExtendedCraftingConfig INSTANCE;

    static {
        BUILDER.comment("Extended Crafting Terminal Settings").push("ExtendedCrafting");
        BASIC_ENTRY = TerminalConfigEntry.instantCreate(BUILDER, "Basic Crafting Terminal", 1);
        ADVANCED_ENTRY = TerminalConfigEntry.instantCreate(BUILDER, "Advanced Crafting Terminal", 1);
        ELITE_ENTRY = TerminalConfigEntry.instantCreate(BUILDER, "Elite Crafting Terminal", 1);
        ULTIMATE_ENTRY = TerminalConfigEntry.instantCreate(BUILDER, "Ultimate Crafting Terminal", 1);
        BUILDER.pop();
        COMMON = BUILDER.build();
        INSTANCE = new ExtendedCraftingConfig();
    }

    private IETTerminalConfig basic;
    private IETTerminalConfig advanced;
    private IETTerminalConfig elite;
    private IETTerminalConfig ultimate;

    public void bake() {
        this.basic = BASIC_ENTRY.bake();
        this.advanced = ADVANCED_ENTRY.bake();
        this.elite = ELITE_ENTRY.bake();
        this.ultimate = ULTIMATE_ENTRY.bake();
    }

    private ExtendedCraftingConfig() {
    }

    public IETTerminalConfig getBasicConfig() {
        return this.basic;
    }

    public IETTerminalConfig getAdvancedConfig() {
        return this.advanced;
    }

    public IETTerminalConfig getEliteConfig() {
        return this.elite;
    }

    public IETTerminalConfig getUltimateConfig() {
        return this.ultimate;
    }
}
