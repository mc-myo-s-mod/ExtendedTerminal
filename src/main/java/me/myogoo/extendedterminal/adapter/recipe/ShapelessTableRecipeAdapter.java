package me.myogoo.extendedterminal.adapter.recipe;

import com.blakebr0.extendedcrafting.crafting.recipe.ShapelessTableRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.EternalSingularityCraftRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.InfinityCatalystCraftRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapelessTableCraftingRecipe;
import me.myogoo.extendedterminal.api.adapter.recipe.IShapelessTableRecipeAdapter;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Optional;

public class ShapelessTableRecipeAdapter implements IShapelessTableRecipeAdapter {
    private final Recipe<?> recipe;
    private final int recipeTier;

    public ShapelessTableRecipeAdapter(Recipe<?> recipe, int tier) {
        this.recipe = recipe;
        this.recipeTier = tier;
    }

    //Extended Crafting
    public ShapelessTableRecipeAdapter(ShapelessTableRecipe recipe) { this(recipe, recipe.getTier()); }

    //ReAvaritia
    public ShapelessTableRecipeAdapter(ShapelessTableCraftingRecipe recipe) { this(recipe, recipe.getTier()); }
    public ShapelessTableRecipeAdapter(EternalSingularityCraftRecipe recipe) { this(recipe, recipe.getTier()); }
    public ShapelessTableRecipeAdapter(InfinityCatalystCraftRecipe recipe) { this(recipe, recipe.getTier()); }


    @Override
    public <R extends Recipe<?>> Optional<R> unwrap(Class<R> recipeClass) {
        if(recipeClass.isInstance(this.recipe)) {
            return Optional.of(recipeClass.cast(this.recipe));
        }
        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends Recipe<?>> R recipe() {
        return (R) this.recipe;
    }

    @Override
    public int tier() {
        return this.recipeTier;
    }
}
