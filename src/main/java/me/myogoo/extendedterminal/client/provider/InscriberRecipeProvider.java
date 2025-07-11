package me.myogoo.extendedterminal.client.provider;

import appeng.core.definitions.AEItems;
import appeng.datagen.providers.recipes.InscriberRecipes;
import appeng.recipes.handlers.InscriberProcessType;
import appeng.recipes.handlers.InscriberRecipeBuilder;
import com.blakebr0.extendedcrafting.init.ModItems;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.init.ETItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

public class InscriberRecipeProvider extends InscriberRecipes {
    public InscriberRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> recipeOutput) {
        InscriberRecipeBuilder.inscribe(AEItems.CALCULATION_PROCESSOR_PRINT, ETItems.PRINTED_COMPAT_PROCESSOR,1)
                .setTop(Ingredient.of(AEItems.ENGINEERING_PROCESSOR_PRINT))
                .setMode(InscriberProcessType.PRESS)
                .save(recipeOutput, ExtendedTerminal.makeId("inscriber/printed_integration_processor"));

        InscriberRecipeBuilder.inscribe(ModItems.LUMINESSENCE.get(), ETItems.COMPAT_PROCESSOR,1)
                .setTop(Ingredient.of(ETItems.PRINTED_COMPAT_PROCESSOR))
                .setBottom(Ingredient.of(AEItems.SILICON_PRINT))
                .setMode(InscriberProcessType.PRESS)
                .save(recipeOutput, ExtendedTerminal.makeId("inscriber/integration_processor"));
    }
}
