package me.myogoo.extendedterminal.config;

import appeng.core.definitions.ItemDefinition;
import com.blakebr0.cucumber.config.ModConfigs;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.config.IETTerminalConfig;
import me.myogoo.extendedterminal.init.ETParts;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.Map;

@Mod.EventBusSubscriber(modid = ExtendedTerminal.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ETConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec COMMON;


    // Extended Crafting
    //basic crafting terminal
    private static final ForgeConfigSpec.BooleanValue ENABLE_BASIC_CRAFTING_TERMINAL;
    private static final ForgeConfigSpec.BooleanValue ENABLE_BASIC_CRAFT_ONLY_POWERED;
    private static final ForgeConfigSpec.DoubleValue BASIC_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE;
    // advanced crafting terminal
    private static final ForgeConfigSpec.BooleanValue ENABLE_ADVANCED_CRAFTING_TERMINAL;
    private static final ForgeConfigSpec.BooleanValue ENABLE_ADVANCED_CRAFT_ONLY_POWERED;
    private static final ForgeConfigSpec.DoubleValue ADVANCED_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE;
    // elite crafting terminal
    private static final ForgeConfigSpec.BooleanValue ENABLE_ELITE_CRAFTING_TERMINAL;
    private static final ForgeConfigSpec.BooleanValue ENABLE_ELITE_CRAFT_ONLY_POWERED;
    private static final ForgeConfigSpec.DoubleValue ELITE_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE;
    // ultimate crafting terminal
    private static final ForgeConfigSpec.BooleanValue ENABLE_ULTIMATE_CRAFTING_TERMINAL;
    private static final ForgeConfigSpec.BooleanValue ENABLE_ULTIMATE_CRAFT_ONLY_POWERED;
    private static final ForgeConfigSpec.DoubleValue ULTIMATE_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE;

    // Re:Avaritia
    // Sculk crafting terminal
    private static final ForgeConfigSpec.BooleanValue ENABLE_SCULK_CRAFTING_TERMINAL;
    private static final ForgeConfigSpec.BooleanValue ENABLE_SCULK_CRAFT_ONLY_POWERED;
    private static final ForgeConfigSpec.DoubleValue SCULK_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE;

    // nether crafting terminal
    private static final ForgeConfigSpec.BooleanValue ENABLE_NETHER_CRAFTING_TERMINAL;
    private static final ForgeConfigSpec.BooleanValue ENABLE_NETHER_CRAFT_ONLY_POWERED;
    private static final ForgeConfigSpec.DoubleValue NETHER_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE;

    // end crafting terminal
    private static final ForgeConfigSpec.BooleanValue ENABLE_END_CRAFTING_TERMINAL;
    private static final ForgeConfigSpec.BooleanValue ENABLE_END_CRAFT_ONLY_POWERED;
    private static final ForgeConfigSpec.DoubleValue END_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE;

    // Extreme crafting terminal
    private static final ForgeConfigSpec.BooleanValue ENABLE_EXTREME_CRAFTING_TERMINAL;
    private static final ForgeConfigSpec.BooleanValue ENABLE_EXTREME_CRAFT_ONLY_POWERED;
    private static final ForgeConfigSpec.DoubleValue EXTREME_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE;

    static {
        BUILDER.comment("setting for common").push("Common Settings");
        BUILDER.pop();
        BUILDER.comment("setting for Extended Crafting Terminal").push("Extended Crafting");
        BUILDER.comment("Basic Terminal").push("Basic Crafting Terminal");
        ENABLE_BASIC_CRAFTING_TERMINAL = BUILDER.comment("enable basic crafting terminal").define("Enable", true);
        ENABLE_BASIC_CRAFT_ONLY_POWERED = BUILDER.comment("The AE system must be online and powered to perform the crafting.").define("Craftable when Active", false);
        BASIC_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE = BUILDER.comment("basic crafting terminal drain passive AE").defineInRange("Passive drain AE", 1, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.comment("Advanced Terminal").push("Advanced Crafting Terminal");
        ENABLE_ADVANCED_CRAFTING_TERMINAL = BUILDER.comment("enable advanced crafting terminal").define("Enable", true);
        ENABLE_ADVANCED_CRAFT_ONLY_POWERED = BUILDER.comment("The AE system must be online and powered to perform the crafting.").define("Craftable when Active", false);
        ADVANCED_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE = BUILDER.comment("advanced crafting terminal drain passive AE").defineInRange("Passive drain AE", 1, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.comment("Elite Terminal").push("Elite Crafting Terminal");
        ENABLE_ELITE_CRAFTING_TERMINAL = BUILDER.comment("enable elite crafting terminal").define("Enable", true);
        ENABLE_ELITE_CRAFT_ONLY_POWERED = BUILDER.comment("The AE system must be online and powered to perform the crafting.").define("Craftable when Active", false);
        ELITE_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE = BUILDER.comment("elite crafting terminal drain passive AE").defineInRange("Passive drain AE", 1, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.comment("Ultimate Terminal").push("Ultimate Crafting Terminal");
        ENABLE_ULTIMATE_CRAFTING_TERMINAL = BUILDER.comment("enable ultimate crafting terminal").define("Enable", true);
        ENABLE_ULTIMATE_CRAFT_ONLY_POWERED = BUILDER.comment("The AE system must be online and powered to perform the crafting.").define("Craftable when Active", false);
        ULTIMATE_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE = BUILDER.comment("ultimate crafting terminal drain passive AE").defineInRange("Passive drain", 1, 0.0, Double.MAX_VALUE);
        BUILDER.pop(2);
        BUILDER.comment("setting for Re:Avaritia").push("Re:Avaritia");
        BUILDER.comment("Sculk Terminal").push("Sculk Crafting Terminal");
        ENABLE_SCULK_CRAFTING_TERMINAL = BUILDER.comment("enable sculk crafting terminal").define("Enable", true);
        ENABLE_SCULK_CRAFT_ONLY_POWERED = BUILDER.comment("The AE system must be online and powered to perform the crafting.").define("Craftable when Active", false);
        SCULK_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE = BUILDER.comment("sculk crafting terminal drain passive AE").defineInRange("Passive drain AE", 1, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.comment("Nether Terminal").push("Nether Crafting Terminal");
        ENABLE_NETHER_CRAFTING_TERMINAL = BUILDER.comment("enable nether crafting terminal").define("Enable", true);
        ENABLE_NETHER_CRAFT_ONLY_POWERED = BUILDER.comment("The AE system must be online and powered to perform the crafting.").define("Craftable when Active", false);
        NETHER_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE = BUILDER.comment("nether crafting terminal drain passive AE").defineInRange("Passive drain AE", 1, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.comment("End Terminal").push("End Crafting Terminal");
        ENABLE_END_CRAFTING_TERMINAL = BUILDER.comment("enable end crafting terminal").define("Enable", true);
        ENABLE_END_CRAFT_ONLY_POWERED = BUILDER.comment("The AE system must be online and powered to perform the crafting.").define("Craftable when Active", false);
        END_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE = BUILDER.comment("end crafting terminal drain passive AE").defineInRange("Passive drain AE", 1, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.comment("Extreme Terminal").push("Extreme Crafting Terminal");
        ENABLE_EXTREME_CRAFTING_TERMINAL = BUILDER.comment("enable Extreme crafting terminal").define("Enable", true);
        ENABLE_EXTREME_CRAFT_ONLY_POWERED = BUILDER.comment("The AE system must be online and powered to perform the crafting.").define("Craftable when Active", false);
        EXTREME_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE = BUILDER.comment("Extreme crafting terminal drain passive AE").defineInRange("Passive drain AE", 1, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        COMMON = BUILDER.build();
    }

    public static void init() {
        BASIC_TERMINAL_CONFIG = new ExtendedCraftingConfig(
                ENABLE_BASIC_CRAFTING_TERMINAL.get(),
                ENABLE_BASIC_CRAFT_ONLY_POWERED.get(),
                BASIC_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE.get()
        );

        ADVANCED_TERMINAL_CONFIG = new ExtendedCraftingConfig(
                ENABLE_ADVANCED_CRAFTING_TERMINAL.get(),
                ENABLE_ADVANCED_CRAFT_ONLY_POWERED.get(),
                ADVANCED_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE.get()
        );

        ELITE_TERMINAL_CONFIG = new ExtendedCraftingConfig(
                ENABLE_ELITE_CRAFTING_TERMINAL.get(),
                ENABLE_ELITE_CRAFT_ONLY_POWERED.get(),
                ELITE_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE.get()
        );

        ULTIMATE_TERMINAL_CONFIG = new ExtendedCraftingConfig(
                ENABLE_ULTIMATE_CRAFTING_TERMINAL.get(),
                ENABLE_ULTIMATE_CRAFT_ONLY_POWERED.get(),
                ULTIMATE_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE.get()
        );

        // Avaritia Configs
        SCULK_TERMINAL_CONFIG = new AvaritiaConfig(
                ENABLE_SCULK_CRAFTING_TERMINAL.get(),
                ENABLE_SCULK_CRAFT_ONLY_POWERED.get(),
                SCULK_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE.get()
        );

        NETHER_TERMINAL_CONFIG = new AvaritiaConfig(
                ENABLE_NETHER_CRAFTING_TERMINAL.get(),
                ENABLE_NETHER_CRAFT_ONLY_POWERED.get(),
                NETHER_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE.get()
        );

        END_TERMINAL_CONFIG = new AvaritiaConfig(
                ENABLE_END_CRAFTING_TERMINAL.get(),
                ENABLE_END_CRAFT_ONLY_POWERED.get(),
                END_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE.get()
        );

        EXTREME_TERMINAL_CONFIG = new AvaritiaConfig(
                ENABLE_EXTREME_CRAFTING_TERMINAL.get(),
                ENABLE_EXTREME_CRAFT_ONLY_POWERED.get(),
                EXTREME_CRAFTING_TERMINAL_PASSIVE_DRAIN_AE.get()
        );
    }

    public static ExtendedCraftingConfig BASIC_TERMINAL_CONFIG;
    public static ExtendedCraftingConfig ADVANCED_TERMINAL_CONFIG;
    public static ExtendedCraftingConfig ELITE_TERMINAL_CONFIG;
    public static ExtendedCraftingConfig ULTIMATE_TERMINAL_CONFIG;

    public static AvaritiaConfig SCULK_TERMINAL_CONFIG;
    public static AvaritiaConfig NETHER_TERMINAL_CONFIG;
    public static AvaritiaConfig END_TERMINAL_CONFIG;
    public static AvaritiaConfig EXTREME_TERMINAL_CONFIG;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == COMMON) {
            init();
        }
    }


    public record ExtendedCraftingConfig(
            boolean enableTerminal,
            boolean enableCraftOnlyPowered,
            double passiveDrainAE
    ) implements IETTerminalConfig {}

    public record AvaritiaConfig(
            boolean enableTerminal,
            boolean enableCraftOnlyPowered,
            double passiveDrainAE
    ) implements IETTerminalConfig {}
}