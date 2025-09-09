package me.myogoo.extendedterminal.client.provider;

import appeng.core.definitions.AEParts;
import com.blakebr0.extendedcrafting.init.ModBlocks;
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

@ModAccessor.ExtendedCrafting
@DataGen
public class ExtendedCraftingRecipeProvider extends RecipeProvider {
    public ExtendedCraftingRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeOutput) {
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition("extendedcrafting"))
                .addRecipe(ETShapedRecipeBuilder.shaped(ETParts.BASIC_TERMINAL_PART, 1)
                        .pattern("AB")
                        .pattern("C ")
                        .define('A', AEParts.CRAFTING_TERMINAL)
                        .define('B', ModBlocks.BASIC_TABLE.get())
                        .define('C', ETItems.COMPAT_PROCESSOR)
                        .unlockedBy("has_crafting_terminal", has(AEParts.CRAFTING_TERMINAL))::save)
                        .build(recipeOutput, ExtendedTerminal.makeId("extended_crafting/basic_terminal"));
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition("extendedcrafting"))
                .addRecipe(ETShapedRecipeBuilder.shaped(ETParts.ADVANCED_TERMINAL_PART, 1)
                        .pattern("AB")
                        .pattern("C ")
                        .define('A', AEParts.CRAFTING_TERMINAL)
                        .define('B', ModBlocks.ADVANCED_TABLE.get())
                        .define('C', ETItems.COMPAT_PROCESSOR)
                        .unlockedBy("has_crafting_terminal", has(AEParts.CRAFTING_TERMINAL))::save)
                        .build(recipeOutput, ExtendedTerminal.makeId("extended_crafting/advanced_terminal"));
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition("extendedcrafting"))
                .addRecipe(ETShapedRecipeBuilder.shaped(ETParts.ELITE_TERMINAL_PART,1)
                        .pattern("AB")
                        .pattern("C ")
                        .define('A', AEParts.CRAFTING_TERMINAL)
                        .define('B', ModBlocks.ELITE_TABLE.get())
                        .define('C', ETItems.COMPAT_PROCESSOR)
                        .unlockedBy("has_crafting_terminal", has(AEParts.CRAFTING_TERMINAL))::save)
                        .build(recipeOutput, ExtendedTerminal.makeId("extended_crafting/elite_terminal"));
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition("extendedcrafting"))
                .addRecipe(ETShapedRecipeBuilder.shaped(ETParts.ULTIMATE_TERMINAL_PART, 1)
                        .pattern("AB")
                        .pattern("C ")
                        .define('A', AEParts.CRAFTING_TERMINAL)
                        .define('B', ModBlocks.ULTIMATE_TABLE.get())
                        .define('C', ETItems.COMPAT_PROCESSOR)
                        .unlockedBy("has_crafting_terminal", has(AEParts.CRAFTING_TERMINAL))::save)
                .build(recipeOutput, ExtendedTerminal.makeId("extended_crafting/ultimate_terminal"));
    }

    @Override
    public String getName() {
        return "Extended Terminal - Extended Crafting Recipes";
    }
}
