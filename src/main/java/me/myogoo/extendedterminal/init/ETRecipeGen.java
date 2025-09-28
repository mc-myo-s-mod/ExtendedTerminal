package me.myogoo.extendedterminal.init;

import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import com.blakebr0.extendedcrafting.init.ModBlocks;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.config.ETConfig;
import me.myogoo.extendedterminal.event.RecipeManagerLoadingEvent;
import me.myogoo.extendedterminal.util.recipe.builder.ShapedTableRecipeBuilder;
import net.byAqua3.avaritia.loader.AvaritiaBlocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLEnvironment;

public class ETRecipeGen {
    @SubscribeEvent
    static void onRegisterRecipes(RecipeManagerLoadingEvent event) {
        if(!FMLEnvironment.production) {
            event.addRecipe(ShapedTableRecipeBuilder.shaped(ETItems.COMPAT_PROCESSOR,1)
                    .pattern(" A ")
                    .pattern("BCB")
                    .pattern(" D ")
                    .define('A', AEParts.PATTERN_PROVIDER)
                    .define('B', AEParts.QUARTZ_FIBER)
                    .define('C', AEItems.CERTUS_QUARTZ_CRYSTAL)
                    .define('D', AEItems.FLUIX_DUST)
                    .tier(2)
                    .buildEC(ExtendedTerminal.makeId("extended_crafting/compat_processor")));
        }
    }

    @ModAccessor.ExtendedCrafting
    private static void loadExCraftingRecipe(RecipeManagerLoadingEvent event) {
        if(ETConfig.BASIC_TERMINAL_CONFIG.enableTerminal()) event.addRecipe(ShapedTableRecipeBuilder.shaped(ETParts.BASIC_TERMINAL_PART, 1)
                .pattern("AB")
                .pattern("C ")
                .define('A', AEParts.CRAFTING_TERMINAL)
                .define('B', ModBlocks.BASIC_TABLE.get())
                .define('C', ETItems.COMPAT_PROCESSOR)
                .buildEC(ExtendedTerminal.makeId("extended_crafting/basic_terminal")));

        if(ETConfig.ADVANCED_TERMINAL_CONFIG.enableTerminal()) event.addRecipe(ShapedTableRecipeBuilder.shaped(ETParts.ADVANCED_TERMINAL_PART, 1)
                .pattern("AB")
                .pattern("C ")
                .define('A', AEParts.CRAFTING_TERMINAL)
                .define('B', ModBlocks.ADVANCED_TABLE.get())
                .define('C', ETItems.COMPAT_PROCESSOR)
                .buildEC(ExtendedTerminal.makeId("extended_crafting/advanced_terminal")));

        if(ETConfig.ELITE_TERMINAL_CONFIG.enableTerminal()) event.addRecipe(ShapedTableRecipeBuilder.shaped(ETParts.ELITE_TERMINAL_PART,1)
                .pattern("AB")
                .pattern("C ")
                .define('A', AEParts.CRAFTING_TERMINAL)
                .define('B', ModBlocks.ELITE_TABLE.get())
                .define('C', ETItems.COMPAT_PROCESSOR)
                .buildEC(ExtendedTerminal.makeId("extended_crafting/elite_terminal")));

        if(ETConfig.ULTIMATE_TERMINAL_CONFIG.enableTerminal()) event.addRecipe(ShapedTableRecipeBuilder.shaped(ETParts.ULTIMATE_TERMINAL_PART, 1)
                .pattern("AB")
                .pattern("C ")
                .define('A', AEParts.CRAFTING_TERMINAL)
                .define('B', ModBlocks.ULTIMATE_TABLE.get())
                .define('C', ETItems.COMPAT_PROCESSOR)
                .buildEC(ExtendedTerminal.makeId("extended_crafting/ultimate_terminal")));
    }

    @ModAccessor.ReAvaritia
    private static void loadAvaritiaRecipe(RecipeManagerLoadingEvent event) {
        if(ETConfig.SCULK_TERMINAL_CONFIG.enableTerminal()) event.addRecipe(ShapedTableRecipeBuilder.shaped(ETParts.SCULK_TERMINAL_PART,1)
                .pattern("AB")
                .pattern("C ")
                .define('A', AEParts.CRAFTING_TERMINAL)
                .define('B', committee.nova.mods.avaritia.init.registry.ModBlocks.sculk_crafting_table)
                .define('C', ETItems.COMPAT_PROCESSOR)
                .buildReAV(ExtendedTerminal.makeId("avaritia/sculk_terminal")));

        if(ETConfig.NETHER_TERMINAL_CONFIG.enableTerminal()) event.addRecipe(ShapedTableRecipeBuilder.shaped(ETParts.NETHER_TERMINAL_PART,1)
                .pattern("AB")
                .pattern("C ")
                .define('A', AEParts.CRAFTING_TERMINAL)
                .define('B', committee.nova.mods.avaritia.init.registry.ModBlocks.nether_crafting_table)
                .define('C', ETItems.COMPAT_PROCESSOR)
                .buildReAV(ExtendedTerminal.makeId("avaritia/nether_terminal")));

        if(ETConfig.END_TERMINAL_CONFIG.enableTerminal()) event.addRecipe(ShapedTableRecipeBuilder.shaped(ETParts.END_TERMINAL_PART,1)
                .pattern("AB")
                .pattern("C ")
                .define('A', AEParts.CRAFTING_TERMINAL)
                .define('B', committee.nova.mods.avaritia.init.registry.ModBlocks.end_crafting_table)
                .define('C', ETItems.COMPAT_PROCESSOR)
                .buildReAV(ExtendedTerminal.makeId("avaritia/end_terminal")));

        if(ETConfig.EXTREME_TERMINAL_CONFIG.enableTerminal()) event.addRecipe(ShapedTableRecipeBuilder.shaped(ETParts.EXTREME_TERMINAL_PART,1)
                .pattern("AB")
                .pattern("C ")
                .define('A', AEParts.CRAFTING_TERMINAL)
                .define('B', committee.nova.mods.avaritia.init.registry.ModBlocks.extreme_crafting_table)
                .define('C', ETItems.COMPAT_PROCESSOR)
                .buildReAV(ExtendedTerminal.makeId("avaritia/extreme_terminal")));
    }

    @ModAccessor.AvaritiaNeo
    private static void loadAvaritiaNeoRecipe(RecipeManagerLoadingEvent event) {
        if(ETConfig.NEO_EXTREME_TERMINAL_CONFIG.enableTerminal()) event.addRecipe(ShapedTableRecipeBuilder.shaped(ETParts.NEO_EXTREME_TERMINAL_PART, 1)
                .pattern("AB")
                .pattern("C ")
                .define('A', AEParts.CRAFTING_TERMINAL)
                .define('B', AvaritiaBlocks.EXTREME_CRAFTING_TABLE.get())
                .define('C', ETItems.COMPAT_PROCESSOR)
                .buildAVNeo(ExtendedTerminal.makeId("avaritia/neo_extreme_terminal")));
    }
}
