package me.myogoo.extendedterminal.api.adapter.recipe.table;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapelessTableRecipe;
import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapedTableCraftingRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapelessTableCraftingRecipe;
import me.myogoo.extendedterminal.adapter.recipe.table.ShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.adapter.recipe.table.ShapelessTableRecipeAdapter;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.adapter.recipe.IRecipeAdapter;
import net.byAqua3.avaritia.recipe.RecipeExtremeCrafting;
import net.byAqua3.avaritia.recipe.RecipeExtremeShaped;
import net.byAqua3.avaritia.recipe.RecipeExtremeShapeless;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.Optional;

public interface ITableRecipeAdapter extends IRecipeAdapter {
    int tier();

    static ITableRecipeAdapter of(CraftingRecipe recipe) {
        if (recipe instanceof ShapedRecipe shaped) {
            return new ShapedTableRecipeAdapter(shaped);
        } else if (recipe instanceof ShapelessRecipe shapeless) {
            return new ShapelessTableRecipeAdapter(shapeless);
        }
        throw new IllegalArgumentException("Unknown CraftingRecipe implementation: " + recipe.getClass().getName());
    }

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
    static ITableRecipeAdapter of(ITierCraftingRecipe recipe) {
        if (recipe instanceof ShapedTableCraftingRecipe shaped) {
            return new ShapedTableRecipeAdapter(shaped);
        } else if (recipe instanceof ShapelessTableCraftingRecipe shapeless) {
            return new ShapelessTableRecipeAdapter(shapeless);
        }
        throw new IllegalArgumentException("Unknown ITierCraftingRecipe implementation: " + recipe.getClass().getName());
    }

    @ModAccessor.AvaritiaNeo
    static ITableRecipeAdapter of(RecipeExtremeCrafting recipe) {
        if (recipe instanceof RecipeExtremeShaped shaped) {
            return new ShapedTableRecipeAdapter(shaped);
        } else if (recipe instanceof RecipeExtremeShapeless shapeless) {
            return new ShapelessTableRecipeAdapter(shapeless);
        }
        throw new IllegalArgumentException("Unknown RecipeExtremeCrafting implementation: " + recipe.getClass().getName());
    }
}
