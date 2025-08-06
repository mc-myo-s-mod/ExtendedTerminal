package me.myogoo.extendedterminal.adapter.recipe;

import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapedTableCraftingRecipe;
import me.myogoo.extendedterminal.api.adapter.recipe.IShapedTableRecipeAdapter;
import net.minecraft.world.item.crafting.Recipe;


import java.util.Optional;

public class ShapedTableRecipeAdapter implements IShapedTableRecipeAdapter {
    private final int recipeTier;
    private final int recipeWidth;
    private final int recipeHeight;
    private final Recipe<?> recipe;

    public ShapedTableRecipeAdapter(Recipe<?> recipe, int tier, int width, int height) {
        this.recipe = recipe;
        this.recipeTier = tier;
        this.recipeWidth = width;
        this.recipeHeight = height;
    }

    public ShapedTableRecipeAdapter(ShapedTableRecipe recipe) {
        this(recipe, recipe.getTier(), recipe.getWidth(), recipe.getHeight());
    }

    public ShapedTableRecipeAdapter(ShapedTableCraftingRecipe recipe) {
        this(recipe, recipe.getTier(), recipe.getWidth(), recipe.getHeight());
    }


    @Override
    @SuppressWarnings("unchecked")
    public <R extends Recipe<?>> R recipe() {
        return (R) this.recipe;
    }

    @Override
    public <R extends Recipe<?>> Optional<R> unwrap(Class<R> recipeClass)
    {
        if(recipeClass.isInstance(this.recipe)) {
            return Optional.of(recipeClass.cast(this.recipe));
        }
        return Optional.empty();
    }

    @Override
    public int tier() {
        return recipeTier;
    }

    @Override
    public int width() {
        return recipeWidth;
    }

    @Override
    public int height() {
        return recipeHeight;
    }
}
