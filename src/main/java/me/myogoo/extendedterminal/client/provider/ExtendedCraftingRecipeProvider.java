package me.myogoo.extendedterminal.client.provider;

import appeng.core.definitions.AEParts;
import com.blakebr0.extendedcrafting.init.ModBlocks;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.init.ETItems;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.extendedterminal.util.extendedcrafting.ShapedTableRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.function.Consumer;

public class ExtendedCraftingRecipeProvider extends RecipeProvider {
    public ExtendedCraftingRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeOutput) {
        ShapedTableRecipeBuilder.shaped(ETParts.BASIC_TERMINAL_PART, 1)
                .pattern("AB")
                .pattern("C ")
                .define('A', AEParts.CRAFTING_TERMINAL)
                .define('B', ModBlocks.BASIC_TABLE.get())
                .define('C', ETItems.COMPAT_PROCESSOR)
                .save(recipeOutput, ExtendedTerminal.makeId("extended_crafting/basic_terminal"));

        ShapedTableRecipeBuilder.shaped(ETParts.ADVANCED_TERMINAL_PART, 1)
                .pattern("AB")
                .pattern("C ")
                .define('A', AEParts.CRAFTING_TERMINAL)
                .define('B', ModBlocks.ADVANCED_TABLE.get())
                .define('C', ETItems.COMPAT_PROCESSOR)
                .save(recipeOutput, ExtendedTerminal.makeId("extended_crafting/advanced_terminal"));

        ShapedTableRecipeBuilder.shaped(ETParts.ELITE_TERMINAL_PART,1)
                .pattern("AB")
                .pattern("C ")
                .define('A', AEParts.CRAFTING_TERMINAL)
                .define('B', ModBlocks.ELITE_TABLE.get())
                .define('C', ETItems.COMPAT_PROCESSOR)
                .save(recipeOutput, ExtendedTerminal.makeId("extended_crafting/elite_terminal"));

        ShapedTableRecipeBuilder.shaped(ETParts.ULTIMATE_TERMINAL_PART, 1)
                .pattern("AB")
                .pattern("C ")
                .define('A', AEParts.CRAFTING_TERMINAL)
                .define('B', ModBlocks.ULTIMATE_TABLE.get())
                .define('C', ETItems.COMPAT_PROCESSOR)
                .save(recipeOutput, ExtendedTerminal.makeId("extended_crafting/ultimate_terminal"));
    }
}
