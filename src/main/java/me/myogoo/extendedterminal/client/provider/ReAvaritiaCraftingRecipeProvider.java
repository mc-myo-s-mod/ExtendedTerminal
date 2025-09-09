package me.myogoo.extendedterminal.client.provider;

import appeng.core.definitions.AEParts;
import committee.nova.mods.avaritia.init.registry.ModBlocks;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.DataGen;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.init.ETItems;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.extendedterminal.util.recipe.ETShapedRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;

import java.util.function.Consumer;

@ModAccessor.ReAvaritia
@DataGen
public class ReAvaritiaCraftingRecipeProvider extends RecipeProvider {
    public ReAvaritiaCraftingRecipeProvider(PackOutput output) {
        super(output);
    }
    protected void buildRecipes(Consumer<FinishedRecipe> recipeOutput) {
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition("avaritia"))
                .addRecipe(ETShapedRecipeBuilder.shaped(ETParts.SCULK_TERMINAL_PART,1)
                        .pattern("AB")
                        .pattern("C ")
                        .define('A', AEParts.CRAFTING_TERMINAL)
                        .define('B', ModBlocks.sculk_crafting_table.get())
                        .define('C', ETItems.COMPAT_PROCESSOR)
                        .unlockedBy("has_crafting_terminal", has(AEParts.CRAFTING_TERMINAL))::save)
                .build(recipeOutput, ExtendedTerminal.makeId("avaritia/sculk_terminal"));
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition("avaritia"))
                .addRecipe(ETShapedRecipeBuilder.shaped(ETParts.NETHER_TERMINAL_PART,1)
                        .pattern("AB")
                        .pattern("C ")
                        .define('A', AEParts.CRAFTING_TERMINAL)
                        .define('B', ModBlocks.nether_crafting_table.get())
                        .define('C', ETItems.COMPAT_PROCESSOR)
                        .unlockedBy("has_crafting_terminal", has(AEParts.CRAFTING_TERMINAL))::save)
                .build(recipeOutput, ExtendedTerminal.makeId("avaritia/nether_terminal"));
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition("avaritia"))
                .addRecipe(ETShapedRecipeBuilder.shaped(ETParts.END_TERMINAL_PART,1)
                        .pattern("AB")
                        .pattern("C ")
                        .define('A', AEParts.CRAFTING_TERMINAL)
                        .define('B', ModBlocks.end_crafting_table.get())
                        .define('C', ETItems.COMPAT_PROCESSOR)
                        .unlockedBy("has_crafting_terminal", has(AEParts.CRAFTING_TERMINAL))::save)
                .build(recipeOutput, ExtendedTerminal.makeId("avaritia/end_terminal"));
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition("avaritia"))
                .addRecipe(ETShapedRecipeBuilder.shaped(ETParts.EXTREME_TERMINAL_PART,1)
                        .pattern("AB")
                        .pattern("C ")
                        .define('A', AEParts.CRAFTING_TERMINAL)
                        .define('B', ModBlocks.extreme_crafting_table.get())
                        .define('C', ETItems.COMPAT_PROCESSOR)
                        .unlockedBy("has_crafting_terminal", has(AEParts.CRAFTING_TERMINAL))::save)
                .build(recipeOutput, ExtendedTerminal.makeId("avaritia/extreme_terminal"));
    }

    @Override
    public String getName() {
        return "Extended Terminal - Re:Avaritia Crafting Recipes";
    }
}
