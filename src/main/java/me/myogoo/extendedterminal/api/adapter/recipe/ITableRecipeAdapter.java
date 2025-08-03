package me.myogoo.extendedterminal.api.adapter.recipe;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapelessTableRecipe;
import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapedTableCraftingRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapelessTableCraftingRecipe;
import me.myogoo.extendedterminal.adapter.recipe.ShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.adapter.recipe.ShapelessTableRecipeAdapter;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Optional;

public interface ITableRecipeAdapter {
    <R extends Recipe<?>> Optional<R> unwrap(Class<R> recipeClass);
    <R extends Recipe<?>> R recipe();
    int tier();

    static ITableRecipeAdapter of(ITableRecipe recipe) {
        if (recipe instanceof ShapedTableRecipe shaped) {
            return new ShapedTableRecipeAdapter(shaped);
        } else if (recipe instanceof ShapelessTableRecipe shapeless) {
            return new ShapelessTableRecipeAdapter(shapeless);
        }
        throw new IllegalArgumentException("Unknown ITableRecipe implementation: " + recipe.getClass().getName());
    }

    static ITableRecipeAdapter of(ITierCraftingRecipe recipe) {
        if (recipe instanceof ShapedTableCraftingRecipe shaped) {
            return new ShapedTableRecipeAdapter(shaped);
        } else if (recipe instanceof ShapelessTableCraftingRecipe shapeless) {
            return new ShapelessTableRecipeAdapter(shapeless);
        }
        throw new IllegalArgumentException("Unknown ITierCraftingRecipe implementation: " + recipe.getClass().getName());
    }
}
