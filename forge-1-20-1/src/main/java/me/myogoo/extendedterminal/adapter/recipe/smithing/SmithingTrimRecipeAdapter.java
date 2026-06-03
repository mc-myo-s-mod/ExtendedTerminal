package me.myogoo.extendedterminal.adapter.recipe.smithing;

import me.myogoo.extendedterminal.mixin.SmithingTrimRecipeAccessor;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;

public class SmithingTrimRecipeAdapter extends SmithingRecipeBaseAdapter {
    private final SmithingTrimRecipe recipe;

    public SmithingTrimRecipeAdapter(SmithingTrimRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        var ingredients = NonNullList.withSize(3, Ingredient.EMPTY);
        var accessor = (SmithingTrimRecipeAccessor) this.recipe;
        ingredients.set(0, accessor.extendedterminal$getTemplate());
        ingredients.set(1, accessor.extendedterminal$getBase());
        ingredients.set(2, accessor.extendedterminal$getAddition());
        return ingredients;
    }
}
