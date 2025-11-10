package me.myogoo.extendedterminal.adapter.recipe.table;

import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapedTableCraftingRecipe;
import me.myogoo.extendedterminal.api.adapter.recipe.table.IShapedTableRecipeAdapter;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;

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

    public ShapedTableRecipeAdapter(net.byAqua3.avaritia.recipe.RecipeExtremeShaped recipe) {
        this(recipe, 4, recipe.getWidth(), recipe.getHeight());
    }

    public ShapedTableRecipeAdapter(ShapedRecipe recipe) {
        this(recipe, 1, recipe.getWidth(), recipe.getHeight());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends Recipe<?>> R recipe() {
        return (R) this.recipe;
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
