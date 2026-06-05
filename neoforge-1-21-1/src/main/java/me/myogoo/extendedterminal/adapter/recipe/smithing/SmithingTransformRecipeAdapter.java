package me.myogoo.extendedterminal.adapter.recipe.smithing;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

public class SmithingTransformRecipeAdapter extends SmithingRecipeBaseAdapter {
    private final SmithingTransformRecipe recipe;
    public SmithingTransformRecipeAdapter(SmithingTransformRecipe recipe) {
        super(recipe);
        this.recipe = recipe;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        var list = NonNullList.withSize(3, Ingredient.EMPTY);
        list.set(0, this.recipe.template);
        list.set(1, this.recipe.base);
        list.set(2, this.recipe.addition);
        return list;
    }

    @Override
    public <R extends Recipe<?>> R get() {
        return null;
    }
}
