package me.myogoo.extendedterminal.datagen.provider;

import appeng.core.definitions.AEItems;
import appeng.datagen.providers.recipes.InscriberRecipes;
import appeng.recipes.handlers.InscriberProcessType;
import appeng.recipes.handlers.InscriberRecipeBuilder;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.init.ETItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class InscriberRecipeProvider extends InscriberRecipes {
    public InscriberRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void buildRecipes(RecipeOutput recipeOutput) {
        InscriberRecipeBuilder.inscribe(ETItems.CHARGED_ENDER_PEARL, ETItems.PRINTED_COMPAT_PROCESSOR,1)
                .setTop(Ingredient.of(ETItems.COMPAT_PRESS))
                .setMode(InscriberProcessType.INSCRIBE)
                .save(recipeOutput, ExtendedTerminal.makeId("inscriber/printed_compat_processor"));

        InscriberRecipeBuilder.inscribe(Items.REDSTONE, ETItems.COMPAT_PROCESSOR,1)
                .setTop(Ingredient.of(ETItems.PRINTED_COMPAT_PROCESSOR))
                .setBottom(Ingredient.of(AEItems.SILICON_PRINT))
                .setMode(InscriberProcessType.PRESS)
                .save(recipeOutput, ExtendedTerminal.makeId("inscriber/compat_processor"));
    }
}
