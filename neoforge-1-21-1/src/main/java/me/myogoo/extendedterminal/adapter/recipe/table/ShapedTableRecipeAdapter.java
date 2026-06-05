package me.myogoo.extendedterminal.adapter.recipe.table;

import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapedTableCraftingRecipe;
import me.myogoo.extendedterminal.api.adapter.recipe.table.IShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.Optional;

public class ShapedTableRecipeAdapter extends AbstractTableRecipeAdapter implements IShapedTableRecipeAdapter {
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
    public <R extends Recipe<?>> R get() {
        return (R) this.recipe;
    }

    @Override
    public int tier() {
        return recipeTier;
    }

    @Override
    public NonNullList<Ingredient> ensureFittedCraftingGrid() {
        var ingredients = recipe.getIngredients();
        NonNullList<Ingredient> expandedIngredients = NonNullList.withSize(height() * width(), Ingredient.EMPTY);

        for(int h = 0; h < height(); h++) {
            for(int w = 0; w < width(); w++) {
                int index = w + h * width();
                if(index < ingredients.size()) {
                    expandedIngredients.set(index, ingredients.get(index));
                } else {
                    expandedIngredients.set(index, Ingredient.EMPTY);
                }
            }
        }
        return  expandedIngredients;
    }

    @Override
    public int width() {
        return this.recipeWidth;
    }

    @Override
    public int height() {
        return this.recipeHeight;
    }
}
