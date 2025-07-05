package com.myogoo.extendedterminal.client.provider;

import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import com.blakebr0.extendedcrafting.init.ModBlocks;
import com.blakebr0.extendedcrafting.init.ModItems;
import com.myogoo.extendedterminal.ExtendedTerminal;
import com.myogoo.extendedterminal.init.ETItems;
import com.myogoo.extendedterminal.init.ETParts;
import com.myogoo.extendedterminal.util.extendedcrafting.ShapedTableRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

import java.util.concurrent.CompletableFuture;

public class ExtendedCraftingRecipeProvider extends RecipeProvider {
    public ExtendedCraftingRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedTableRecipeBuilder.shaped(ETParts.BASIC_TERMINAL_PART, 1)
                .pattern("AB")
                .pattern("C ")
                .define('A', AEParts.CRAFTING_TERMINAL)
                .define('B', ModBlocks.BASIC_TABLE.get())
                .define('C', ETItems.INTEGRATION_PROCESSOR)
                .save(recipeOutput, ExtendedTerminal.makeId("extended_crafting/basic_terminal"));

        ShapedTableRecipeBuilder.shaped(ETParts.ADVANCED_TERMINAL_PART, 1)
                .pattern("AB")
                .pattern("C ")
                .define('A', ETParts.BASIC_TERMINAL_PART)
                .define('B', ModBlocks.ADVANCED_TABLE.get())
                .define('C', ETItems.INTEGRATION_PROCESSOR)
                .save(recipeOutput, ExtendedTerminal.makeId("extended_crafting/advanced_terminal"));

        ShapedTableRecipeBuilder.shaped(ETParts.ELITE_TERMINAL_PART,1)
                .pattern("AB")
                .pattern("C ")
                .define('A', ETParts.ADVANCED_TERMINAL_PART)
                .define('B', ModBlocks.ELITE_TABLE.get())
                .define('C', ETItems.INTEGRATION_PROCESSOR)
                .save(recipeOutput, ExtendedTerminal.makeId("extended_crafting/elite_terminal"));

        ShapedTableRecipeBuilder.shaped(ETParts.ULTIMATE_TERMINAL_PART, 1)
                .pattern("AB")
                .pattern("C ")
                .define('A', ETParts.ADVANCED_TERMINAL_PART)
                .define('B', ModBlocks.ULTIMATE_TABLE.get())
                .define('C', ETItems.INTEGRATION_PROCESSOR)
                .save(recipeOutput, ExtendedTerminal.makeId("extended_crafting/ultimate_terminal"));
    }
}
