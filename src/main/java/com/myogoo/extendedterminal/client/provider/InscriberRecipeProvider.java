package com.myogoo.extendedterminal.client.provider;

import appeng.core.definitions.AEItems;
import appeng.datagen.providers.recipes.InscriberRecipes;
import appeng.recipes.handlers.InscriberProcessType;
import appeng.recipes.handlers.InscriberRecipeBuilder;
import com.blakebr0.extendedcrafting.init.ModItems;
import com.myogoo.extendedterminal.ExtendedTerminal;
import com.myogoo.extendedterminal.init.ETItems;
import com.sun.jdi.Type;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class InscriberRecipeProvider extends InscriberRecipes {
    public InscriberRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void buildRecipes(RecipeOutput recipeOutput) {
        InscriberRecipeBuilder.inscribe(AEItems.CALCULATION_PROCESSOR_PRINT, ETItems.PRINTED_INTEGRATION_PROCESSOR,1)
                .setTop(Ingredient.of(AEItems.ENGINEERING_PROCESSOR_PRINT))
                .setMode(InscriberProcessType.PRESS)
                .save(recipeOutput, ExtendedTerminal.makeId("inscriber/printed_integration_processor"));

        InscriberRecipeBuilder.inscribe(ModItems.REDSTONE_INGOT.get(), ETItems.INTEGRATION_PROCESSOR,1)
                .setTop(Ingredient.of(ETItems.PRINTED_INTEGRATION_PROCESSOR))
                .setBottom(Ingredient.of(AEItems.SILICON_PRINT))
                .setMode(InscriberProcessType.PRESS)
                .save(recipeOutput, ExtendedTerminal.makeId("inscriber/integration_processor"));
    }
}
