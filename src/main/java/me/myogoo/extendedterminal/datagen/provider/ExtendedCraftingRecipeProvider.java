package me.myogoo.extendedterminal.datagen.provider;

import appeng.core.definitions.AEParts;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.datagen.externalItems.ECItems;
import me.myogoo.extendedterminal.init.ETItems;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.extendedterminal.util.mod.ModLoadHelper;
import me.myogoo.extendedterminal.util.recipe.ETShapedRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.Consumer;

public class ExtendedCraftingRecipeProvider extends RecipeProvider {
    public ExtendedCraftingRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeOutput) {
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition(ModLoadHelper.ECCrafting_ID))
                .addRecipe(ETShapedRecipeBuilder.shaped(ETParts.BASIC_TERMINAL_PART, 1)
                        .pattern("AB")
                        .pattern("C ")
                        .define('A', AEParts.CRAFTING_TERMINAL)
                        .define('B', Objects.requireNonNull(ECItems.BASIC_TABLE))
                        .define('C', ETItems.COMPAT_PROCESSOR)
                        .unlockedBy("has_crafting_terminal", has(AEParts.CRAFTING_TERMINAL))::save)
                        .build(recipeOutput, ExtendedTerminal.makeId("extended_crafting/basic_terminal"));
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition(ModLoadHelper.ECCrafting_ID))
                .addRecipe(ETShapedRecipeBuilder.shaped(ETParts.ADVANCED_TERMINAL_PART, 1)
                        .pattern("AB")
                        .pattern("C ")
                        .define('A', AEParts.CRAFTING_TERMINAL)
                        .define('B', Objects.requireNonNull(ECItems.ADVANCED_TABLE))
                        .define('C', ETItems.COMPAT_PROCESSOR)
                        .unlockedBy("has_crafting_terminal", has(AEParts.CRAFTING_TERMINAL))::save)
                        .build(recipeOutput, ExtendedTerminal.makeId("extended_crafting/advanced_terminal"));
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition(ModLoadHelper.ECCrafting_ID))
                .addRecipe(ETShapedRecipeBuilder.shaped(ETParts.ELITE_TERMINAL_PART,1)
                        .pattern("AB")
                        .pattern("C ")
                        .define('A', AEParts.CRAFTING_TERMINAL)
                        .define('B', Objects.requireNonNull(ECItems.ELITE_TABLE))
                        .define('C', ETItems.COMPAT_PROCESSOR)
                        .unlockedBy("has_crafting_terminal", has(AEParts.CRAFTING_TERMINAL))::save)
                        .build(recipeOutput, ExtendedTerminal.makeId("extended_crafting/elite_terminal"));
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition(ModLoadHelper.ECCrafting_ID))
                .addRecipe(ETShapedRecipeBuilder.shaped(ETParts.ULTIMATE_TERMINAL_PART, 1)
                        .pattern("AB")
                        .pattern("C ")
                        .define('A', AEParts.CRAFTING_TERMINAL)
                        .define('B', Objects.requireNonNull(ECItems.ULTIMATE_TABLE))
                        .define('C', ETItems.COMPAT_PROCESSOR)
                        .unlockedBy("has_crafting_terminal", has(AEParts.CRAFTING_TERMINAL))::save)
                .build(recipeOutput, ExtendedTerminal.makeId("extended_crafting/ultimate_terminal"));
    }

    @Override
    public String getName() {
        return "Extended Terminal - Extended Crafting Recipes";
    }

    private static ItemStack getItemStack(ResourceLocation id) {
        var item = ForgeRegistries.ITEMS.getValue(id);
        if(item == null) {
            throw new IllegalStateException("Failed to get item: " + id);
        }
        return item.getDefaultInstance();
    }
}
