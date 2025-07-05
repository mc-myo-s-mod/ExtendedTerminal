package com.myogoo.extendedterminal.client;

import com.myogoo.extendedterminal.ExtendedTerminal;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = ExtendedTerminal.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ETConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean enableBasicTerminal;
    public static boolean enableAdvancedTerminal;
    public static boolean enableEliteTerminal;
    public static boolean enableUltimateTerminal;

    static {
        BUILDER.comment("Extended Crafting");
        //ENABLE_BASIC_CRAFTING_TERMINAL = BUILDER.comment("enable basic crafting terminal").define("enableBasicCraftingTerminal", true);
        //ENABLE_ADVANCED_CRAFTING_TERMINAL = BUILDER.comment("enable advanced crafting terminal").define("enableAdvancedCraftingTerminal", true);
        //ENABLE_ELITE_CRAFTING_TERMINAL = BUILDER.comment("enable elite crafting terminal").define("enableEliteCraftingTerminal", true);
        //ENABLE_ULTIMATE_CRAFTING_TERMINAL = BUILDER.comment("enable ultimate crafting terminal").define("enableUltimateCraftingTerminal", true);
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {

    }
}