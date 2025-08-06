package me.myogoo.extendedterminal.api.adapter.recipe;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapelessTableRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.*;
import me.myogoo.extendedterminal.adapter.recipe.ShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.adapter.recipe.ShapelessTableRecipeAdapter;
import me.myogoo.extendedterminal.api.ModAccessor;

import net.minecraft.world.item.crafting.Recipe;

import java.util.Optional;

public interface ITableRecipeAdapter {
    <R extends Recipe<?>> Optional<R> unwrap(Class<R> recipeClass);
    <R extends Recipe<?>> R recipe();
    int tier();

    @ModAccessor.ExtendedCrafting
    static ITableRecipeAdapter of(ITableRecipe recipe) {
        if (recipe instanceof ShapedTableRecipe shaped) {
            return new ShapedTableRecipeAdapter(shaped);
        } else if (recipe instanceof ShapelessTableRecipe shapeless) {
            return new ShapelessTableRecipeAdapter(shapeless);
        }
        throw new IllegalArgumentException("Unknown ITableRecipe implementation: " + recipe.getClass().getName());
    }

    @ModAccessor.ReAvaritia
    static ITableRecipeAdapter of(BaseTableCraftingRecipe recipe) {
        if (recipe instanceof ShapedTableCraftingRecipe shaped) {
            return new ShapedTableRecipeAdapter(shaped);
        } else if (recipe instanceof ShapelessTableCraftingRecipe shapeless) {
            return new ShapelessTableRecipeAdapter(shapeless);
        } else if (recipe instanceof EternalSingularityCraftRecipe singularityRecipe) {
            return new ShapelessTableRecipeAdapter(singularityRecipe);
        } else if (recipe instanceof InfinityCatalystCraftRecipe infinityRecipe) {
            return new ShapelessTableRecipeAdapter(infinityRecipe);
        }
        throw new IllegalArgumentException("Unknown BaseTableCraftingRecipe implementation: " + recipe.getClass().getName());
    }


}
