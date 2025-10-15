package me.myogoo.extendedterminal.config;

import me.myogoo.extendedterminal.api.config.IETConfig;
import me.myogoo.extendedterminal.api.config.IETTerminalConfig;
import net.neoforged.neoforge.common.ModConfigSpec;


public class ExtendedTerminalConfig implements IETConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec COMMON;

    private static final TerminalConfigEntry ET_ENTRY;
    private static final ModConfigSpec.BooleanValue ENABLE_CRAFTING_PANEL;
    private static final ModConfigSpec.BooleanValue ENABLE_SMITHING_PANEL;
    private static final ModConfigSpec.BooleanValue ENABLE_STONECUTTER_PANEL;
    private static final ModConfigSpec.BooleanValue ENABLE_ANVIL_PANEL;

    public static final ExtendedTerminalConfig INSTANCE;

    static {
        BUILDER.comment("Extended Terminal Settings").push("ExtendedTerminal");
        ET_ENTRY = TerminalConfigEntry.instantCreate(BUILDER, "Extended Terminal", 1, false);
        ENABLE_CRAFTING_PANEL = BUILDER
                .comment("Enable Crafting Panel in Extended Terminal")
                .define("Enable Crafting Panel", true);
        ENABLE_SMITHING_PANEL = BUILDER
                .comment("Enable Smithing Panel in Extended Terminal")
                .define("Enable Smithing Panel", true);
        ENABLE_STONECUTTER_PANEL = BUILDER
                .comment("Enable Stonecutter Panel in Extended Terminal")
                .define("Enable Stonecutter Panel", true);
        ENABLE_ANVIL_PANEL = BUILDER
                .comment("Enable Anvil Panel in Extended Terminal")
                .define("Enable Anvil Panel", true);

        BUILDER.pop();

        COMMON = BUILDER.build();
        INSTANCE = new ExtendedTerminalConfig();
    }

    private IETTerminalConfig et;

    public void bake() {
        this.et = ET_ENTRY.bake();
    }

    private ExtendedTerminalConfig() {
    }

    public IExtendedTerminalConfig getExtendedTerminalConfig() {
        return new ExtendedTerminalConfigImpl(
                this.et.enableTerminal(),
                this.et.enableCraftOnlyPowered(),
                this.et.passiveDrainAE(),
                ENABLE_CRAFTING_PANEL.get(),
                ENABLE_SMITHING_PANEL.get(),
                ENABLE_STONECUTTER_PANEL.get(),
                ENABLE_ANVIL_PANEL.get()
        );
    }

    private record ExtendedTerminalConfigImpl(
            boolean enableTerminal,
            boolean enableCraftOnlyPowered,
            double passiveDrainAE,
            boolean enableCraftingPanel,
            boolean enableSmithingPanel,
            boolean enableStonecutterPanel,
            boolean enableAnvilPanel
    ) implements IExtendedTerminalConfig { }

    public static interface IExtendedTerminalConfig extends IETTerminalConfig {
        boolean enableCraftingPanel();
        boolean enableSmithingPanel();
        boolean enableStonecutterPanel();
        boolean enableAnvilPanel();
    }
}