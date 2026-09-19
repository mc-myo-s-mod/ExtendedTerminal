package me.myogoo.extendedterminal.adapter.recipe.table;

import com.blakebr0.extendedcrafting.crafting.recipe.ShapelessTableRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapelessTableCraftingRecipe;
import me.myogoo.extendedterminal.api.adapter.recipe.table.IShapelessTableRecipeAdapter;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.List;
import java.util.Optional;

public class ShapelessTableRecipeAdapter extends AbstractTableRecipeAdapter implements IShapelessTableRecipeAdapter {
    private ShapelessTableRecipeAdapter(Recipe<?> recipe, int tier, ResourceKey<Recipe<?>> recipeId) {
        super(recipe, tier, recipeId);
    }

    public ShapelessTableRecipeAdapter(ShapelessTableRecipe recipe, ResourceKey<Recipe<?>> recipeId) {
        this(recipe, recipe.getTier(), recipeId);
    }

    public ShapelessTableRecipeAdapter(ShapelessTableCraftingRecipe recipe, ResourceKey<Recipe<?>> recipeId) {
        this(recipe, recipe.getTier(), recipeId);
    }

    public ShapelessTableRecipeAdapter(ShapelessRecipe recipe, ResourceKey<Recipe<?>> recipeId) {
        this(recipe, 1, recipeId);
    }

    public ShapelessTableRecipeAdapter(CraftingRecipe recipe, ResourceKey<Recipe<?>> recipeId) {
        this(recipe, 1, recipeId);
    }


    @Override
    public List<Optional<Ingredient>> ensureFittedCraftingGrid() {
        var ingredients = recipeIngredients();
        NonNullList<Optional<Ingredient>> expandedIngredients = NonNullList.withSize(gridSize(), Optional.empty());
        for (int i = 0; i < ingredients.size(); i++) {
            expandedIngredients.set(i, ingredients.get(i));
        }
        return expandedIngredients;
    }


}
