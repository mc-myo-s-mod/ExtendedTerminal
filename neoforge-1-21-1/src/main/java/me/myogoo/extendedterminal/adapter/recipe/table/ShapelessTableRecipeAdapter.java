package me.myogoo.extendedterminal.adapter.recipe.table;

import com.blakebr0.extendedcrafting.crafting.recipe.ShapelessTableRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapelessTableCraftingRecipe;
import me.myogoo.extendedterminal.api.adapter.recipe.table.IShapelessTableRecipeAdapter;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import net.byAqua3.avaritia.recipe.RecipeExtremeShapeless;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class ShapelessTableRecipeAdapter extends AbstractTableRecipeAdapter implements IShapelessTableRecipeAdapter {
    private final Recipe<?> recipe;
    private final int recipeTier;

    private ShapelessTableRecipeAdapter(Recipe<?> recipe, int tier) {
        this.recipe = recipe;
        this.recipeTier = tier;
    }

    public ShapelessTableRecipeAdapter(ShapelessTableRecipe recipe) { this(recipe, recipe.getTier()); }

    public ShapelessTableRecipeAdapter(ShapelessTableCraftingRecipe recipe) {
        this(recipe, recipe.getTier());
    }

    public ShapelessTableRecipeAdapter(RecipeExtremeShapeless recipe) { this(recipe, 4); }

    public ShapelessTableRecipeAdapter(ShapelessRecipe recipe) { this(recipe, 1); }

    public ShapelessTableRecipeAdapter(CraftingRecipe recipe) { this(recipe, 1); }

    @Override
    public int tier() {
        return this.recipeTier;
    }

    @Override
    public NonNullList<Ingredient> ensureFittedCraftingGrid() {
        var ingredients = recipe.getIngredients();
        NonNullList<Ingredient> expandedIngredients = NonNullList.withSize(gridSize(), Ingredient.EMPTY);
        for(int i = 0; i < ingredients.size(); i++) {
            expandedIngredients.set(i, ingredients.get(i));
        }
        return expandedIngredients;
    }

    @Override
    public <R extends Recipe<?>> R get() {
        return (R) this.recipe;
    }
}
