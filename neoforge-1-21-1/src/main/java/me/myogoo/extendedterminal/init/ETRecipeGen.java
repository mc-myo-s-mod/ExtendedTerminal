package me.myogoo.extendedterminal.init;

import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.event.RecipeManagerLoadingEvent;
import me.myogoo.extendedterminal.util.recipe.builder.ShapedTableRecipeBuilder;
import me.myogoo.myotus.api.MyotusAPI;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import me.myogoo.extendedterminal.api.annotation.ExtendedCrafting;

public class ETRecipeGen {
    @SubscribeEvent
    static void onRegisterRecipes(RecipeManagerLoadingEvent event) {
        if (!FMLEnvironment.production) {
            if (MyotusAPI.get().integrations().isLoaded(ExtendedCrafting.class)) {

                event.addRecipe(ShapedTableRecipeBuilder.shaped(ETItems.COMPAT_PROCESSOR, 1)
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
    }

}
