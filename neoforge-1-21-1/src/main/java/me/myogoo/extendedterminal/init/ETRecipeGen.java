package me.myogoo.extendedterminal.init;

import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.annotation.AvaritiaNeo;
import me.myogoo.extendedterminal.api.annotation.ExtendedCrafting;
import me.myogoo.extendedterminal.api.annotation.ReAvaritia;
import me.myogoo.extendedterminal.event.RecipeManagerLoadingEvent;
import me.myogoo.extendedterminal.util.recipe.builder.ShapedTableRecipeBuilder;
import me.myogoo.extendedterminal.util.recipe.builder.ShapelessTableRecipeBuilder;
import me.myogoo.myotus.api.MyotusAPI;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLEnvironment;

public class ETRecipeGen {
    @SubscribeEvent
    static void onRegisterRecipes(RecipeManagerLoadingEvent event) {
        if (FMLEnvironment.production) {
            return;
        }

        if (MyotusAPI.get().integrations().isLoaded(ExtendedCrafting.class)) {
            registerExtendedCraftingRecipes(event);
        }
        if (MyotusAPI.get().integrations().isLoaded(ReAvaritia.class)) {
            registerReAvaritiaRecipes(event);
        }
        if (MyotusAPI.get().integrations().isLoaded(AvaritiaNeo.class)) {
            registerAvaritiaNeoRecipes(event);
        }
    }

    private static void registerExtendedCraftingRecipes(RecipeManagerLoadingEvent event) {
        for (var recipe : DevRecipe.values()) {
            event.addRecipe(shaped(recipe).buildEC(ExtendedTerminal.makeId("dev/extendedcrafting/" + recipe.id() + "_shaped")));
            event.addRecipe(shapeless(recipe).buildEC(ExtendedTerminal.makeId("dev/extendedcrafting/" + recipe.id() + "_shapeless")));
        }
    }

    private static void registerReAvaritiaRecipes(RecipeManagerLoadingEvent event) {
        for (var recipe : DevRecipe.values()) {
            event.addRecipe(shaped(recipe).buildReAV(ExtendedTerminal.makeId("dev/re_avaritia/" + recipe.id() + "_shaped")));
            event.addRecipe(shapeless(recipe).buildReAV(ExtendedTerminal.makeId("dev/re_avaritia/" + recipe.id() + "_shapeless")));
        }
    }

    private static void registerAvaritiaNeoRecipes(RecipeManagerLoadingEvent event) {
        var recipe = DevRecipe.EXTREME;
        event.addRecipe(shaped(recipe).buildAVNeo(ExtendedTerminal.makeId("dev/avaritia_neo/" + recipe.id() + "_shaped")));
        event.addRecipe(shapeless(recipe).buildAVNeo(ExtendedTerminal.makeId("dev/avaritia_neo/" + recipe.id() + "_shapeless")));
    }

    private static ShapedTableRecipeBuilder shaped(DevRecipe recipe) {
        return ShapedTableRecipeBuilder.shaped(ETItems.COMPAT_PROCESSOR, 1)
                .pattern(recipe.pattern())
                .pattern("BCB")
                .pattern(" D ")
                .define('A', recipe.top())
                .define('B', AEParts.QUARTZ_FIBER)
                .define('C', AEItems.CERTUS_QUARTZ_CRYSTAL)
                .define('D', AEItems.FLUIX_DUST)
                .tier(recipe.tier());
    }

    private static ShapelessTableRecipeBuilder shapeless(DevRecipe recipe) {
        return ShapelessTableRecipeBuilder.shapeless(ETItems.COMPAT_PROCESSOR, 1)
                .requires(recipe.top())
                .requires(AEParts.QUARTZ_FIBER)
                .requires(AEParts.QUARTZ_FIBER)
                .requires(AEItems.CERTUS_QUARTZ_CRYSTAL)
                .requires(AEItems.FLUIX_DUST)
                .tier(recipe.tier());
    }

    private enum DevRecipe {
        BASIC("basic_recipe", 1, " A ", AEItems.CALCULATION_PROCESSOR),
        EPIC("epic_recipe", 2, "AAA", AEItems.ENGINEERING_PROCESSOR),
        ELITE("elite_recipe", 3, " A ", AEItems.LOGIC_PROCESSOR),
        ULTIMATE("ultimate_recipe", 4, "AAA", AEItems.SINGULARITY),
        EXTREME("extreme_recipe", 4, "AAA", AEItems.SINGULARITY);

        private final String id;
        private final int tier;
        private final String pattern;
        private final net.minecraft.world.level.ItemLike top;

        DevRecipe(String id, int tier, String pattern, net.minecraft.world.level.ItemLike top) {
            this.id = id;
            this.tier = tier;
            this.pattern = pattern;
            this.top = top;
        }

        String id() {
            return id;
        }

        int tier() {
            return tier;
        }

        String pattern() {
            return pattern;
        }

        net.minecraft.world.level.ItemLike top() {
            return top;
        }
    }
}
