package me.myogoo.extendedterminal.datagen.provider;

import appeng.core.definitions.AEParts;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.datagen.externalItems.ReAVItems;
import me.myogoo.extendedterminal.init.ETItems;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.extendedterminal.util.mod.ModLoadHelper;
import me.myogoo.extendedterminal.util.recipe.ETShapedRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;

import java.util.Objects;
import java.util.function.Consumer;

public class ReAvaritiaCraftingRecipeProvider extends RecipeProvider {
    public ReAvaritiaCraftingRecipeProvider(PackOutput output) {
        super(output);
    }
    protected void buildRecipes(Consumer<FinishedRecipe> recipeOutput) {
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition(ModLoadHelper.Avaritia_ID))
                .addRecipe(ETShapedRecipeBuilder.shaped(ETParts.SCULK_TERMINAL_PART,1)
                        .pattern("AB")
                        .pattern("C ")
                        .define('A', AEParts.CRAFTING_TERMINAL)
                        .define('B', Objects.requireNonNull(ReAVItems.SCULK_TABLE))
                        .define('C', ETItems.COMPAT_PROCESSOR)
                        .unlockedBy("has_crafting_terminal", has(AEParts.CRAFTING_TERMINAL))::save)
                .build(recipeOutput, ExtendedTerminal.makeId("avaritia/sculk_terminal"));
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition(ModLoadHelper.Avaritia_ID))
                .addRecipe(ETShapedRecipeBuilder.shaped(ETParts.NETHER_TERMINAL_PART,1)
                        .pattern("AB")
                        .pattern("C ")
                        .define('A', AEParts.CRAFTING_TERMINAL)
                        .define('B', Objects.requireNonNull(ReAVItems.NETHER_TABLE))
                        .define('C', ETItems.COMPAT_PROCESSOR)
                        .unlockedBy("has_crafting_terminal", has(AEParts.CRAFTING_TERMINAL))::save)
                .build(recipeOutput, ExtendedTerminal.makeId("avaritia/nether_terminal"));
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition(ModLoadHelper.Avaritia_ID))
                .addRecipe(ETShapedRecipeBuilder.shaped(ETParts.END_TERMINAL_PART,1)
                        .pattern("AB")
                        .pattern("C ")
                        .define('A', AEParts.CRAFTING_TERMINAL)
                        .define('B', Objects.requireNonNull(ReAVItems.END_TABLE))
                        .define('C', ETItems.COMPAT_PROCESSOR)
                        .unlockedBy("has_crafting_terminal", has(AEParts.CRAFTING_TERMINAL))::save)
                .build(recipeOutput, ExtendedTerminal.makeId("avaritia/end_terminal"));
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition(ModLoadHelper.Avaritia_ID))
                .addRecipe(ETShapedRecipeBuilder.shaped(ETParts.EXTREME_TERMINAL_PART,1)
                        .pattern("AB")
                        .pattern("C ")
                        .define('A', AEParts.CRAFTING_TERMINAL)
                        .define('B', Objects.requireNonNull(ReAVItems.EXTREME_TABLE))
                        .define('C', ETItems.COMPAT_PROCESSOR)
                        .unlockedBy("has_crafting_terminal", has(AEParts.CRAFTING_TERMINAL))::save)
                .build(recipeOutput, ExtendedTerminal.makeId("avaritia/extreme_terminal"));
    }

    @Override
    public String getName() {
        return "Extended Terminal - Re:Avaritia Crafting Recipes";
    }
}
