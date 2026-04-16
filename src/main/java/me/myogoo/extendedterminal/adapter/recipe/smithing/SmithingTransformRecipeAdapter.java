package me.myogoo.extendedterminal.adapter.recipe.smithing;

import me.myogoo.extendedterminal.mixin.SmithingTransformRecipeAccessor;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

public class SmithingTransformRecipeAdapter extends SmithingRecipeBaseAdapter {
    private final SmithingTransformRecipe recipe;

    public SmithingTransformRecipeAdapter(SmithingTransformRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        var ingredients = NonNullList.withSize(3, Ingredient.EMPTY);
        var accessor = (SmithingTransformRecipeAccessor) this.recipe;
        ingredients.set(0, accessor.extendedterminal$getTemplate());
        ingredients.set(1, accessor.extendedterminal$getBase());
        ingredients.set(2, accessor.extendedterminal$getAddition());
        return ingredients;
    }
}
