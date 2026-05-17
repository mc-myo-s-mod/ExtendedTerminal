package me.myogoo.extendedterminal.adapter.recipe;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapelessTableRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.EternalSingularityCraftRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ITierCraftingRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.InfinityCatalystCraftRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapedTableCraftingRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapelessTableCraftingRecipe;
import me.myogoo.extendedterminal.api.ModAccessor.AvaritiaNeo;
import me.myogoo.extendedterminal.api.ModAccessor.ExtendedCrafting;
import me.myogoo.extendedterminal.api.ModAccessor.ReAvaritia;
import me.myogoo.extendedterminal.api.adapter.recipe.ITableRecipeAdapter;
import net.byAqua3.avaritia.recipe.RecipeExtremeCrafting;
import net.byAqua3.avaritia.recipe.RecipeExtremeShaped;
import net.byAqua3.avaritia.recipe.RecipeExtremeShapeless;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public final class TableRecipeAdapters {
    private TableRecipeAdapters() {
    }

    public static ITableRecipeAdapter<?> of(CraftingRecipe recipe) {
        if (recipe instanceof ShapedRecipe shaped) {
            return new ShapedTableRecipeAdapter(shaped);
        } else if (recipe instanceof ShapelessRecipe shapeless) {
            return new ShapelessTableRecipeAdapter(shapeless);
        } else {
            return new ShapelessTableRecipeAdapter(recipe);
        }
    }

    @ExtendedCrafting
    public static ITableRecipeAdapter<?> of(ITableRecipe recipe) {
        if (recipe instanceof ShapedTableRecipe shaped) {
            return new ShapedTableRecipeAdapter(shaped);
        } else if (recipe instanceof ShapelessTableRecipe shapeless) {
            return new ShapelessTableRecipeAdapter(shapeless);
        }
        throw new IllegalArgumentException("Unknown ITableRecipe implementation: " + recipe.getClass().getName());
    }

    @ReAvaritia
    public static ITableRecipeAdapter<?> of(ITierCraftingRecipe recipe) {
        if (recipe instanceof ShapedTableCraftingRecipe shaped) {
            return new ShapedTableRecipeAdapter(shaped);
        } else if (recipe instanceof ShapelessTableCraftingRecipe shapeless) {
            return new ShapelessTableRecipeAdapter(shapeless);
        } else if (recipe instanceof EternalSingularityCraftRecipe singularityRecipe) {
            return new ShapelessTableRecipeAdapter(singularityRecipe);
        } else if (recipe instanceof InfinityCatalystCraftRecipe infinityRecipe) {
            return new ShapelessTableRecipeAdapter(infinityRecipe);
        }
        throw new IllegalArgumentException("Unknown ITierCraftingRecipe implementation: " + recipe.getClass().getName());
    }

    @AvaritiaNeo
    public static ITableRecipeAdapter<?> of(RecipeExtremeCrafting recipe) {
        if (recipe instanceof RecipeExtremeShaped shaped) {
            return new ShapedTableRecipeAdapter(shaped);
        } else if (recipe instanceof RecipeExtremeShapeless shapeless) {
            return new ShapelessTableRecipeAdapter(shapeless);
        }
        throw new IllegalArgumentException("Unknown RecipeExtremeCrafting implementation: " + recipe.getClass().getName());
    }
}
