package me.myogoo.extendedterminal.adapter.recipe.table;

import com.blakebr0.extendedcrafting.crafting.recipe.ShapelessTableRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapelessTableCraftingRecipe;
import me.myogoo.extendedterminal.api.adapter.recipe.table.IShapelessTableRecipeAdapter;
import net.byAqua3.avaritia.recipe.RecipeExtremeShapeless;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.Optional;

public class ShapelessTableRecipeAdapter implements IShapelessTableRecipeAdapter {
    private final Recipe<?> recipe;
    private final int recipeTier;

    public ShapelessTableRecipeAdapter(Recipe<?> recipe, int tier) {
        this.recipe = recipe;
        this.recipeTier = tier;
    }

    public ShapelessTableRecipeAdapter(ShapelessTableRecipe recipe) { this(recipe, recipe.getTier()); }

    public ShapelessTableRecipeAdapter(ShapelessTableCraftingRecipe recipe) {
        this(recipe, recipe.getTier());
    }

    public ShapelessTableRecipeAdapter(RecipeExtremeShapeless recipe) { this(recipe, 4); }

    public ShapelessTableRecipeAdapter(ShapelessRecipe recipe) { this(recipe, -1); }

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
