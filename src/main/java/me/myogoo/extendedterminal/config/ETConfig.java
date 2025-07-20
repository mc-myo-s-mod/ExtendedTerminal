package me.myogoo.extendedterminal.config;

import me.myogoo.extendedterminal.ExtendedTerminal;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = ExtendedTerminal.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ETConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec COMMON;

    // Extended Crafting
    //basic crafting terminal
    private static final ModConfigSpec.BooleanValue ENABLE_BASIC_CRAFTING_TERMINAL;
    private static final ModConfigSpec.DoubleValue BASIC_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE;
    // advanced crafting terminal
    private static final ModConfigSpec.BooleanValue ENABLE_ADVANCED_CRAFTING_TERMINAL;
    private static final ModConfigSpec.DoubleValue ADVANCED_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE;
    // elite crafting terminal
    private static final ModConfigSpec.BooleanValue ENABLE_ELITE_CRAFTING_TERMINAL;
    private static final ModConfigSpec.DoubleValue ELITE_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE;
    // ultimate crafting terminal
    private static final ModConfigSpec.BooleanValue ENABLE_ULTIMATE_CRAFTING_TERMINAL;
    private static final ModConfigSpec.DoubleValue ULTIMATE_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE;

    static {
        BUILDER.comment("setting for common").push("Common Settings");
        BUILDER.pop();
        BUILDER.comment("setting for Extended Crafting Terminal").push("Extended Crafting");
        BUILDER.comment("Basic Terminal").push("Basic Crafting Terminal");
        ENABLE_BASIC_CRAFTING_TERMINAL = BUILDER.comment("enable basic crafting terminal").define("Enable", true);
        BASIC_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE = BUILDER.comment("basic crafting terminal drain passive AE").defineInRange("Passive drain AE", 1, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.comment("Advanced Terminal").push("Advanced Crafting Terminal");
        ENABLE_ADVANCED_CRAFTING_TERMINAL = BUILDER.comment("enable advanced crafting terminal").define("Enable", true);
        ADVANCED_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE = BUILDER.comment("advanced crafting terminal drain passive AE").defineInRange("Passive drain AE", 1, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.comment("Elite Terminal").push("Elite Crafting Terminal");
        ENABLE_ELITE_CRAFTING_TERMINAL = BUILDER.comment("enable elite crafting terminal").define("Enable", true);
        ELITE_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE = BUILDER.comment("elite crafting terminal drain passive AE").defineInRange("Passive drain AE", 1, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.comment("Ultimate Terminal").push("Ultimate Crafting Terminal");
        ENABLE_ULTIMATE_CRAFTING_TERMINAL = BUILDER.comment("enable ultimate crafting terminal").define("Enable", true);
        ULTIMATE_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE = BUILDER.comment("ultimate crafting terminal drain passive AE").defineInRange("Passive drain", 1, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        COMMON = BUILDER.build();
    }

    public static ExtendedCraftingConfig BASIC_TERMINAL_CONFIG;
    public static ExtendedCraftingConfig ADVANCED_TERMINAL_CONFIG;
    public static ExtendedCraftingConfig ELITE_TERMINAL_CONFIG;
    public static ExtendedCraftingConfig ULTIMATE_TERMINAL_CONFIG;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if(event.getConfig().getSpec() == COMMON) {
            BASIC_TERMINAL_CONFIG = new ExtendedCraftingConfig(
                    ENABLE_BASIC_CRAFTING_TERMINAL.get(),
                    BASIC_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE.get()
            );

            ADVANCED_TERMINAL_CONFIG = new ExtendedCraftingConfig(
                    ENABLE_ADVANCED_CRAFTING_TERMINAL.get(),
                    ADVANCED_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE.get()
            );

            ELITE_TERMINAL_CONFIG = new ExtendedCraftingConfig(
                    ENABLE_ELITE_CRAFTING_TERMINAL.get(),
                    ELITE_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE.get()
            );

            ULTIMATE_TERMINAL_CONFIG = new ExtendedCraftingConfig(
                    ENABLE_ULTIMATE_CRAFTING_TERMINAL.get(),
                    ULTIMATE_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE.get()
            );
        }
    }


    public record ExtendedCraftingConfig(
            boolean enableTerminal,
            double passiveDrainAE
    ) {}
}